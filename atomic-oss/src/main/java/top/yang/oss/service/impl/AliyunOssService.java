package top.yang.oss.service.impl;


import java.io.File;
import top.yang.oss.service.FileServer;

public class AliyunOssService implements FileServer {

  @Override
  public String upload(String ossPath, File file) {
    return null;
  }

  @Override
  public void uploadPrivate(String ossPath, File file) {

  }

  @Override
  public String getPrivateAccessUrl(String ossPath, long expire) {
    return null;
  }
}
