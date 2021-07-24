package top.yang.net.response;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;

public interface IResponseHandler {

  void onSuccess(Response response);

  void onFailure(Call call, String e);

  void onProgress(long currentBytes, long totalBytes);
}
