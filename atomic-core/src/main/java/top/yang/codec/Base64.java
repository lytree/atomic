package top.yang.codec;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import top.yang.io.FileUtils;
import top.yang.io.IOUtils;
import top.yang.string.CharsetsUtils;
import top.yang.string.StringUtils;

/**
 * Base64工具类，提供Base64的编码和解码方案<br>
 * base64编码是用64（2的6次方）个ASCII字符来表示256（2的8次方）个ASCII字符，<br>
 * 也就是三位二进制数组经过编码后变为四位的ASCII字符显示，长度比原来增加1/3。
 *
 * @author Looly
 */
public class Base64 {

	// -------------------------------------------------------------------- encode

	/**
	 * 编码为Base64，非URL安全的
	 *
	 * @param arr     被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 */
	public static byte[] encode(byte[] arr, boolean lineSep) {
		return Base64Encoder.encode(arr, lineSep);
	}

	/**
	 * 编码为Base64，URL安全的
	 *
	 * @param arr     被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 * @since 3.0.6
	 */
	public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
		return Base64Encoder.encodeUrlSafe(arr, lineSep);
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(String source) {
		return Base64Encoder.encode(source);
	}

	/**
	 * base64编码，URL安全
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(String source) {
		return Base64Encoder.encodeUrlSafe(source);
	}

	/**
	 * base64编码
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String encode(String source, String charset) {
		return encode(source, CharsetsUtils.toCharsetName(charset));
	}

	/**
	 * base64编码，不进行padding(末尾不会填充'=')
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 编码
	 * @return 被加密后的字符串
	 * @since 5.5.2
	 */
	public static String encodeWithoutPadding(String source, String charset) {
		return encodeWithoutPadding(StringUtils.toBytes(source, charset));
	}

	/**
	 * base64编码,URL安全
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(String source, String charset) {
		return encodeUrlSafe(source, CharsetsUtils.toCharsetName(charset));
	}

	/**
	 * base64编码
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String encode(String source, Charset charset) {
		return Base64Encoder.encode(source, charset);
	}

	/**
	 * base64编码，URL安全的
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(String source, Charset charset) {
		return Base64Encoder.encodeUrlSafe(source, charset);
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(byte[] source) {
		return Base64Encoder.encode(source);
	}

	/**
	 * base64编码，不进行padding(末尾不会填充'=')
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 5.5.2
	 */
	public static String encodeWithoutPadding(byte[] source) {
		return java.util.Base64.getEncoder().withoutPadding().encodeToString(source);
	}

	/**
	 * base64编码,URL安全的
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(byte[] source) {
		return Base64Encoder.encodeUrlSafe(source);
	}

//	/**
//	 * base64编码
//	 *
//	 * @param in 被编码base64的流（一般为图片流或者文件流）
//	 * @return 被加密后的字符串
//	 * @since 4.0.9
//	 */
//	public static String encode(InputStream in) {
//		return Base64Encoder.encode(IOUtils.readBytes(in));
//	}
//
//	/**
//	 * base64编码,URL安全的
//	 *
//	 * @param in 被编码base64的流（一般为图片流或者文件流）
//	 * @return 被加密后的字符串
//	 * @since 4.0.9
//	 */
//	public static String encodeUrlSafe(InputStream in) {
//		return Base64Encoder.encodeUrlSafe(IOUtils.readBytes(in));
//	}
//
//	/**
//	 * base64编码
//	 *
//	 * @param file 被编码base64的文件
//	 * @return 被加密后的字符串
//	 * @since 4.0.9
//	 */
//	public static String encode(File file) {
//		return Base64Encoder.encode(FileUtils.readBytes(file));
//	}
//
//	/**
//	 * base64编码,URL安全的
//	 *
//	 * @param file 被编码base64的文件
//	 * @return 被加密后的字符串
//	 * @since 4.0.9
//	 */
//	public static String encodeUrlSafe(File file) {
//		return Base64Encoder.encodeUrlSafe(FileUtils.readBytes(file));
//	}

