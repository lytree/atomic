package top.yang.net.request;

import okhttp3.Request;
import top.yang.net.enums.MediaTypes;
import top.yang.net.interfaces.HttpRequest;

import java.util.Map;

public class TraceRequest implements HttpRequest {
    @Override
    public Request getRequest(String url, Map<String, String> requestBody, MediaTypes mediaTypes) {
        return null;
    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody) {
        return null;
    }
}
