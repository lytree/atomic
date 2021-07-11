package top.yang.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import top.yang.exception.FileIoException;
import top.yang.exception.TemplateFileCreateException;

public class FileWapper {

  private File file;

  public FileWapper(File file) {
    this.file = file;
  }

  public static FileWapper from(String path) {
    return new FileWapper(new File(path));
  }

  public static FileWapper from(File file) {
    return new FileWapper(file);
  }

  /**
   * @methodName: fromTemp
   * @description: 创建临时文件
   * @author:
   * @date: 2018/6/8
   **/
  public static FileWapper fromTemp(String filename) {
    String prefix = filename.substring(0, filename.indexOf("."));
    String suffix = filename.substring(filename.indexOf("."));
    try {
      File tempFile = File.createTempFile(prefix, suffix);
      return new FileWapper(tempFile);
    } catch (IOException e) {
      throw new TemplateFileCreateException(filename);
    }
  }

  /**
   * @methodName: create
   * @description: 创建文件
   * @author: xiangfeng@biyouxinli.com.cn
   * @date: 2020/9/29
   **/
  public FileWapper create() throws IOException {

    createPath();

    if (!file.exists()) {
      file.createNewFile();
    }

    return this;
  }

  /**
   * @methodName: delete
   * @escription: 删除文件
   * @author: xiangfeng@biyouxinli.com.cn
   * @date: 2020/9/29
   **/
  public FileWapper delete() throws IOException {

    // 防止文件夹误删，只删除文件
    if (file.exists() && file.isFile()) {
      file.delete();
    }

    return this;
  }

  /**
   * @methodName: createPath
   * @description: 判断路径，如果没有路径则创建路径
   * @author:
   * @date: 2018/5/21
   **/
  public FileWapper createPath() {
    createPath(file);
    return this;
  }

  private void createPath(File file) {
    if (file.isDirectory()) {
      if (!file.exists()) {
        file.mkdirs();
      }
    } else {
      File parentFile = file.getParentFile();
      if (!parentFile.exists()) {
        parentFile.mkdirs();
      }
    }
  }

  public FileWapper write(String content) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write(content.getBytes("UTF-8"));
    fileOutputStream.close();
    return this;
  }

  /**
   * @methodName: write
   * @description: 读取文件输入流，并将数据写入到文件
   * @author:
   * @date: 2018/6/8
   **/
  public FileWapper write(InputStream inputStream) {
    writeTo(inputStream, file);
    return this;
  }

  private void writeTo(InputStream inputStream, File file) {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(file);

      Integer inv = 1000;
      do {
        int length = inputStream.available() > inv ? inv : (inputStream.available());
        byte[] temp = new byte[length];
        inputStream.read(temp, 0, length);
        fileOutputStream.write(temp);
      } while (inputStream.available() > 0);

    } catch (IOException e) {
      throw new FileIoException();
    }
  }

  /**
   * @methodName: extension
   * @description: 获取扩展名
   * @author:
   * @date: 2018/6/4
   **/
  public String extension() {
    String fileName = file.getName();
    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
    return suffix;
  }

  /**
   * @methodName: file
   * @description: 获取文件
   * @author:
   * @date: 2018/6/8
   **/
  public File file() {
    return file;
  }

  /**
   * @methodName: saveAs
   * @description: 文件另存为
   * @author:
   * @date: 2018/6/8
   **/
  public FileWapper saveAs(String path, String fileName) {
    try {
      File saveAsFile = new File(path + File.separator + fileName);
      createPath(saveAsFile);
      writeTo(new FileInputStream(file), saveAsFile);
    } catch (IOException e) {
      throw new FileIoException();
    }
    return this;
  }

  /**
   * @methodName: read
   * @description: 将对象读取成字节流
   * @author: xiangfeng@biyouxinli.com.cn
   * @date: 2018/8/2
   **/
  public byte[] read() {
    byte[] buffer = null;
    try {
      FileInputStream fis = new FileInputStream(file);
      ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
      byte[] b = new byte[1000];
      int n;
      while ((n = fis.read(b)) != -1) {
        bos.write(b, 0, n);
      }
      fis.close();
      bos.close();
      buffer = bos.toByteArray();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return buffer;
  }

}
