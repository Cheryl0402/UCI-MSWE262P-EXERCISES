import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadJar {
    public static void main(String[] args){
        String jarFile = "";
        if (args.length < 0){
            jarFile = args[0];
        } else {
            System.out.println("Please input a correct file name.");
        }

        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
        } catch (IOException e) {System.out.println(e);}

        findAllClassesInJar1(jar);

    }

    // Read files from jarfile
    private static void findAllClassesInJar1(JarFile jar) {
        Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();
            System.out.println(entry.getName());
        }
    }

    private static List<String> findAllClassesInJar2(JarFile jar) {
        Stream<JarEntry> stream = jar.stream();

        return stream
                .filter(entry -> entry.getName().endsWith(".class"))
                .map(entry -> getFQN(entry.getName()))
                .sorted()
                .collect(Collectors.toList());
    }

    private static String getFQN(String resourceName) {
        return resourceName.replaceAll("/", ".").substring(0, resourceName.lastIndexOf('.'));
    }
}
