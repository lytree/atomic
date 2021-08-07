package top.yang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA512;
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

  public static String sha3224(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA3.Digest224.getInstance("SHA3").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha3224(String data) {
    return sha3224(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha3224(String data, Charset charset) {
    return sha3224(data.getBytes(charset));
  }

  public static String sha3224(String data, String charset) {
    return sha3224(data.getBytes(Charset.forName(charset)));
  }

  public static String sha3256(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA3.Digest256.getInstance("SHA3").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha3256(String data) {
    return sha3256(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha3256(String data, Charset charset) {
    return sha3256(data.getBytes(charset));
  }

  public static String sha3256(String data, String charset) {
    return sha3256(data.getBytes(Charset.forName(charset)));
  }

  public static String sha3512(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA3.Digest512.getInstance("SHA3").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha3512(String data) {
    return sha3512(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha3512(String data, Charset charset) {
    return sha3512(data.getBytes(charset));
  }

  public static String sha3512(String data, String charset) {
    return sha3512(data.getBytes(Charset.forName(charset)));
  }

  public static String sha256(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA256.Digest.getInstance("SHA256").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha256(String data) {
    return sha256(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha256(String data, Charset charset) {
    return sha256(data.getBytes(charset));
  }

  public static String sha256(String data, String charset) {
    return sha256(data.getBytes(Charset.forName(charset)));
  }

  public static String sha512(byte[] data) {
    try {
      return HexUtil.encodeHexString(SHA512.Digest.getInstance("SHA512").digest(data));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String sha512(String data) {
    return sha512(data.getBytes(StandardCharsets.UTF_8));
  }

  public static String sha512(String data, Charset charset) {
    return sha512(data.getBytes(charset));
  }

  public static String sha512(String data, String charset) {
    return sha512(data.getBytes(Charset.forName(charset)));
  }
}