	/**
	 * 编码为Base64字符串<br>
	 * 如果isMultiLine为{@code true}，则每76个字符一个换行符，否则在一行显示
	 *
	 * @param arr         被编码的数组
	 * @param isMultiLine 在76个char之后是CRLF还是EOF
	 * @param isUrlSafe   是否使用URL安全字符，一般为{@code false}
	 * @return 编码后的bytes
	 * @since 5.7.2
	 */
	public static String encodeStr(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
		return Base64Encoder.encodeStr(arr, isMultiLine, isUrlSafe);
	}

	/**
	 * 编码为Base64<br>
	 * 如果isMultiLine为{@code true}，则每76个字符一个换行符，否则在一行显示
	 *
	 * @param arr         被编码的数组
	 * @param isMultiLine 在76个char之后是CRLF还是EOF
	 * @param isUrlSafe   是否使用URL安全字符，一般为{@code false}
	 * @return 编码后的bytes
	 */
	public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
		return Base64Encoder.encode(arr, isMultiLine, isUrlSafe);
	}

	// -------------------------------------------------------------------- decode

	/**
	 * base64解码
	 *
	 * @param source 被解码的base64字符串
	 * @return 被加密后的字符串
	 * @since 4.3.2
	 */
	public static String decodeStrGbk(String source) {
		return Base64Decoder.decodeStr(source, CharsetsUtils.CHARSET_GBK);
	}

	/**
	 * base64解码
	 *
	 * @param source 被解码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String decodeStr(String source) {
		return Base64Decoder.decodeStr(source);
	}

	/**
	 * base64解码
	 *
	 * @param source  被解码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String decodeStr(String source, String charset) {
		return decodeStr(source, CharsetsUtils.toCharsetName(charset));
	}

	/**
	 * base64解码
	 *
	 * @param source  被解码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String decodeStr(String source, Charset charset) {
		return Base64Decoder.decodeStr(source, charset);
	}

//	/**
//	 * base64解码
//	 *
//	 * @param base64   被解码的base64字符串
//	 * @param destFile 目标文件
//	 * @return 目标文件
//	 * @since 4.0.9
//	 */
//	public static File decodeToFile(String base64) {
//		return FileUtils.writeBytes(Base64Decoder.decode(base64));
//	}
//
//	/**
//	 * base64解码
//	 *
//	 * @param base64     被解码的base64字符串
//	 * @since 4.0.9
//	 */
//	public static OutputStream decodeToStream(String base64) {
//		IOUtils.write(Base64Decoder.decode(base64));
//	}

	/**
	 * base64解码
	 *
	 * @param base64 被解码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static byte[] decode(String base64) {
		return Base64Decoder.decode(base64);
	}

	/**
	 * 解码Base64
	 *
	 * @param in 输入
	 * @return 解码后的bytes
	 */
	public static byte[] decode(byte[] in) {
		return Base64Decoder.decode(in);
	}

	/**
	 * 检查是否为Base64
	 *
	 * @param base64 Base64的bytes
	 * @return 是否为Base64
	 * @since 5.7.5
	 */
	public static boolean isBase64(String base64){
		return isBase64(StringUtils.toBytes(base64));
	}

	/**
	 * 检查是否为Base64
	 *
	 * @param base64Bytes Base64的bytes
	 * @return 是否为Base64
	 * @since 5.7.5
	 */
	public static boolean isBase64(byte[] base64Bytes){
		for (byte base64Byte : base64Bytes) {
			if (false == (Base64Decoder.isBase64Code(base64Byte) || isWhiteSpace(base64Byte))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isWhiteSpace(byte byteToCheck) {
		switch (byteToCheck) {
			case ' ' :
			case '\n' :
			case '\r' :
			case '\t' :
				return true;
			default :
				return false;
		}
	}
}
