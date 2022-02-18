package Framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Framework.Framework class that loading plugins
 */
public class Framework {
    public static void main(String[] args) {
        String filePath = args.length > 0 ? args[0] : "../pride-and-prejudice.txt";

        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Choose your extract option: ExtratNormal; ExtractZWords");
        String extraction = scanner.nextLine();  // Read user input
        System.out.println("Choose your count option: CountNormalWords; CountStartLetter");
        String count = scanner.nextLine();
        System.out.println(extraction);
        System.out.println(count);

        Class cls = null;
        URL classUrl = null;
        try {
            // Find classes in the given jar file
            classUrl = new URL("file://home/runner/MSWE262P-JAVA/Week6/Deploy/...");
        } catch (Exception e){
            e.printStackTrace();
        }

        URL[] classUrls = {classUrl};
        URLClassLoader classLoader = new URLClassLoader(classUrls);
        try {
            cls = classLoader.loadClass(extraction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cls != null) {
            try {
                ExtractInterface ex = (ExtractInterface)
                ExtractInterface ei = (ExtractInterface) cls.newInstance();
                cls.extract(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Once we get the mix and match from user, we load plugin from deploy
        try {
            File configFile = new File("../deploy/config.properties");
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            // load the properties file:
            props.load(reader);

            String plugin1 = "";
            if (extraction.equals("ExtractNormal")) {
                plugin1 = props.getClass().getResource().getProperty("ExtractNormal");
            } else if (extraction.equals("ExtractZWords")) {
                plugin1 = props.getProperty("ExtractZWords");
            } else {
                System.out.println("Please choose from listed options");
            }

            String plugin2 = "";
            if (count.equals("CountNormalWords")) {
                plugin2 = props.getProperty("CountNormalWords");
            } else if (count.equals("CountStartLetter")) {
                plugin2 = props.getProperty("CountStartLetter");
            } else {
                System.out.println("Please choose from listed options");
            }

            System.out.println(extraction + plugin1 + count + plugin2);


        } catch (IOException e) {
            e.printStackTrace();
        }


        // print output, resList get the top25 from count plugin
//        List<Map.Entry<String, Integer>> resList = new ArrayList<>();
//        for (Map.Entry<String,Integer> entry : resList){
//            System.out.println(entry.getKey() + " - " + entry.getValue());
//        }

    }
}
