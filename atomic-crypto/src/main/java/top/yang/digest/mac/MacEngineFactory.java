package top.yang.digest.mac;


import java.security.Key;
import top.yang.SmUtil;
import top.yang.digest.HmacAlgorithm;

/**
 * {@link MacEngine} 实现工厂类
 *
 * @author Looly
 * @since 4.5.13
 */
public class MacEngineFactory {

  /**
   * 根据给定算法和密钥生成对应的{@link MacEngine}
   *
   * @param algorithm 算法，见{@link HmacAlgorithm}
   * @param key       密钥
   * @return {@link MacEngine}
   */
  public static MacEngine createEngine(String algorithm, Key key) {
    if (algorithm.equalsIgnoreCase(HmacAlgorithm.HmacSM3.getValue())) {
      // HmacSM3算法是BC库实现的
      return SmUtil.createHmacSm3Engine(key.getEncoded());
    }
    return new DefaultHMacEngine(algorithm, key);
  }
}
