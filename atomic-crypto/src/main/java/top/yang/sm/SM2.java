package top.yang.sm;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.util.encoders.Hex;

public class SM2 {
  /**
   * 生成密钥对
   */
  static KeyPair createECKeyPair() {
    //使用标准名称创建EC参数生成的参数规范
    final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");

    // 获取一个椭圆曲线类型的密钥对生成器
    final KeyPairGenerator kpg;
    try {
      kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
      // 使用SM2算法域参数集初始化密钥生成器（默认使用以最高优先级安装的提供者的 SecureRandom 的实现作为随机源）
      // kpg.initialize(sm2Spec);

      // 使用SM2的算法域参数集和指定的随机源初始化密钥生成器
      kpg.initialize(sm2Spec, new SecureRandom());

      // 通过密钥生成器生成密钥对
      return kpg.generateKeyPair();

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 公钥加密
   *
   * @param publicKey SM2公钥
   * @param data      明文数据
   * @param modeType  加密模式
   * @return
   */
  public static String encrypt(BCECPublicKey publicKey, String data, int modeType) {
    //加密模式
    SM2Engine.Mode mode;
    if (modeType == 1) {
      mode = SM2Engine.Mode.C1C3C2;
    } else {
      mode = SM2Engine.Mode.C1C2C3;
    }

    //通过公钥对象获取公钥的基本域参数。
    ECParameterSpec ecParameterSpec = publicKey.getParameters();
    ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(),
        ecParameterSpec.getG(), ecParameterSpec.getN());

    //通过公钥值和公钥基本参数创建公钥参数对象
    ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(publicKey.getQ(), ecDomainParameters);

    //根据加密模式实例化SM2公钥加密引擎
    SM2Engine sm2Engine = new SM2Engine(mode);

    //初始化加密引擎
    sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));

    byte[] arrayOfBytes = null;
    try {
      //将明文字符串转换为指定编码的字节串
      byte[] in = data.getBytes("utf-8");

      //通过加密引擎对字节数串行加密
      arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
    } catch (Exception e) {
      System.out.println("SM2加密时出现异常:" + e.getMessage());
      e.printStackTrace();
    }

    //将加密后的字节串转换为十六进制字符串
    return Hex.toHexString(arrayOfBytes);
  }

  /**
   * 私钥解密
   *
   * @param privateKey SM私钥
   * @param cipherData 密文数据
   * @return
   */
  public static String decrypt(BCECPrivateKey privateKey, String cipherData, int modeType) {
    //解密模式
    SM2Engine.Mode mode;
    if (modeType == 1) {
      mode = SM2Engine.Mode.C1C3C2;
    } else {
      mode = SM2Engine.Mode.C1C2C3;
    }

    //将十六进制字符串密文转换为字节数组（需要与加密一致，加密是：加密后的字节数组转换为了十六进制字符串）
    byte[] cipherDataByte = Hex.decode(cipherData);

    //通过私钥对象获取私钥的基本域参数。
    ECParameterSpec ecParameterSpec = privateKey.getParameters();
    ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(),
        ecParameterSpec.getG(), ecParameterSpec.getN());

    //通过私钥值和私钥钥基本参数创建私钥参数对象
    ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey.getD(),
        ecDomainParameters);

    //通过解密模式创建解密引擎并初始化
    SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
    sm2Engine.init(false, ecPrivateKeyParameters);

    String result = null;
    try {
      //通过解密引擎对密文字节串进行解密
      byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
      //将解密后的字节串转换为utf8字符编码的字符串（需要与明文加密时字符串转换成字节串所指定的字符编码保持一致）
      result = new String(arrayOfBytes, "utf-8");
    } catch (Exception e) {
      System.out.println("SM2解密时出现异常" + e.getMessage());
    }
    return result;
  }


  //椭圆曲线ECParameters ASN.1 结构
  private static X9ECParameters x9ECParameters = GMNamedCurves.getByName("sm2p256v1");
  //椭圆曲线公钥或私钥的基本域参数。
  private static ECParameterSpec ecDomainParameters = new ECParameterSpec(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());

  /**
   * 公钥字符串转换为 BCECPublicKey 公钥对象
   *
   * @param pubKeyHex 64字节十六进制公钥字符串(如果公钥字符串为65字节首个字节为0x04：表示该公钥为非压缩格式，操作时需要删除)
   * @return BCECPublicKey SM2公钥对象
   */
  public static BCECPublicKey getECPublicKeyByPublicKeyHex(String pubKeyHex) {
    //截取64字节有效的SM2公钥（如果公钥首个字节为0x04）
    if (pubKeyHex.length() > 128) {
      pubKeyHex = pubKeyHex.substring(pubKeyHex.length() - 128);
    }
    //将公钥拆分为x,y分量（各32字节）
    String stringX = pubKeyHex.substring(0, 64);
    String stringY = pubKeyHex.substring(stringX.length());
    //将公钥x、y分量转换为BigInteger类型
    BigInteger x = new BigInteger(stringX, 16);
    BigInteger y = new BigInteger(stringY, 16);
    //通过公钥x、y分量创建椭圆曲线公钥规范
    ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(x9ECParameters.getCurve().createPoint(x, y), ecDomainParameters);
    //通过椭圆曲线公钥规范，创建出椭圆曲线公钥对象（可用于SM2加密及验签）
    return new BCECPublicKey("EC", ecPublicKeySpec, BouncyCastleProvider.CONFIGURATION);
  }


  /**
   * 私钥字符串转换为 BCECPrivateKey 私钥对象
   *
   * @param privateKeyHex: 32字节十六进制私钥字符串
   * @return BCECPrivateKey:SM2私钥对象
   */
  public static BCECPrivateKey getBCECPrivateKeyByPrivateKeyHex(String privateKeyHex) {
    //将十六进制私钥字符串转换为BigInteger对象
    BigInteger d = new BigInteger(privateKeyHex, 16);
    //通过私钥和私钥域参数集创建椭圆曲线私钥规范
    ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d, ecDomainParameters);
    //通过椭圆曲线私钥规范，创建出椭圆曲线私钥对象（可用于SM2解密和签名）
    return new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
  }
}
