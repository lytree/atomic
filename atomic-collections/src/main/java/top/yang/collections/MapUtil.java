package top.yang.collections;

import org.apache.commons.collections4.*;
import org.apache.commons.collections4.map.*;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class MapUtil {
    private MapUtil() {
    }

    /**
     * 合并多个map
     *
     * @param maps
     * @param <K>
     * @param <V>
     * @return
     * @throws Exception
     */
    @SafeVarargs
    public static <K, V> Map mergeMaps(Map<K, V>... maps) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = maps[0].getClass(); // 获取传入map的类型
        Map map = null;
        map = (Map) clazz.getDeclaredConstructor().newInstance();
        for (int i = 0, len = maps.length; i < len; i++) {
            map.putAll(maps[i]);
        }
        return map;
    }
}
