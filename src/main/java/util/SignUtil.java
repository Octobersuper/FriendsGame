package util;


import com.zcf.framework.common.utils.MD5Util;

import java.util.*;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2019/11/18
 * Time:14:13
 */
public class SignUtil {
    /**
     * 生成签名
     * @param map
     * @return
     */
    public static String getSign(Map<String, String> map) {

        String keys = "zTqUjiLTS8uRzr5UR7LAOE3yExzw5E8C";
        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }

            }
//			sb.append(PropertyManager.getProperty("SIGNKEY"));
            result = sb.toString()+"key="+keys;

            //进行MD5加密
            result = MD5Util.MD5EncodeUtf8(result);
        } catch (Exception e) {
            return null;
        }
        return result;
    }
}
