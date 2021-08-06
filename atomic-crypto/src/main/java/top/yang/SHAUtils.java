package top.yang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import top.yang.math.HexUtil;

public class SHAUtils {

  public static String sha1(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA1.Digest.getInstance("SHA1").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha1(String data) {
    return sha1(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha1(String data, Charset charset) {
    return sha1(data.getBytes(charset));
  }

  public static String sha1(String data, String charset) {
    return sha1(data.getBytes(Charset.forName(charset)));
  }

  public static String sha3(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA3.DigestSHA3.getInstance("SHA1").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha3(String data) {
    return sha1(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha3(String data, Charset charset) {
    return sha1(data.getBytes(charset));
  }

  public static String sha3(String data, String charset) {
    return sha1(data.getBytes(Charset.forName(charset)));
  }
}
