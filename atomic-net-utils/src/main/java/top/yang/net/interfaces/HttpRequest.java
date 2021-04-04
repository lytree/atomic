package top.yang.net.interfaces;

import okhttp3.Request;
import top.yang.net.enums.MediaTypes;

import java.util.Map;

public interface HttpRequest {
    public Request getRequest(String url, Map<String, String> requestBody, MediaTypes mediaTypes);

    public Request getRequest(String url, Map<String, String> requestBody);

    default Request.Builder url(String url) {
        return new Request.Builder().url(url);
    }
}
