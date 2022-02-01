//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.function.Function;
//
///**
// * Style #10  Constraints:
// * Possible names:
// * The One
// * Monadic Identity
// * The wrapper of all things
// * Imperative functional style
// */
//
//public class Ten {
//    private Object value;
//
//    // Constructor takes an Object argument
//    public Ten(Object object){
//        this.value = object;
//    }
//    // Binding function
//    public Ten bind(IFunction func){
//        value = func.call(value);
//        return this;
//    }
//
//    // Unwrapping  function to print the final value
//    public void PrintMe(){
//        System.out.println(value);
//    }
//
//    public static void main(String[] args) {
//        Ten ten = new Ten("pride-and-prejudice.txt");
//        ten.bind(new ReadFile())
//            .bind(new countFrequency())
//            .bind(new SortMap())
//            .bind(new GetTop25())
//            .PrintMe();
//    }
//}
//
///**
// * function interface has a function call which takes an Object as argument and return an Object.
// */
//interface IFunction {
//    Object call(Object arg);
//}
//
//// Read file and return the file as a string array.
//class ReadFile implements  IFunction{
//    public Object call(Object object){
//        String fileName = (String)object;
//        String[] words = null;
//        try {
//            words = new String(Files.readAllBytes(Paths.get(fileName))).toLowerCase()
//                    .replaceAll("[^a-zA-Z0-9]+", " ").split(" ");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return words;
//    }
//}
//
//// Count frequency should take a word list as argument, and count its word frequency. Return it as a wrapped value
//class countFrequency implements  IFunction{
//    public Object call(Object object){
//        String[] words = (String[])object;
//        Map<String, Integer> frequency = new HashMap<>();
//        try {
//            List<String> stopWords = Arrays.asList(new String(Files.readAllBytes(Paths.get("stop_words.txt")))
//                    .toLowerCase().split(","));
//            for (String word : words) {
//                if (!stopWords.contains(word) && word.length() > 1) {
//                    if (frequency.containsKey(word)){
//                        frequency.put(word, frequency.get(word) + 1);
//                    } else {
//                        frequency.put(word, 1);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return frequency;
//    }
//}
//
//// Sort frequency map and return sorted map as a list
//class SortMap implements  IFunction{
//    public Object call(Object object){
//        Map<String, Integer> map = (Map<String, Integer>)object;
//        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
//        Collections.sort(list, (e1, e2) -> (e2.getValue() - e1.getValue()));
//        return list;
//    }
//}
//
//// Get top 25 frequency and return as a string.
//class GetTop25 implements IFunction{
//    public Object call(Object object) {
//        ArrayList<Map.Entry<String, Integer>> list = (ArrayList<Map.Entry<String, Integer>>)object;
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < 25; i++){
//            sb.append(list.get(i).getKey() + " - " + list.get(i).getValue() + "\n");
//        }
//        return sb.toString();
//    }
//}
//
