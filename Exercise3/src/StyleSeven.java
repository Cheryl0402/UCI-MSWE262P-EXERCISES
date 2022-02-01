

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class StyleSeven {

    public static void main(String[] args) throws Exception {

        List<String> stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).split(","));
        List<String> words = Arrays.asList(new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase()
                .replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
        words.stream().filter(str -> !stopWords.contains(str) && str.length() > 1)
                .collect(Collectors.toMap(w -> w, w -> 1, Integer::sum))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .collect(Collectors.toList())
                .forEach((entry)-> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }

}

