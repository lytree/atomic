package top.yang.net.request;

import okhttp3.HttpUrl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpRequestUrl {
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestUrl.class);

    /**
     * @param url         url
     * @param requestBody get 请求
     * @return String
     */
    public static String createStringUrl(String url, Map<String, String> requestBody) {
        StringBuffer sb = new StringBuffer(url);
        if (MapUtils.isNotEmpty(requestBody)) {
            boolean firstFlag = true;
            for (Map.Entry<String, String> entry : requestBody.entrySet()) {
                if (firstFlag) {
                    sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param url          url
     * @param queryMap     查询条件
     * @param pathSegments 路径 /api/v1/doc
     * @return HttpUrl
     */
    public static HttpUrl createHttpQueryUrl(String url, @Nullable Map<String, String> queryMap, @Nullable List<String> pathSegments) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        if (MapUtils.isNotEmpty(queryMap)) {
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        if (!CollectionUtils.isEmpty(pathSegments)) {
            for (String pathSegment : pathSegments) {
                builder.addPathSegment(pathSegment);
            }
        }
        return builder.build();
    }

    public static URL createUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
