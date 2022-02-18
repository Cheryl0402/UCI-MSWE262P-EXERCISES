package ExtractZWords;

import Framework.ExtractInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtractZWords implements ExtractInterface {
    public List<String> extract(String filePath){
        List<String> words;
        List<String> wordList = new ArrayList();
        try{
            words
                    = Arrays.asList(new String(Files.readAllBytes(Paths.get("pride-and-prejudice.txt"))).toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
            List<String> stopWords
                    = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).split(","));
            for (String word : words) {
                if (!stopWords.contains(word) && word.length() > 1) {
                    if (word.contains("z")) {
                        wordList.add(word);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return wordList;
    }
}