package top.yang.crypto.asymmetric;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import top.yang.codec.binary.Base64;
import top.yang.io.IOUtils;
import top.yang.math.HexUtils;
import top.yang.lang.StringUtils;

/**
 * 非对称加密器接口，提供：
 * <ul>
 *     <li>加密为bytes</li>
 *     <li>加密为Hex(16进制)</li>
 *     <li>加密为Base64</li>
 *     <li>加密为BCD</li>
 * </ul>
 *
 * @author looly
 *
 */
public interface AsymmetricEncryptor {

    /**
     * 加密
     *
     * @param data    被加密的bytes
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     */
    byte[] encrypt(byte[] data, KeyType keyType);

    /**
     * 编码为Hex字符串
     *
     * @param data    被加密的bytes
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Hex字符串
     */
    default String encryptHex(byte[] data, KeyType keyType) {
        return HexUtils.encodeHexString(encrypt(data, keyType));
    }

    /**
     * 编码为Base64字符串
     *
     * @param data    被加密的bytes
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Base64字符串
     *
     */
    default String encryptBase64(byte[] data, KeyType keyType) {
        return new Base64().encodeToString(encrypt(data, keyType));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     */
    default byte[] encrypt(String data, String charset, KeyType keyType) throws UnsupportedEncodingException {
        return encrypt(StringUtils.getBytes(data, charset), keyType);
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     */
    default byte[] encrypt(String data, Charset charset, KeyType keyType) {
        return encrypt(StringUtils.getBytes(data, charset), keyType);
    }

    /**
     * 加密，使用UTF-8编码
     *
     * @param data    被加密的字符串
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     */
    default byte[] encrypt(String data, KeyType keyType) {
        return encrypt(StringUtils.getBytesUtf8(data), keyType);
    }

    /**
     * 编码为Hex字符串
     *
     * @param data    被加密的字符串
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Hex字符串
     *
     */
    default String encryptHex(String data, KeyType keyType) {
        return HexUtils.encodeHexString(encrypt(data, keyType));
    }

    /**
     * 编码为Hex字符串
     *
     * @param data    被加密的bytes
     * @param charset 编码
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Hex字符串
     *
     */
    default String encryptHex(String data, Charset charset, KeyType keyType) {
        return HexUtils.encodeHexString(encrypt(data, charset, keyType));
    }

    /**
     * 编码为Base64字符串，使用UTF-8编码
     *
     * @param data    被加密的字符串
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Base64字符串
     *
     */
    default String encryptBase64(String data, KeyType keyType) {
        return new Base64().encodeToString(encrypt(data, keyType));
    }

    /**
     * 编码为Base64字符串
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Base64字符串
     *
     */
    default String encryptBase64(String data, Charset charset, KeyType keyType) {
        return new Base64().encodeToString(encrypt(data, charset, keyType));
    }

    /**
     * 加密
     *
     * @param data    被加密的数据流
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     * @throws IOException IO异常
     */
    default byte[] encrypt(InputStream data, KeyType keyType) throws IOException {
        return encrypt(IOUtils.buffer(data).readAllBytes(), keyType);
    }

    /**
     * 编码为Hex字符串
     *
     * @param data    被加密的数据流
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Hex字符串
     *
     */
    default String encryptHex(InputStream data, KeyType keyType) throws IOException {
        return HexUtils.encodeHexString(encrypt(data, keyType));
    }

    /**
     * 编码为Base64字符串
     *
     * @param data    被加密的数据流
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return Base64字符串
     *
     */
    default String encryptBase64(InputStream data, KeyType keyType) throws IOException {
        return new Base64().encodeToString(encrypt(data, keyType));
    }

}
