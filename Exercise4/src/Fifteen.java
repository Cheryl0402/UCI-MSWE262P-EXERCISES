import jdk.jfr.Event;

import javax.print.DocFlavor;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;

public class Fifteen {
    public static void main(String[] args) {
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        ReadFile rd = new ReadFile(wfapp);
        FilterStopWords stopWordsFilter = new FilterStopWords(wfapp);
        CountFrequency cf = new CountFrequency(wfapp, rd, stopWordsFilter);
        CountWordsWithZ zCounter = new CountWordsWithZ(wfapp,rd,stopWordsFilter);
        wfapp.run("pride-and-prejudice.txt");
    }

    static class WordFrequencyFramework {
        List<FileHandler> fileHandlers;
        List<StringHandler> processHandlers;
        List<StringHandler> handlers;

        public WordFrequencyFramework(){
            fileHandlers = new ArrayList<>();
            processHandlers = new ArrayList<>();
            handlers = new ArrayList<>();
        }

        public void registerLoadEvent(FileHandler handler) {
            fileHandlers.add(handler);
        }

        public void registerProcessEvent(StringHandler handler) {
            processHandlers.add(handler);
        }

        public void registerEndEvent(StringHandler handler) {
            handlers.add(handler);
        }

        public void run(String filePath){
            for (FileHandler handler : fileHandlers) {
                handler.handleFile(filePath);
            }
            for (StringHandler stringHandler : processHandlers){
                stringHandler.handleString();
            }
            for ( StringHandler stringHandler : handlers){
                stringHandler.handleString();
            }
        }
    }

    interface FileHandler {
        void handleFile(String fileName);
    }

    interface StringHandler {
        void handleString();
    }

    // entity to read File ( stop_words and input file)
    static class ReadFile {
        List<String> words;

        public ReadFile(WordFrequencyFramework wfapp) {
            wfapp.registerLoadEvent(fileHandler);
        }

        FileHandler fileHandler = filePath -> {
            try {
                words = Arrays.asList(new String(Files.readAllBytes(Paths.get(filePath))).toLowerCase()
                        .replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    static class FilterStopWords {
        List<String> stopWords;
        public FilterStopWords(WordFrequencyFramework wfapp){
            wfapp.registerLoadEvent(stopwordsHandler);
        }
        FileHandler stopwordsHandler = (file) -> {
            try {
                stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).toLowerCase().split(","));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        public boolean isStopWord(String word){
            return stopWords.contains(word);
        }

    }

    static class CountFrequency {
        HashMap<String, Integer> frequency;
        ReadFile readFile;
        FilterStopWords stopWordsFilter;

        public CountFrequency(WordFrequencyFramework wfapp, ReadFile rd, FilterStopWords fsw){
            frequency = new HashMap<>();
            readFile = rd;
            stopWordsFilter = fsw;
            wfapp.registerProcessEvent(frequencyHandler);
            wfapp.registerEndEvent(printHandler);
        }

        StringHandler frequencyHandler = () -> {
            for (String word : readFile.words) {
                if (!stopWordsFilter.isStopWord(word) && word.length() > 1) {
                    if (frequency.containsKey(word)) {
                        frequency.put(word, frequency.get(word) + 1);
                    } else {
                        frequency.put(word, 1);
                    }
                }
            }
        };

        StringHandler printHandler = () -> {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(frequency.entrySet());
            Collections.sort(list, (e1, e2) -> (e2.getValue() - e1.getValue()));
            for(int i = 0; i < 25; i++){
                System.out.println(list.get(i).getKey() + " - " + list.get(i).getValue());
            }
        };
    }

    static class CountWordsWithZ{
        private static int count = 0;
        ReadFile readFile;
        FilterStopWords stopWordsFilter;

        public CountWordsWithZ(WordFrequencyFramework wfapp, ReadFile rd, FilterStopWords fsw){
            readFile = rd;
            stopWordsFilter = fsw;
            wfapp.registerEndEvent(countAndPrintHandler);
        }

        StringHandler countAndPrintHandler = () -> {
            for (String word : readFile.words) {
                if (!stopWordsFilter.isStopWord(word)) {
                    if (word.contains("z")) {
                        count++;
                    }
                }
            }
            System.out.println("Number of words with letter z: " + count);
        };

    }


}
