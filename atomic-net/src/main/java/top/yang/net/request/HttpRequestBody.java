package top.yang.net.request;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.json.JSONUtil;
import top.yang.net.enums.ContentTypes;

import java.io.File;
import java.util.Map;

public class HttpRequestBody {
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestBody.class);

    public static RequestBody requestBodyJSON(Map<String, Object> map) {

        return RequestBody.create(JSONUtil.toJSONString(map), ContentTypes.JSON.getMediaType());
    }

    public static RequestBody requestBody(Map<String, Object> map, ContentTypes contentTypes) {
        return RequestBody.create(JSONUtil.toJSONString(map), contentTypes.getMediaType());
    }

    public static RequestBody requestBody(File file) {
        return RequestBody.create(file, ContentTypes.UPLOAD.getMediaType());
    }
}
