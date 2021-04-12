package top.yang.net.request;

import okhttp3.Headers;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.collections.MapUtil;
import top.yang.net.exception.UnsupportedHeaderException;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestHeader.class);
    private static HashMap<String, Object> headerDefaultMap = new HashMap<>();

    static {
        headerDefaultMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headerDefaultMap.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        headerDefaultMap.put("Cache-Control", "no-cache");
        headerDefaultMap.put("Connection", "keep-alive");
        headerDefaultMap.put("Accept-Encoding", "*");
        headerDefaultMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4456.0 Safari/537.36 Edg/91.0.845.2");
    }

    public static Headers header(Map<String, Object> map) {
        Headers.Builder builder = new Headers.Builder();

        if (MapUtils.isNotEmpty(map)) {
            headerDefaultMap.putAll(map);
            for (Map.Entry<String, Object> entry : headerDefaultMap.entrySet()) {
                if (entry.getValue() instanceof String) {
                    builder.add(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof Date) {
                    builder.add(entry.getKey(), (Date) entry.getValue());
                } else if (entry.getValue() instanceof Instant) {
                    builder.add(entry.getKey(), (Instant) entry.getValue());
                } else {
                    throw new UnsupportedHeaderException("不支持" + entry.getKey() + "其中的参数类型");
                }
            }
        } else {
            for (Map.Entry<String, Object> entry : headerDefaultMap.entrySet()) {
                if (entry.getValue() instanceof String) {
                    builder.add(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof Date) {
                    builder.add(entry.getKey(), (Date) entry.getValue());
                } else if (entry.getValue() instanceof Instant) {
                    builder.add(entry.getKey(), (Instant) entry.getValue());
                } else {
                    throw new UnsupportedHeaderException("不支持" + entry.getKey() + "其中的参数类型");
                }
            }
        }

        return builder.build();
    }
}
