package top.yang.net.request;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections4.MapUtils;
import top.yang.json.JSONUtil;
import top.yang.net.enums.MediaTypes;
import top.yang.net.interfaces.HttpRequest;

import java.util.Iterator;
import java.util.Map;

public class PostRequest implements HttpRequest {
    public static Request.Builder post(RequestBody requestBody){
        return new Request.Builder().post(requestBody);
    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody, MediaTypes mediaTypes) {
        return post(RequestBody.create(JSONUtil.toJSONString(requestBody), MediaType.parse(mediaTypes.getMediaType()))).url(url).build();
    }

    @Override
    public Request getRequest(String url, Map<String, String> requestBody) {
        return post(RequestBody.create(JSONUtil.toJSONString(requestBody), MediaType.parse(MediaTypes.JSON.getMediaType()))).url(url).build();
    }
}
