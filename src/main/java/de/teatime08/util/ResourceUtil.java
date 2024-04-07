package de.teatime08.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtil {
    /**
     * Loads one or multiple file contents from a packages jar file, from classpath or from a native file.
     * Works for files inside the classpath.
     * @param path the path for the resource to load, under /resources/ then your file name e.g. /text.txt would be enough.
     * @param cl the current classloader, can be from any class, as long as in path
     * @return a list of file contents read by this method.
     */
    public static byte[] loadResource(String path, Class cl) throws IOException {
        path = removeFirstPathFileSeperator(path);
        InputStream resourceStream = cl.getClassLoader().getResourceAsStream(path);
        if (resourceStream == null)
            throw new IllegalArgumentException("Cannot load resource at path: " + path);
        byte[] bytes = new byte[resourceStream.available()];
        DataInputStream dataInputStream = new DataInputStream(resourceStream);
        dataInputStream.readFully(bytes);
        return bytes;
    }

    private static String removeFirstPathFileSeperator(String path) {
        while (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
        }
        return path;
    }
}
