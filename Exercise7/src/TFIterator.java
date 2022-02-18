import java.util.List;

public class TwentyEight {
    // Read words from file
    class FileWordsIterator implements Iterable<String> {
        private List<String> words;
        private String word;
        public boolean hasNext() {
            return (rand >= 0.1);
        }
        public String next() {
            if (rand >= 0.1) {
                while (Math.abs(last - rand) < 0.4) {
                    System.out.print("* ");
                    rand = Math.random();
                }
                last = rand;
            }
            return word;
        }
        public void remove() { }
    }



    public static void main(String[] args){
        // read file iterator

        // read stop words iterator

        // Non stop words

        //  count and sort


    }
}
