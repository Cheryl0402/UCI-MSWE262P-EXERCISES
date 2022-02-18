package CountStartLetter;

import Framework.CountInterface;

import java.util.*;

public class CountStartLetter implements CountInterface {
    public List<Map.Entry<String, Integer>> countTop25(List<String> wordList){
        // put wordlist into map
        Map<String, Integer> frequecy = new HashMap<>();
        for (String word : wordList) {
            char c = word.charAt(0);
            if (Character.isAlphabetic(c)) {
                frequecy.put(c + "", frequecy.getOrDefault(c + "", 0) + 1);
            }
        }
        for (char alph = 'a'; alph <= 'z'; alph++) {
            if (!frequecy.containsKey(alph + "")) {
                frequecy.put(alph + "", 0);
            }
        }

        // sort map
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequecy.entrySet());
        Collections.sort(list, (e1, e2) -> (e2.getKey().compareTo(e2.getKey())));

        // get top 25
        List<Map.Entry<String, Integer>> top25 = new ArrayList<>();
        for (int i = 0; i <= 25; i++) {
            top25.add(list.get(i));
        }
        return top25;

    }
}
