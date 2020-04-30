package test1;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2020/3/3
 * Time:17:41
 */
public class ceshi {
    public static void main(String[] args) {
        Double userName = 0.001;
        for (int i = 0; i < 101; i++) {
            userName = userName * 2;
        }
        System.out.println(userName);
    }


    public static Map readConfiguration(String path) throws Exception {
        ClassLoader classLoader = ceshi.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        String newPath = resource.getPath();

        Properties properties = new Properties();
        //可以用两种不同的流来加载配置文件
        properties.load(new BufferedReader(new FileReader(newPath)));
//        properties.load(ReadConfigurationClass.class.getResourceAsStream(path));

        //也可以指定键名来获取值
        //String name = properties.getProperty("name");
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entrySet) {
            resultMap.put(entry.getKey(), entry.getValue());
            System.out.println(entry.getKey()+" -- "+entry.getValue());
        }
        return resultMap;
    }
}
