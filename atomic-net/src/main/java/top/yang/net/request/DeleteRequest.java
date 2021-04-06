package top.yang.net.request;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import top.yang.json.JSONUtil;
import top.yang.net.enums.MediaTypes;
import top.yang.net.interfaces.HttpRequest;

import java.util.Map;

public class DeleteRequest implements HttpRequest {
    public Request.Builder delete(RequestBody requestBody) {
        return new Request.Builder().delete(requestBody);
    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody, MediaTypes mediaTypes) {
        return delete(RequestBody.create(JSONUtil.toJSONString(requestBody), MediaType.parse(mediaTypes.getMediaType()))).url(url).build();

    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody) {
        return delete(RequestBody.create(JSONUtil.toJSONString(requestBody), MediaType.parse(MediaTypes.JSON.getMediaType()))).url(url).build();
    }
}
