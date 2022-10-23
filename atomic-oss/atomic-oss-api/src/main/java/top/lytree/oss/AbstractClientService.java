package top.lytree.oss;

import java.util.Map;

public abstract class AbstractClientService<T> {

    protected T client;

    public AbstractClientService(T client) {
        this.client = client;
    }

    protected String buildPostObjectBody(String boundary, Map<String, String> formFields, String filename, String contentType) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Map.Entry<String, String> entry : formFields.entrySet()) {
            // 添加boundary行,行首以--开头
            stringBuffer.append("--").append(boundary).append("\r\n");
            // 字段名
            stringBuffer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
            // 字段值
            stringBuffer.append(entry.getValue()).append("\r\n");
        }
        // 添加boundary行,行首以--开头
        stringBuffer.append("--").append(boundary).append("\r\n");
        // 文件名
        stringBuffer.append("Content-Disposition: form-data; name=\"file\"; " + "filename=\"").append(filename).append("\"\r\n");
        // 文件类型
        stringBuffer.append("Content-Type: ").append(contentType).append("\r\n\r\n");
        return stringBuffer.toString();
    }
}
