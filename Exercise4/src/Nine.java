
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Style #9
 * kick forward
 *
 * @Author Yi Chen
 */

public class Nine {
    private String file;
    public Nine(String file){
        this.file = file;
        new readFile().call(this.file, new countFrequency());
    }

    public static void main(String[] args) {
        new Nine("pride-and-prejudice.txt");
    }
}

/**
 * An interface is a completely "abstract class" that is used to group related methods with empty bodies
 * <p>
 * IFunction interface has a function call which takes into arg and IFunction instance as arguments.
 */
interface function {
    void call(Object arg, function func);
}

class readFile implements  function{
    public void call(Object object, function func){
        String filaName = (String)object;
        String[] words = null;
        try {
           words = new String(Files.readAllBytes(Paths.get("pride-and-prejudice.txt"))).toLowerCase()
                   .replaceAll("[^a-zA-Z0-9]+", " ").split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        func.call(words, new SortMap());
    }

}

class countFrequency implements  function{
    public void call(Object object, function func){
        String[] words = (String[])object;
        Map<String, Integer> frequency = new HashMap<>();
        try {
            List<String> stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt")))
                    .toLowerCase().split(","));
            for (String word : words) {
                if (!stopWords.contains(word) && word.length() > 1) {
                    if (frequency.containsKey(word)){
                        frequency.put(word, frequency.get(word) + 1);
                    } else {
                        frequency.put(word, 1);
                    }
                }
            }
            func.call(frequency, new Print());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SortMap implements  function{
    public void call(Object object, function func){
        Map<String, Integer> map = (Map<String, Integer>)object;
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (e1, e2) -> (e2.getValue() - e1.getValue()));
        func.call(list, new Print());

    }
}
class Print implements function{
    public void call(Object object, function func) {
        ArrayList<Map.Entry<String, Integer>> list = (ArrayList<Map.Entry<String, Integer>>)object;
        for(int i = 0; i < 25; i++){
            System.out.println(list.get(i).getKey() + " - " + list.get(i).getValue());
        }
        return;
//        func.call(null, null);
    }
}

