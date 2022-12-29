package mindustry.mod;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class VelocityModClassLoader extends URLClassLoader{

    private static final Set<VelocityModClassLoader> loaders = new CopyOnWriteArraySet<>();

    static {
        // ClassLoader.registerAsParallelCapable();
    }

    public VelocityModClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addToClassloaders() {
        loaders.add(this);
    }

    @Override
    public void close() throws IOException{
        loaders.remove(this);
        super.close();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkOther)
    throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
            // Ignored: we'll try others
        }

        if (checkOther) {
            for (VelocityModClassLoader loader : loaders) {
                if (loader != this) {
                    try {
                        return loader.loadClass0(name, resolve, false);
                    } catch (ClassNotFoundException ignored) {
                        // We're trying others, safe to ignore
                    }
                }
            }
        }

        throw new ClassNotFoundException(name);
    }
}