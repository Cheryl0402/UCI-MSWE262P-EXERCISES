

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class StyleEight {
    public static void  main(String[] args) throws Exception{
        // 1, process stop words
        List<String> stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).split(","));
        // 2, process testfile into list of words
        List<String> words = new ArrayList<>();
        Scanner scanner = new Scanner(new FileReader(args[0]));
//        Scanner scanner = new Scanner(new FileReader("test_file.txt"));
        while (scanner.hasNext()) {
            String l = scanner.nextLine();
            String line = l.toLowerCase();
            StringReader reader = new StringReader(line);
            parse(reader, words, stopWords);
        }
        scanner.close();

        // construct words frequency map
        HashMap<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }

//        List<Map.Entry<String, Integer>> sortedMap = sort(frequency);
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequency.entrySet());
        Collections.sort(list, (e1, e2) -> {return e2.getValue() - e1.getValue();});
        for (int i = 0; i < 25; i++){
            System.out.println(list.get(i).getKey() + " - " + list.get(i).getValue());
        }
    }

    // parsing a string with recursion
    private static void parse(StringReader reader, List<String> words, List<String> stopWords) throws Exception{
        int r;
        String s = "";
        while ((r = reader.read()) != -1) {
            char c = (char)r;
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9'){
                s = s + c;
            } else {
                if (!s.isEmpty() && !stopWords.contains(s) && s.length() > 1) {
                    words.add(s);
                    break;
                }
                s = "";
            }
        }
        if (r == -1) {
            if (!s.isEmpty() && !stopWords.contains(s) && s.length() > 1) {
                words.add(s);
            }
            return;
        }
        parse(reader, words, stopWords);
    }
}


