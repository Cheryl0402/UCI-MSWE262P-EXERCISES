import javax.imageio.IIOException;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ThirtyTwo {
    // Read file and return a data String
    public static String readFile(String filePath) {
        String dataStr = null;
        try {
            dataStr = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e){
            e.printStackTrace();
        }
        return dataStr;
    }

    // Takes into a string and return chunks of lines
    public static List<String> partition(String dataStr, int numLines) {
        List<String> chunkList = new ArrayList<>();
        List<String> lines = Arrays.asList(dataStr.split("\n"));
        StringBuilder sb =  new StringBuilder();
        int count = 0;
        while (count < lines.size()) {
            String line = lines.get(count);
            sb.append(line).append("\n");
            count++;
            if (count % 200 == 0) {
                chunkList.add(sb.toString().toLowerCase());
                sb = new StringBuilder();
            }
        }
        // Leftover lines
        chunkList.add(sb.toString().toLowerCase());
        return chunkList;
    }

    /**
     * Take a chunk of lines which is a string, returns a list of pairs(word, 1)
     * one for each word in the input
     */
    static class WordSpliter implements Function<String, List<Object[]>> {
        public List<String> scan(String str) {
            List<String> wordList;
            wordList =Arrays.asList( str.toLowerCase().replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
            return wordList;
        }
        public List<String> removeStopWords(List<String> wordList) {
            List<String> stopWords = null;
            try {
                 stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).toLowerCase().split(","));
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> targetedWords = new ArrayList<>();
            for (String word : wordList) {
                if (!stopWords.contains(word) && word.length() > 1) {
                    targetedWords.add(word);
                }
            }
            return targetedWords;
        }

        // The actual work of the mapper
        public List<Object[]> apply(String str) {
            List<Object[]> result = new ArrayList<>();
            List<String> filteredWords = removeStopWords(scan(str));
            for (String word : filteredWords) {
                result.add(new Object[]{word, 1});
            }
            return result;
        }
    }

    // Takes a list of lists of pairs of the form and returns a dictionary mapping each unique word
    // to the corresponding list of pairs
    public static HashMap<String, List<Object[]>> regroup(List<List<Object[]>> pairList){
        // { w1 : [(w1, 1), (w1, 1)...],
        //      w2 : [(w2, 1), (w2, 1)...],
        //      ...}
        HashMap<String, List<Object[]>> mapping = new HashMap<>();
        for (List<Object[]> pairs : pairList) {
            for (Object[] pair : pairs) {
                String word = (String) pair[0];
                if (mapping.containsKey(word)){
                    mapping.get(word).add(pair);
                } else {
                    List<Object[]> pairVal = new ArrayList<>();
                    pairVal.add(pair);
                    mapping.put(word, pairVal);
                }
            }
        }
        return mapping;
    }

// """
//    Takes a mapping of the form (word, [(word, 1), (word, 1)...)])
//    and returns a pair (word, frequency), where frequency is the
//    sum of all the reported occurrences
//    """
    public static HashMap<String, Integer> CountWords(HashMap<String, List<Object[]>> mapping) {
        HashMap<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, List<Object[]>> entry : mapping.entrySet()){
            Object[] reducedVal = entry.getValue().stream()
                    .reduce(new Object[]{"", 0}, (fre1, fre2) -> new Object[]{"", (Integer)fre1[1] + (Integer) fre2[1]});
            result.put(entry.getKey(),(Integer) reducedVal[1]);
        }
        return result;
    }

    public static List<Map.Entry<String, Integer>> sort(HashMap<String, Integer> freMap) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(freMap.entrySet());
        Collections.sort(list, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return list;
    }

    public static void main(String[] args) {
        String filePath = "pride-and-prejudice.txt";
        WordSpliter wordSpliter = new WordSpliter();
        List<List<Object[]>> splits = partition(readFile(filePath), 200).stream()
                .map(wordSpliter).collect(Collectors.toList());
        HashMap<String, List<Object[]>> splitsPerWord = regroup(splits);
        List<Map.Entry<String, Integer>> sortedMap = sort(CountWords(splitsPerWord));
        for (int i = 0; i < 25; i++) {
            System.out.println(sortedMap.get(i).getKey() + " - " + sortedMap.get(i).getValue());
        }
    }
}