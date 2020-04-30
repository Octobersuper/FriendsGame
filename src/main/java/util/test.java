package util;

/**
 */
public class test {
    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            for (int j = i; j < 4; j++) {
                if(i!=j){
                    System.out.println(j);
                }
            }
        }
    }
}
