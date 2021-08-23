package top.yang.oss.service;

import java.io.File;

public interface FileServer {

  String upload(String ossPath, File file);

  void uploadPrivate(String ossPath, File file);

  String getPrivateAccessUrl(String ossPath, long expire);
}
