package top.yang.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.bouncycastle.jcajce.provider.digest.MD2;
import org.bouncycastle.jcajce.provider.digest.MD4;
import org.bouncycastle.jcajce.provider.digest.MD5;
import top.yang.math.HexUtils;

public class MD5Utils {


  /**
   * 计算32位MD5摘要值
   *
   * @param data 被摘要数据
   * @return MD5摘要
   */
  public static String md5(byte[] data) {
    try {
      byte[] bytes = MD5.Digest.getInstance("MD5").digest(data);
      return HexUtils.encodeHexString(bytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String md5(String data) {
    return md5(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String md5(String data, Charset charset) {
    return md5(data.getBytes(charset));
  }

  public static String md5(String data, String charset) {
    return md5(data.getBytes(Charset.forName(charset)));
  }

  public static String md516(String data) {
    return Objects.requireNonNull(md5(data.getBytes(StandardCharsets.UTF_8))).substring(8, 24);
  }

  public static String md516(String data, Charset charset) {
    return Objects.requireNonNull(md5(data.getBytes(charset))).substring(8, 24);
  }

  public static String md516(String data, String charset) {
    return Objects.requireNonNull(md5(data.getBytes(Charset.forName(charset)))).substring(8, 24);
  }

  /**
   * 计算32位MD4摘要值
   *
   * @param data 被摘要数据
   * @return MD5摘要
   */
  public static String md4(byte[] data) {
    try {
      byte[] bytes = MD4.Digest.getInstance("MD4").digest(data);
      return HexUtils.encodeHexString(bytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String md4(String data) {
    return md5(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String md4(String data, Charset charset) {
    return md5(data.getBytes(charset));
  }

  public static String md4(String data, String charset) {
    return md5(data.getBytes(Charset.forName(charset)));
  }

  public static String md416(String data) {
    return Objects.requireNonNull(md5(data.getBytes(StandardCharsets.UTF_8))).substring(8, 24);
  }

  public static String md416(String data, Charset charset) {
    return Objects.requireNonNull(md5(data.getBytes(charset))).substring(8, 24);
  }

  public static String md416(String data, String charset) {
    return Objects.requireNonNull(md5(data.getBytes(Charset.forName(charset)))).substring(8, 24);
  }

  /**
   * 计算32位MD2摘要值
   *
   * @param data 被摘要数据
   * @return MD5摘要
   */
  public static String md2(byte[] data) {
    try {
      byte[] bytes = MD2.Digest.getInstance("MD2").digest(data);
      return HexUtils.encodeHexString(bytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String md2(String data) {
    return md5(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String md2(String data, Charset charset) {
    return md5(data.getBytes(charset));
  }

  public static String md2(String data, String charset) {
    return md5(data.getBytes(Charset.forName(charset)));
  }

  public static String md216(String data) {
    return Objects.requireNonNull(md5(data.getBytes(StandardCharsets.UTF_8))).substring(8, 24);
  }

  public static String md216(String data, Charset charset) {
    return Objects.requireNonNull(md5(data.getBytes(charset))).substring(8, 24);
  }

  public static String md216(String data, String charset) {
    return Objects.requireNonNull(md5(data.getBytes(Charset.forName(charset)))).substring(8, 24);
  }
}
