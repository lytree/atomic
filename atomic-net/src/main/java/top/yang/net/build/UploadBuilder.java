package top.yang.net.build;


import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import top.yang.net.HttpUtils;
import top.yang.net.body.ProgressRequestBody;
import top.yang.net.callback.CustomCallback;
import top.yang.net.response.IResponseHandler;

/**
 * upload builder Created by tsy on 16/9/18.
 */
public class UploadBuilder extends OkHttpRequestBuilderHasParam<UploadBuilder> {

  private Map<String, File> files;
  private List<MultipartBody.Part> extraParts;

  public UploadBuilder(HttpUtils httpUtils) {
    super(httpUtils);
  }

  /**
   * add upload files
   *
   * @param files files
   * @return
   */
  public UploadBuilder files(Map<String, File> files) {
    this.files = files;
    return this;
  }

  /**
   * add one upload file
   *
   * @param key  file key
   * @param file file
   * @return
   */
  public UploadBuilder addFile(String key, File file) {
    if (this.files == null) {
      files = new LinkedHashMap<>();
    }
    files.put(key, file);
    return this;
  }

  /**
   * add one upload file
   *
   * @param key         file key
   * @param fileName    file name
   * @param fileContent byte[] file content
   * @return
   */
  public UploadBuilder addFile(String key, String fileName, byte[] fileContent) {
    if (this.extraParts == null) {
      this.extraParts = new ArrayList<MultipartBody.Part>();
    }
    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), fileContent);
    this.extraParts.add(MultipartBody.Part.create(Headers.of("Content-Disposition",
        "form-data; name=\"" + key + "\"; filename=\"" + fileName + "\""),
        fileBody));
    return this;
  }

  @Override
  public void enqueue(IResponseHandler responseHandler) {

    if (url == null || url.length() == 0) {
      throw new IllegalArgumentException("url can not be null !");
    }

    Request.Builder builder = new Request.Builder().url(url);
    appendHeaders(builder, headers);

    if (tag != null) {
      builder.tag(tag);
    }

    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    appendParams(multipartBuilder, params);
    appendFiles(multipartBuilder, files);
    appendParts(multipartBuilder, extraParts);

    builder.post(new ProgressRequestBody(multipartBuilder.build(), responseHandler));

    Request request = builder.build();

    httpUtils.create().
        newCall(request).
        enqueue(new CustomCallback(responseHandler));
  }

  //append params into MultipartBody builder
  private void appendParams(MultipartBody.Builder builder, Map<String, String> params) {
    if (params != null && !params.isEmpty()) {
      for (String key : params.keySet()) {
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
            RequestBody.create(params.get(key), null));
      }
    }
  }

  //append files into MultipartBody builder
  private void appendFiles(MultipartBody.Builder builder, Map<String, File> files) {
    if (files != null && !files.isEmpty()) {
      RequestBody fileBody;
      for (String key : files.keySet()) {
        File file = files.get(key);
        String fileName = file.getName();
        fileBody = RequestBody.create(file, MediaType.parse(guessMimeType(fileName)));
        builder.addPart(Headers.of("Content-Disposition",
            "form-data; name=\"" + key + "\"; filename=\"" + fileName + "\""),
            fileBody);
      }
    }
  }

  //appends parts into MultipartBody builder
  private void appendParts(MultipartBody.Builder builder, List<MultipartBody.Part> parts) {
    if (parts != null && parts.size() > 0) {
      for (int i = 0; i < parts.size(); i++) {
        builder.addPart(parts.get(i));
      }
    }
  }

  //获取mime type
  private String guessMimeType(String path) {
    FileNameMap fileNameMap = URLConnection.getFileNameMap();
    String contentTypeFor = fileNameMap.getContentTypeFor(path);
    if (contentTypeFor == null) {
      contentTypeFor = "application/octet-stream";
    }
    return contentTypeFor;
  }
}
