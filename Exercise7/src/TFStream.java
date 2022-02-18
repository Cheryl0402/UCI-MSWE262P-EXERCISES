import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TFStream {
    public static void main(String[] args) throws IOException {
        // stop_words
        List<String> stopWords
                = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).split(","));

        // words List<String> words
        String filePath = "pride-and-prejudice.txt";
//        String filePath = args[0];
        List<String> words
                = Arrays.asList(new String(Files.readAllBytes(Paths.get(filePath))).toLowerCase()
                .replaceAll("[^a-zA-Z0-9]+", " ").split(" "));


        // Build word frequency map, sort and print top 25 words.
        words.stream()
                .filter(str -> !stopWords.contains(str) && str.length() > 1)
                .collect(Collectors.toMap(w -> w, w -> 1, Integer::sum))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .collect(Collectors.toList())
                .forEach((entry)-> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }
}
