import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Active Letterbox
 * The larger problem is decomposed into 'things' that make sense for the problem domain
 * Each 'thing' has a queue meant for other \textit{things} to place messages in it
 * Each 'thing' is a capsule of data that exposes only its ability to receive messages via the queue
 * Each 'thing' has its own thread of execution independent of the others.
 */

public class TwentyNine {
    public static void main(String[] args) {
        String filePath = "pride-and-prejudice.txt";
        List<Thread> actors = new ArrayList<>();
        WordFrequencyManager wordFrequencyManager = new WordFrequencyManager();
        StopWordManager stopWordManager = new StopWordManager();
        DataStorageManager storageManager = new DataStorageManager();
        WordFrequencyController wordFrequencyController = new WordFrequencyController();

//        String filePath = "pride-and-prejudice.txt";
        try {
            stopWordManager.queue.put(Arrays.asList("init", wordFrequencyManager));
            storageManager.queue.put(Arrays.asList("init",filePath,stopWordManager));
            wordFrequencyController.queue.put(Arrays.asList("run",storageManager));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        actors.add(wordFrequencyManager);
        actors.add(stopWordManager);
        actors.add(storageManager);
        actors.add(wordFrequencyController);

        // Wait for the active objects to finish
        for (Thread actor : actors) {
            try {
                actor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

abstract class ActiveWFObject extends Thread{
    private String name;
    protected boolean stopMe;
    protected ArrayBlockingQueue<List<Object>> queue;
    private Thread currThread;

    ActiveWFObject(){
        name = this.getClass().toString();
        stopMe = false;
        queue = new ArrayBlockingQueue<>(100);
        currThread = new Thread(this);
        currThread.start();
    }

    void dispatch(List<Object> message){

    }

    @Override
    public void run() {
        while (!stopMe) {
            try {
                List<Object>  message = queue.take();
                this.dispatch(message);

                if (((String) message.get(0)).equals("die")) {
                    this.stopMe = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DataStorageManager extends ActiveWFObject{
    private StopWordManager stopWordManager;
    private WordFrequencyController wordFrequencyController;
    private List<String> words = new ArrayList<>();
    private List<String> targetWords = new ArrayList<>();

    public void dispatch(List<Object> message){
        if (((String)message.get(0)).equals("init")) {
            this.init(message);
        } else if (((String)message.get(0)).equals("sendWordFreq")) {
            this.processWords(message);
        } else {
            List<Object> messageCopy = new ArrayList<>(message);
            try {
                this.stopWordManager.queue.put(messageCopy);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void init(List<Object> message) {
        String filePath = (String) message.get(1);
        this.stopWordManager = (StopWordManager) message.get(2);
        try {
            words = Arrays.asList(new String(Files.readAllBytes(Paths.get(filePath))).toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]+", " ").split(" "));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processWords(List<Object> message) {
        Object recipient = message.get(1);
        this.wordFrequencyController = (WordFrequencyController) recipient;

        for (String word : words) {
            if (word.length() > 1) {
                targetWords.add(word);
            }
        }

        // Sent word copy to stopWordManager to "filter" stop words
        for (String word : targetWords) {
            String wordCopy = word;
            try {
                this.stopWordManager.queue.put(Arrays.asList("filter", wordCopy));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            this.stopWordManager.queue.put(Arrays.asList("top25", this.wordFrequencyController));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


class StopWordManager extends ActiveWFObject{
    private List<String> stopWords = new ArrayList<>();
    private WordFrequencyManager wordFrequencyManager;

    public void dispatch(List<Object> message) {
        if (message.get(0).equals("init")) {
            init(message);
        } else if (message.get(0).equals("filter")) {
            this.filter(message);
        } else {
            try {
                this.wordFrequencyManager.queue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
     private void init(List<Object> message) {
         try {
             stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt"))).toLowerCase().split(","));
         } catch (IOException e) {
             e.printStackTrace();
         }
         this.wordFrequencyManager = (WordFrequencyManager) message.get(1);
     }

     private void filter(List<Object> message) {
        String word = (String) message.get(1);
        if (!stopWords.contains(word)) {
            try {
                this.wordFrequencyManager.queue.put(Arrays.asList("word", word));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
     }
}

class WordFrequencyManager extends ActiveWFObject{
    WordFrequencyController wordFrequencyController;
    private Map<String, Integer> wordFre = new HashMap<>();
    public void dispatch(List<Object> message) {
        if (message.get(0).equals("word")) {
            this.incrementCount(message);
        } else if (message.get(0).equals("top25")) {
            this.top25(message);
        }
    }

    private void incrementCount(List<Object> message) {
        String word = (String) message.get(1);
        wordFre.put(word, wordFre.getOrDefault(word, 0) + 1);
    }

    private void top25(List<Object> message) {
        Object recipient = message.get(1);
        this.wordFrequencyController = (WordFrequencyController) recipient;
        List<Map.Entry<String, Integer>> sortMap = sortMap(wordFre);
        try {
            this.wordFrequencyController.queue.put(Arrays.asList("top25",sortMap));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Map.Entry<String, Integer>> sortMap(Map<String, Integer> wordFre) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFre.entrySet());
        Collections.sort(list, (e1, e2) -> (e2.getValue() - e1.getValue()));
        return list;
    }
}

class WordFrequencyController extends ActiveWFObject{
    private DataStorageManager dataStorageManager;
    public void dispatch(List<Object> message) {
        if (message.get(0).equals("run")) {
            this.run(message);
        } else if (message.get(0).equals("top25")) {
            this.printTop25(message);
        } else {
            System.out.println("No such "+message.get(0) + " method in WordFrequencyController");
            System.exit(0);
        }
    }

    private void run(List<Object> message) {
        this.dataStorageManager = (DataStorageManager) message.get(1);
        try {
            this.dataStorageManager.queue.put(Arrays.asList("sendWordFreq", this));
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void printTop25(List<Object> message) {
        List<Map.Entry<String, Integer>> sortedMap = (List<Map.Entry<String, Integer>>)message.get(1);
        System.out.println("\n========== wordFrequency style 29 ===========");
        for (int i = 0; i < 25; i++) {
            System.out.println(sortedMap.get(i).getKey() + " - " + sortedMap.get(i).getValue());

        }
        try {
            this.dataStorageManager.queue.put(Arrays.asList("die"));
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        this.stopMe = true;
    }
}





