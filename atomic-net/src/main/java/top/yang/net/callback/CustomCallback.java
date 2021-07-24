package top.yang.net.callback;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.yang.net.response.IResponseHandler;

public class CustomCallback implements Callback {

  private IResponseHandler responseHandler;

  public CustomCallback(IResponseHandler responseHandler) {
    this.responseHandler = responseHandler;
  }

  @Override
  public void onFailure(Call call, final IOException e) {
    responseHandler.onFailure(call, e.getMessage());
  }

  @Override
  public void onResponse(Call call, final Response response) {
    if (response.isSuccessful()) {
      responseHandler.onSuccess(response);
    } else {
      responseHandler.onFailure(call, response.message());
    }
  }
}
