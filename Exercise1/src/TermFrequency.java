import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TermFrequency {
    private static HashMap<String, Integer> frequency = new HashMap<>();
    private static HashSet<String> stopWords = new HashSet<>();
    public static void main(String[] args){
        String fileName = args[0];
       // String stopWords = args[1];
        loadStopWord();
        countFrequency(fileName);
        printFrequency();
    }
    // load stop words to a hash set
    private static void loadStopWord(){
        try{
            FileReader fr = new FileReader("../stop_words.txt");
            Scanner scan = new Scanner(fr);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] words = line.trim().split(",");
                for (String word : words) {
                    stopWords.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        }
    }

//    private static void loadStopWord(String file){
//        try{
//            FileReader fr = new FileReader(file);
//            Scanner scan = new Scanner(fr);
//            while (scan.hasNext()) {
//                String line = scan.nextLine();
//                String[] words = line.trim().split(",");
//                for (String word : words) {
//                    stopWords.add(word);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("File Not Found!");
//        }
//    }

    // count frequency and put them into a hash map
    private static void countFrequency(String filename){
        try{
            FileReader fr = new FileReader(filename);
            Scanner scan = new Scanner(fr);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] words = line.split("[^a-zA-Z]");
                for (String word : words) {
                    String lowerWord = word.toLowerCase();
                    if (!stopWords.contains(lowerWord) && lowerWord.length() >= 2) {
                        if (frequency.containsKey(lowerWord)){
                            frequency.put(lowerWord, frequency.get(lowerWord) + 1);
                        } else {
                            frequency.put(lowerWord, 1);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e){
            System.out.println(e);
        }
    }

    // sort the frequency for out put
    private static List<Map.Entry<String, Integer>> sortFre(){
        // get the hole entry set
        Set<Map.Entry<String, Integer>> set = frequency.entrySet();
        //sort the entry values
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2){
                return e2.getValue().compareTo(e1.getValue());
            }
        });
        return list;
    }

    // Method to print frequency map
    private static void printFrequency(){
        List<Map.Entry<String, Integer>> sortedMap = sortFre();
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedMap) {
            count++;
            if (count > 25) {
                break;
            }
            String word = entry.getKey();
            Integer fre = entry.getValue();
            System.out.println(word + " - " + fre);
        }
    }
}
