import com.sun.jdi.request.InvalidRequestStateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Dataspaces style
 * Existence of one or more units that execute concurrently
 * Existence of one or more data spaces where concurrent units store and retrieve data
 * No direct data exchanges between the concurrent units, other than via the data spaces
 */
public class Thirty {
    static BlockingQueue<String> wordSpace = new LinkedBlockingQueue<>();
    static BlockingQueue<HashMap<String,Integer>> frequencySpace = new LinkedBlockingQueue<>();
    static List<String> stopWords;

    static class Worker implements Runnable{
        BlockingQueue<String> wSpace;
        BlockingQueue<HashMap<String, Integer>> freSpace;
        HashMap<String, Integer> wordFre = new HashMap<>();

        public Worker(BlockingQueue<String> wSpace, BlockingQueue<HashMap<String, Integer>> freSpace){
            this.wSpace = wSpace;
            this.freSpace = freSpace;
        }

        // Worker function that consumes words from the word space and sends partial results to the frequency space
        // Consumes from wordSpace and produce frequency to freSpace
        public void run(){
            while (true) {
                String word = wordSpace.poll();
                if (word != null) {
                    if (!stopWords.contains(word) && word.length() > 1) {
                        wordFre.put(word, wordFre.getOrDefault(word, 0) + 1);
                    }
                } else {
                    frequencySpace.offer(wordFre);
                    break;
                }
            }
        }
    }
    public static void loadStopWords() {
        try {
            stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).toLowerCase().split(","));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Load stop words.
        loadStopWords();

        // Process file. Produce word to wordSpace
        String filePath = "pride-and-prejudice.txt";
        // ProcessFile(args[0]);
        try {
            List<String> words = Arrays.asList(new String(Files.readAllBytes(Paths.get(filePath))).toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
            for (String word : words) {
                wordSpace.offer(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create 5 worker threads and launch them at their jobs.
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(new Worker(wordSpace, frequencySpace));
            workers.add(t);
            t.start();
        }

        // Wait for the workers to finish.
        for(Thread t : workers){
            try {
                t.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        // Merge partial fre by consuming frequency data from frequencySpace
        HashMap<String, Integer> frequency = new HashMap<>();
        while (!frequencySpace.isEmpty()){
            HashMap<String, Integer> fre = frequencySpace.poll();
            if (!fre.isEmpty()) {
                for (Map.Entry<String, Integer> entry : fre.entrySet()){
                    String word = entry.getKey();
                    Integer curValue = entry.getValue();
                    if (frequency.containsKey(word)){
                        int existingVal = frequency.get(word);
                        frequency.put(word, existingVal + curValue);
                    } else {
                        frequency.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        // Sort frequency map and print out top25
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequency.entrySet());
        Collections.sort(list, (e1, e2) -> (e2.getValue() - e1.getValue()));
        for (int i = 0; i < 25; i++) {
            System.out.println(list.get(i).getKey() + " - " + list.get(i).getValue());
        }
    }
}
