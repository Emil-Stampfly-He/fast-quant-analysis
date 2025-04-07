package org.imperial.fastquantanalysis.memory;

import lombok.Getter;

import javax.tools.*;
import java.io.IOException;

@Getter
public class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private InMemoryByteCode byteCode;

    public ClassFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className,
                                               JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        byteCode = new InMemoryByteCode(className, kind);
        return byteCode;
    }

    public InMemoryByteCode getByteCode() {
        return byteCode;
    }
}
