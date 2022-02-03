import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadJar {
    public static void main(String[] args){
        String jarFile = "json-20211205.jar";
//        if (args.length < 0){
//            jarFile = args[0];
//        } else {
//            System.out.println("Please input a correct file name.");
//        }

        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
        } catch (IOException e) {System.out.println(e);}

        List<String> classNames = findAllClassesInJar(jar);
        File file = new File(jarFile);
        ClassLoader cl = null;
        try {
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            cl = new URLClassLoader(urls);
        } catch (IOException e){
            System.out.println(e);
        }
        for (String className : classNames){
            loadAndTest(className, cl);
        }
    }

    // Find all classes in the given jar file
    private static List<String> findAllClassesInJar(JarFile jar) {
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

    private static void loadAndTest(String className, ClassLoader cl){

        // load class from giving class name
        Class c = null;
        try {
            c = cl.loadClass(className);
            System.out.println("---------------" + className + "---------------");
        } catch (ClassNotFoundException e) {
            System.out.println("No such class: " + className);
        }

        // counters
        int publicMethod = 0;
        int privateMethod = 0;
        int protectedMethod = 0;
        int staticMethod = 0;
        int fieldCounter = 0;

        if ( c != null) {
            // Get declared fields
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                fieldCounter++;
            }

            // Get declared methods
            Method[] methods = c.getDeclaredMethods();
            for (final Method m : methods) {
                // Get modifiers
                String modifier = Modifier.toString(m.getModifiers());
                if (modifier.contains("public")) {
                    publicMethod++;
                }
                if (modifier.contains("private")) {
                    privateMethod++;
                }
                if (modifier.contains("protected")) {
                    protectedMethod++;
                }
                if (modifier.contains("static")) {
                    staticMethod++;
                }
            }
            System.out.println("Public methods: " + publicMethod);
            System.out.println("Private methods: " + privateMethod);
            System.out.println("Protected methods: " + protectedMethod);
            System.out.println("Static methods: " + staticMethod);
            System.out.println("Fields: " + fieldCounter);
        }
    }
}

