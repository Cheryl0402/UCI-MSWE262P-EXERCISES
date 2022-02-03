import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
}
