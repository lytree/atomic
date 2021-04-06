package top.yang.net.request;

import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import top.yang.net.enums.MediaTypes;
import top.yang.net.interfaces.HttpRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class GetRequest implements HttpRequest {
    public Request.Builder get() {
        return new Request.Builder().get();
    }
    public static String createGetUrl(String url, Map<String, String> requestBody) {
        StringBuffer sb = new StringBuffer(url);
        if (MapUtils.isNotEmpty(requestBody)) {
            boolean firstFlag = true;
            Iterator<Map.Entry<String, String>> iterator = requestBody.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
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

    @Override
    public Request getRequest(String url, Map<String, String> requestBody, MediaTypes mediaTypes) {
        return get().url(createGetUrl(url, requestBody)).build();
    }
}
