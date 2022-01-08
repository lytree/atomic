package top.yang.net.response;

import java.io.File;
import okhttp3.Call;

/**
 * @author pride
 */
public interface DownloadResponseHandler {

    public void onStart(long totalBytes);

    public void onCancel();

    public abstract void onFinish(File downloadFile);

    public abstract void onProgress(long currentBytes, long totalBytes);

    public abstract void onFailure(Call call, String e);
}
