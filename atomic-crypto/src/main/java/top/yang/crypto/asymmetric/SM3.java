package top.yang.crypto.asymmetric;

import java.security.NoSuchAlgorithmException;
import top.yang.math.HexUtils;

public class SM3 {

  /**
   * 计算32位MD4摘要值
   *
   * @param data 被摘要数据
   * @return MD5摘要
   */
  public static String sm3(byte[] data) {
    try {
      byte[] bytes = org.bouncycastle.jcajce.provider.digest.SM3.Digest.getInstance("SM3").digest(data);
      return HexUtils.encodeHexString(bytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }
}
