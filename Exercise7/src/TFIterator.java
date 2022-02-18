import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TFIterator {

    // Read lines from file
    static class LinesIterator implements Iterator<String> {
        private Scanner scanner;
        public LinesIterator(Scanner scanner){
            this.scanner = scanner;
        }
        public boolean hasNext() {
            return scanner.hasNextLine();
        }
        public String next() {
            return scanner.nextLine().toLowerCase();
        }
    }

    // Read words from line
    static class WordsIterator implements Iterator<List<String>> {
        private Iterator<String> prevIter;
        private List<String> filteredWords;

        public WordsIterator(Iterator<String>  lineIter){
            this.prevIter = lineIter;
        }

        public boolean hasNext() {
            return prevIter.hasNext();
        }

        public List<String> next() {
            filteredWords = new ArrayList<>();
            while (prevIter.hasNext()) {
                String line = prevIter.next();
                filteredWords = Arrays.asList(line.replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
                return filteredWords;
            }
            return filteredWords;
        }
    }

    // Filter stop words
    static class StopWordsFilterIterator implements Iterator<List<String>>{
        private Iterator<List<String>> prevIte;

        private List<String> stopWords;


        public StopWordsFilterIterator(Iterator<List<String>> wordIter){
            this.prevIte = wordIter;
            loadStopWords();
        }

        private void loadStopWords(){
            try {
                stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).toLowerCase().split(","));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean hasNext() {
            return prevIte.hasNext();
        }

        public List<String> next() {
            List<String> nonStopWords = new ArrayList<>();
            while (prevIte.hasNext()) {
                List<String> words = prevIte.next();
                for (String word : words){
                    if (!stopWords.contains(word) && word.length() > 1){
                         nonStopWords.add(word);
                    }
                }
                return nonStopWords;
            }
            return null;
        }
    }

    static class CountAndSortIterator implements Iterator<List<Map.Entry<String, Integer>>>{
        private Iterator<List<String>> prevIte;
        private HashMap<String, Integer> frequency;
        private List<String> wordList;

        public CountAndSortIterator(Iterator<List<String>> prevIte){
            this.prevIte = prevIte;
            frequency = new HashMap<>();
            wordList = new ArrayList<>();
        }

        public boolean hasNext() {
            return prevIte.hasNext();
        }

        public List<Map.Entry<String, Integer>> next() {
            int index = 1;
            List<Map.Entry<String, Integer>> list;
            while (prevIte.hasNext()  && index % 1000 != 0) {
                wordList = prevIte.next();
                index++;
                for (String word : wordList) {
                    frequency.put(word, frequency.getOrDefault(word, 0) + 1);
                }
            }
            // Sort frequency for every accumulated 1000 lines
            list = new ArrayList<>(frequency.entrySet());
            Collections.sort(list, (e1, e2) -> (e2.getValue() - e1.getValue()));
            return list;
        }
    }

    public static void main(String[] args){
        // read file iterator
        String filePath = "pride-and-prejudice.txt";
//        String filePath = "test.txt";
//        String filePath = args[0];
        try {
            Scanner scanner = new Scanner(new File(filePath));
            LinesIterator linesIterator = new LinesIterator(scanner);
            WordsIterator wordsIterator = new WordsIterator(linesIterator);
            StopWordsFilterIterator stopWordsFilterIterator = new StopWordsFilterIterator(wordsIterator);
            CountAndSortIterator countAndSortIterator = new CountAndSortIterator(stopWordsFilterIterator);

            int counter = 1;
            while (countAndSortIterator.hasNext()) {
                System.out.println("--------Iteration " + counter++ + " ---------");
                List<Map.Entry<String, Integer>> sortedFre = countAndSortIterator.next();
                for (int i = 0; i < 25; i++) {
                    System.out.println(sortedFre.get(i).getKey() + " - " + sortedFre.get(i).getValue());
                }
            }
            scanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
