package nice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description :
 * @Author : 田迎
 * @Date : 2022/7/24 13:53
 * @Version : 1.0.0
 **/
public class ChmStudy {


    public static void main(String[] args) {

        /**
         *创建容量为8的CHM
         */
        Map<String, String> map = new ConcurrentHashMap<>(8);
        // 多线程并发操作 线程安全
        new Thread(() -> map.put("1", "one")).start();
        new Thread(() -> map.put("2", "two")).start();
        System.out.println(map);

    }
}
