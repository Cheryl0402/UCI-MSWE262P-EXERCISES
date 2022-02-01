import java.io.BufferedReader;
import java.io.FileReader;

public class test {
    public static void main(String[] args) throws Exception{
        String str = "The Project Gutenberg EBook of Pride and Prejudice, by Jane Austen";
        BufferedReader br = new BufferedReader(new FileReader("test_file.txt"));
        int i = 0;
        int start = 0;
        String s = "";
        while(i < str.length()){
            char c = str.charAt(i);
            if ( c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9'){
                s = s +c;
                i++;
            } else {
                System.out.println("s: " + s);
                s = "";
                i++;
            }

        }

        if ( s.length() > 0) {
            System.out.println("s: " + s);
        }
    }
}
