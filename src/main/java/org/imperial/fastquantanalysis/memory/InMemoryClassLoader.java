package org.imperial.fastquantanalysis.memory;

import java.util.HashMap;
import java.util.Map;

public class InMemoryClassLoader extends ClassLoader {
    private final Map<String, byte[]> classes = new HashMap<>();

    public void addClass(String name, byte[] bytes) {
        classes.put(name, bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classes.get(name);
        if (bytes == null) {
            return super.findClass(name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}