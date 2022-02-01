import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Style4 {
    public static void  main(String[] args) throws Exception{
        // one piece of code that tries to do everything at the same time.
        // 1, process stop words
        List<String> stopWords = new ArrayList<>();
        try {
            FileReader fr = new FileReader("stop_words.txt");
            Scanner scan = new Scanner(fr);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] words = line.trim().split(",");
                for (String word : words) {
                    stopWords.add(word);
                }
            }
            //scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        }

        // 2, Use arraylist of arraylist to store the frequency data
        ArrayList<String> words = new ArrayList<>();
        ArrayList<Integer> frequency = new ArrayList<>();

        FileReader file = new FileReader(args[0]);
//        FileReader file = new FileReader("test_file.txt");
        Scanner scanner = new Scanner(file);

        while (scanner.hasNext()){
            String textLine = scanner.nextLine();
            System.out.println(textLine);

            // process each line
            String s = "";
            int charIndex = 0;
            while (charIndex < textLine.length()) {
                // process char
                char c = textLine.charAt(charIndex);
                // Build word char by char. Check if c is a letter or number
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    s = s + c;
                    charIndex++;           // the last word, charIndex ++ is larger than len, so it steps out of the while loop
                } else {  // now we have a word
                    // check empty and stop words
                    String str = s.toLowerCase();
                    System.out.println("str : " + str);
                    boolean exist = false;
                    if (!stopWords.contains(str) && str.length() >= 2){
                       // Check if str exists in words list
                        int fre = 1;
                        int freIndex = -1;
                        for (int i = 0; i < words.size(); i++) {
                            if (str.equals(words.get(i))) {
                                exist = true;
                                fre = frequency.get(i) + 1;
                                freIndex = i;
                            }
                        }
                         if (exist) {
                             frequency.set(freIndex, fre);
                             System.out.println("str in the list. set " + str + " frequency to "  + fre);
                         } else {
                             words.add(str);
                             frequency.add(1);
                             System.out.println("add " + str + ": 1 " );
                         }

                    }
                    // reset s to an empty string
                    s = "";
                    charIndex++;

                }
            }
            if (s.length() > 0) {
                String str = s.toLowerCase();
                System.out.println("str : " + str);
                boolean exist = false;
                if (!stopWords.contains(str) && str.length() >= 2){
                    // Check if str exists in words list
                    int fre = 1;
                    int freIndex = -1;
                    for (int i = 0; i < words.size(); i++) {
                        if (str.equals(words.get(i))) {
                            exist = true;
                            fre = frequency.get(i) + 1;
                            freIndex = i;
                        }
                    }
                    if (exist) {
                        frequency.set(freIndex, fre);
                        System.out.println("str in the list. set " + str + " frequency to "  + fre);
                    } else {
                        words.add(str);
                        frequency.add(1);
                        System.out.println("add " + str + ": 1 " );
                    }

                }

            }
        }

        for(int i = 0; i < words.size(); i++) {
            System.out.println("word: " + words.get(i) + ". frequency: " + frequency.get(i));
        }

        // sort frequency list and words list
        for ( int i = 0; i < frequency.size(); i++) {
            for (int j = i + 1; j < frequency.size(); j++) {
               if (frequency.get(i) < frequency.get(j)) {
                   // swap frequency at index i and j
                   int fre1= frequency.get(i);
                   int fre2 = frequency.get(j);
                   frequency.set(i, fre2);
                   frequency.set(j, fre1);

                   // swap word at index i and j
                   String str1 = words.get(i);
                   String str2 = words.get(j);
                   words.set(i, str2);
                   words.set(j, str1);
               }

            }
        }

        // print the top 25
        System.out.println("\n----------Style #4 Word frequency (top 25) -----------");
        for ( int i = 0; i < 25; i++) {
            System.out.println(words.get(i) + " - " + frequency.get(i));
        }

    }

}
