package org.imperial.fastquantanalysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.constant.StrategyName;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.imperial.fastquantanalysis.exception.StrategyRunningException;
import org.imperial.fastquantanalysis.mapper.CodeMapper;
import org.imperial.fastquantanalysis.memory.InMemoryClassLoader;
import org.imperial.fastquantanalysis.memory.InMemoryJavaFileObject;
import org.imperial.fastquantanalysis.service.ICodeService;
import org.imperial.fastquantanalysis.util.RedisIdUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.cli.common.ExitCode;
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Code service implementation class
 *
 * @author Emil S. He
 * @since 2025-04-08
 */
@Slf4j
@Service
public class CodeServiceImpl extends ServiceImpl<CodeMapper, QuantStrategy> implements ICodeService {

    @Resource
    private RedisIdUtil redisIdUtil;

    /**
     * Get customized Java code snippet from frontend and run
     * @param code Customized code snippet
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> runJavaCode(String code) {
        try {
            Class<?> strategyClass = compileJavaSource(code);
            return getResponseEntity(strategyClass);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    /**
     * Get customized Kotlin code snippet from frontend and run
     * @param code Customized code snippet
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> runKotlinCode(String code) {
        try {
            Class<?> strategyClass = compileKotlinSource(code);
            return getResponseEntity(strategyClass);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    /**
     * Helper method
     * @param strategyClass Strategy class byte code
     * @return OK or fail message
     * @throws InstantiationException Instantiation exception
     * @throws IllegalAccessException Illegal access exception
     * @throws java.lang.reflect.InvocationTargetException Invocation target exception
     * @throws NoSuchMethodException No such method exception
     * @throws StrategyRunningException Strategy running exception
     */
    @NotNull
    private ResponseEntity<?> getResponseEntity(Class<?> strategyClass)
            throws InstantiationException, IllegalAccessException,
            java.lang.reflect.InvocationTargetException, NoSuchMethodException,
            StrategyRunningException {
        Object instance = strategyClass.getDeclaredConstructor().newInstance();

        List<Method> strategyMethods = Arrays.stream(strategyClass.getDeclaredMethods())
                .filter(m -> m.getName().equals("runStrategy"))
                .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(QuantStrategy.class))
                .filter(m -> m.getReturnType().equals(QuantStrategy.class))
                .toList();

        if (strategyMethods.isEmpty()) {
            return ResponseEntity.badRequest().body("Did not find \"runStrategy\" method.");
        }

        QuantStrategy originalStrategy = new QuantStrategy();
        originalStrategy.setStrategyId(redisIdUtil.nextId(StrategyName.CUSTOMIZED_STRATEGY));
        originalStrategy.setStrategyName(StrategyName.CUSTOMIZED_STRATEGY);
        originalStrategy.setStartDate(LocalDateTime.now());
        originalStrategy.setEndDate(LocalDateTime.now());

        QuantStrategy finalStrategy = originalStrategy;
        for (Method m : strategyMethods) {
            QuantStrategy temp = (QuantStrategy) m.invoke(instance, finalStrategy);
            if (temp != null) {
                finalStrategy = mergeStrategy(finalStrategy, temp);
            }
        }

        fillMissingFields(finalStrategy);
        this.save(finalStrategy);

        return ResponseEntity.ok(finalStrategy);
    }

    private Class<?> compileJavaSource(String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("JDK compiler not available. Please check and refresh the page.");
        }

        InMemoryJavaFileObject fileObject = new InMemoryJavaFileObject("UserStrategy", sourceCode);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);

        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                standardFileManager,
                diagnostics,
                null,
                null,
                List.of(fileObject)
        );

        boolean success = task.call();
        if (!success) {
            StringBuilder errorMsg = new StringBuilder();
            diagnostics.getDiagnostics().forEach(d -> errorMsg.append(d.toString()).append("\n"));
            throw new IllegalArgumentException("Failed to compile: \n" + errorMsg);
        }

        InMemoryClassLoader classLoader = new InMemoryClassLoader();
        return classLoader.loadClass("UserStrategy");
    }

    private Class<?> compileKotlinSource(String sourceCode) throws Exception {
        Path tempDir = Files.createTempDirectory("kotlinCompile");
        Path sourceFile = tempDir.resolve("UserStrategy.kt");
        Files.writeString(sourceFile, sourceCode);

        // Build Kotlin compiler parameters: compile the current file,
        // -d specify the compilation output directory,
        // and set the classpath to the current JVM classpath.
        String[] args = new String[]{
                sourceFile.toAbsolutePath().toString(),
                "-d", tempDir.toAbsolutePath().toString(),
                "-classpath", System.getProperty("java.class.path")
        };

        K2JVMCompiler compiler = new K2JVMCompiler();
        ExitCode exitCode = compiler.exec(System.err, args);
        if (exitCode != ExitCode.OK) {
            throw new IllegalArgumentException("Kotlin compilation failed with exit code: " + exitCode);
        }

        URL[] urls = new URL[]{tempDir.toUri().toURL()};
        try (URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader())) {
            return classLoader.loadClass("UserStrategy");
        }
    }

    private QuantStrategy mergeStrategy(QuantStrategy original, QuantStrategy additional) {
        if (additional.getAnnualizedReturn() != null) {
            original.setAnnualizedReturn(additional.getAnnualizedReturn());
        }
        if (additional.getMaxDrawdown() != null) {
            original.setMaxDrawdown(additional.getMaxDrawdown());
        }
        if (additional.getCumulativeReturn() != null) {
            original.setCumulativeReturn(additional.getCumulativeReturn());
        }
        if (additional.getVolatility() != null) {
            original.setVolatility(additional.getVolatility());
        }
        if (additional.getSharpeRatio() != null) {
            original.setSharpeRatio(additional.getSharpeRatio());
        }
        if (additional.getTradeCount() != null) {
            original.setTradeCount(additional.getTradeCount());
        }
        return original;
    }

    private void fillMissingFields(QuantStrategy strategy) {
        if (strategy.getAnnualizedReturn() == null) {
            strategy.setAnnualizedReturn(0.0);
        }
        if (strategy.getMaxDrawdown() == null) {
            strategy.setMaxDrawdown(0.0);
        }
        if (strategy.getCumulativeReturn() == null) {
            strategy.setCumulativeReturn(0.0);
        }
        if (strategy.getVolatility() == null) {
            strategy.setVolatility(0.0);
        }
        if (strategy.getSharpeRatio() == null) {
            strategy.setSharpeRatio(0.0);
        }
        if (strategy.getTradeCount() == null) {
            strategy.setTradeCount(0);
        }
    }
}
