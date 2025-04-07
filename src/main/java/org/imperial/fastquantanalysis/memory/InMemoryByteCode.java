package org.imperial.fastquantanalysis.memory;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class InMemoryByteCode extends SimpleJavaFileObject {

    private ByteArrayOutputStream outputStream;

    public InMemoryByteCode(String className, Kind kind) {
        super(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    public byte[] getBytes() {
        return outputStream.toByteArray();
    }
}
