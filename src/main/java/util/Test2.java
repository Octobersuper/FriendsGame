package util;

import java.util.ArrayList;

/**
 * @ClassName Test2
 * @Author zhanghu
 * @Date 2019/6/11 20:37
 **/
public class Test2 {


    public static void main(String[] args) {

        String  str="1-2-3-4-";
        String[] split = str.split("-");
        ArrayList<Integer> tingList = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            tingList.add(Integer.parseInt(split[i]));
        }
        System.out.println(tingList);
    }
}
