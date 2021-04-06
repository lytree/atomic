package top.yang.net.request;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import top.yang.json.JSONUtil;
import top.yang.net.enums.MediaTypes;
import top.yang.net.interfaces.HttpRequest;

import java.util.Map;

public class PatchRequest implements HttpRequest {
    public static Request.Builder patch(RequestBody requestBody) {
        return new Request.Builder().patch(requestBody);
    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody, MediaTypes mediaTypes) {
        return patch(RequestBody.create(JSONUtil.toJSONString(requestBody), MediaType.parse(mediaTypes.getMediaType()))).build();
    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody) {
        return patch(RequestBody.create(JSONUtil.toJSONString(requestBody), MediaType.parse(MediaTypes.JSON.getMediaType()))).build();
    }
}
