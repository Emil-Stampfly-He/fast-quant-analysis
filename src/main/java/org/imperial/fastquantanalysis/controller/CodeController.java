package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.service.ICodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Code controller
 *
 * @author Emil S. He
 * @since  2025-04-08
 */
@Slf4j
@RestController
@RequestMapping("/code")
@Tag(name = "Customized Strategy Code Uploading Interface")
public class CodeController {

    @Resource
    private ICodeService codeService;

    /**
     * Get customized Java code snippet from frontend and run
     * @param code Customized code snippet
     * @return OK or fail message
     * @postmantest untested
     * TODO Has BUG
     */
    @PostMapping("/java")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get customized code snippet and run")
    public ResponseEntity<?> runJavaCode(@RequestBody String code) {
        return codeService.runJavaCode(code);
    }

    /**
     * Get customized Kotlin code snippet from frontend and run
     * @param code Customized code snippet
     * @return OK or fail message
     * @postmantest untested
     */
    @PostMapping("/kotlin")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get customized code snippet and run")
    public ResponseEntity<?> runKotlinCode(@RequestBody String code) {
        return codeService.runKotlinCode(code);
    }

    // TODO Add runPythonCode method
}
