package CountNormal;

import Framework.CountInterface;

import java.util.*;

public class CountNormal implements CountInterface {

    public List<Map.Entry<String, Integer>> countTop25(List<String> wordList){
        // put wordlist into map
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : wordList) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }

        // sort map
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());
        Collections.sort(entryList, (e1, e2) -> (e2.getValue() - e1.getValue()));

        // get top 25
        List<Map.Entry<String, Integer>> top25 = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            top25.add(entryList.get(i));
        }
        return top25;
    }
}
