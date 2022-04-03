package top.yang.crypto.symmetric;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import top.yang.math.HexUtils;
import top.yang.string.CharsetUtils;
import top.yang.string.StringUtils;

/**
 * 对称加密器接口，提供：
 * <ul>
 *     <li>加密为bytes</li>
 *     <li>加密为Hex(16进制)</li>
 *     <li>加密为Base64</li>
 * </ul>
 *
 * @author looly
 * @since 5.7.12
 */
public interface SymmetricEncryptor {

    /**
     * 加密
     *
     * @param data 被加密的bytes
     * @return 加密后的bytes
     */
    byte[] encrypt(byte[] data);

    /**
     * 加密，针对大数据量，可选结束后是否关闭流
     *
     * @param data    被加密的字符串
     * @param out     输出流，可以是文件或网络位置
     * @param isClose 是否关闭流
     * @throws IOException IO异常
     */
    void encrypt(InputStream data, OutputStream out, boolean isClose) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException;

    /**
     * 加密
     *
     * @param data 数据
     * @return 加密后的Hex
     */
    default String encryptHex(byte[] data) {
        return HexUtils.encodeHexString(encrypt(data));
    }

    /**
     * 加密
     *
     * @param data 数据
     * @return 加密后的Base64
     */
    default String encryptBase64(byte[] data) {
        return new Base64().encodeToString(encrypt(data));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @return 加密后的bytes
     */
    default byte[] encrypt(String data, String charset) {
        return encrypt(StringUtils.getBytes(data, CharsetUtils.toCharset(charset)));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @return 加密后的bytes
     */
    default byte[] encrypt(String data, Charset charset) {
        return encrypt(StringUtils.getBytes(data, charset));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @return 加密后的Hex
     */
    default String encryptHex(String data, String charset) {
        return HexUtils.encodeHexString(encrypt(data, charset));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @return 加密后的Hex
     */
    default String encryptHex(String data, Charset charset) {
        return HexUtils.encodeHexString(encrypt(data, charset));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @return 加密后的Base64
     */
    default String encryptBase64(String data, String charset) {
        return new Base64().encodeToString(encrypt(data, charset));
    }

    /**
     * 加密
     *
     * @param data    被加密的字符串
     * @param charset 编码
     * @return 加密后的Base64
     * @since 4.5.12
     */
    default String encryptBase64(String data, Charset charset) {
        return new Base64().encodeToString(encrypt(data, charset));
    }

    /**
     * 加密，使用UTF-8编码
     *
     * @param data 被加密的字符串
     * @return 加密后的bytes
     */
    default byte[] encrypt(String data) {
        return encrypt(StringUtils.getBytes(data, CharsetUtils.CHARSET_UTF_8));
    }

    /**
     * 加密，使用UTF-8编码
     *
     * @param data 被加密的字符串
     * @return 加密后的Hex
     */
    default String encryptHex(String data) {
        return HexUtils.encodeHexString(encrypt(data));
    }

    /**
     * 加密，使用UTF-8编码
     *
     * @param data 被加密的字符串
     * @return 加密后的Base64
     */
    default String encryptBase64(String data) {
        return new Base64().encodeToString(encrypt(data));
    }

    /**
     * 加密，加密后关闭流
     *
     * @param data 被加密的字符串
     * @return 加密后的bytes
     * @throws IOException IO异常
     */
    default byte[] encrypt(InputStream data) throws IOException {
        return encrypt(IOUtils.buffer(data).readAllBytes());
    }

    /**
     * 加密
     *
     * @param data 被加密的字符串
     * @return 加密后的Hex
     */
    default String encryptHex(InputStream data) throws IOException {
        return HexUtils.encodeHexString(encrypt(data));
    }

    /**
     * 加密
     *
     * @param data 被加密的字符串
     * @return 加密后的Base64
     */
    default String encryptBase64(InputStream data) throws IOException {
        return new Base64().encodeToString(encrypt(data));
    }
}
