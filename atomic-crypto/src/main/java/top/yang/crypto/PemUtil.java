package top.yang.crypto;


import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.commons.io.IOUtils;
import top.yang.lang.StringUtils;

/**
 * PEM(Privacy Enhanced Mail)格式相关工具类。（基于Bouncy Castle）
 *
 * <p>
 * PEM一般为文本格式，以 -----BEGIN... 开头，以 -----END... 结尾，中间的内容是 BASE64 编码。
 * <p>
 * 这种格式可以保存证书和私钥，有时我们也把PEM格式的私钥的后缀改为 .key 以区别证书与私钥。
 *
 * @author looly
 * @since 5.1.6
 */
public class PemUtil {

    /**
     * 读取PEM格式的私钥
     *
     * @param pemStream pem流
     * @return {@link PrivateKey}
     * @since 4.5.2
     */
    public static PrivateKey readPemPrivateKey(InputStream pemStream) throws IOException {
        return (PrivateKey) readPemKey(pemStream);
    }

    /**
     * 读取PEM格式的公钥
     *
     * @param pemStream pem流
     * @return {@link PublicKey}
     * @since 4.5.2
     */
    public static PublicKey readPemPublicKey(InputStream pemStream) throws IOException {
        return (PublicKey) readPemKey(pemStream);
    }

    /**
     * 从pem文件中读取公钥或私钥<br> 根据类型返回 {@link PublicKey} 或者 {@link PrivateKey}
     *
     * @param keyStream pem流
     * @return {@link Key}，null表示无法识别的密钥类型
     * @since 5.1.6
     */
    public static Key readPemKey(InputStream keyStream) throws IOException {
        final PemObject object = readPemObject(keyStream);
        final String type = object.getType();
        if (StringUtils.isNotBlank(type)) {
            //private
            if (type.endsWith("EC PRIVATE KEY")) {
                return KeyUtil.generatePrivateKey("EC", object.getContent());
            }
            if (type.endsWith("PRIVATE KEY")) {
                return KeyUtil.generateRSAPrivateKey(object.getContent());
            }

            // public
            if (type.endsWith("EC PUBLIC KEY")) {
                return KeyUtil.generatePublicKey("EC", object.getContent());
            } else if (type.endsWith("PUBLIC KEY")) {
                return KeyUtil.generateRSAPublicKey(object.getContent());
            } else if (type.endsWith("CERTIFICATE")) {
                return KeyUtil.readPublicKeyFromCert(new ByteArrayInputStream(object.getContent()));
            }
        }

        //表示无法识别的密钥类型
        return null;
    }

    /**
     * 从pem流中读取公钥或私钥
     *
     * @param keyStream pem流
     * @return 密钥bytes
     * @since 5.1.6
     */
    public static byte[] readPem(InputStream keyStream) throws IOException {
        PemObject pemObject = readPemObject(keyStream);
        if (null != pemObject) {
            return pemObject.getContent();
        }
        return null;
    }

    /**
     * 读取pem文件中的信息，包括类型、头信息和密钥内容
     *
     * @param keyStream pem流
     * @return {@link PemObject}
     * @since 4.5.2
     */
    public static PemObject readPemObject(InputStream keyStream) throws IOException {
        return readPemObject(new InputStreamReader(keyStream));
    }

    /**
     * 读取pem文件中的信息，包括类型、头信息和密钥内容
     *
     * @param reader pem Reader
     * @return {@link PemObject}
     * @since 5.1.6
     */
    public static PemObject readPemObject(Reader reader) throws IOException {
        PemReader pemReader = null;
        try {
            pemReader = new PemReader(reader);
            return pemReader.readPemObject();
        } finally {
            IOUtils.close(pemReader);
        }
    }

    /**
     * 读取OpenSSL生成的ANS1格式的Pem私钥文件，必须为PKCS#1格式
     *
     * @param keyStream 私钥pem流
     * @return {@link PrivateKey}
     */
    public static PrivateKey readSm2PemPrivateKey(InputStream keyStream) throws IOException {
        try {
            return KeyUtil.generatePrivateKey("sm2", ECKeyUtil.createOpenSSHPrivateKeySpec(readPem(keyStream)));
        } finally {
            IOUtils.close(keyStream);
        }
    }

    /**
     * 将私钥或公钥转换为PEM格式的字符串
     *
     * @param type    密钥类型（私钥、公钥、证书）
     * @param content 密钥内容
     * @return PEM内容
     * @since 5.5.9
     */
    public static String toPem(String type, byte[] content) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        writePemObject(type, content, stringWriter);
        return stringWriter.toString();
    }

    /**
     * 写出pem密钥（私钥、公钥、证书）
     *
     * @param type      密钥类型（私钥、公钥、证书）
     * @param content   密钥内容，需为PKCS#1格式
     * @param keyStream pem流
     * @since 5.1.6
     */
    public static void writePemObject(String type, byte[] content, OutputStream keyStream) throws IOException {
        writePemObject(new PemObject(type, content), keyStream);
    }

    /**
     * 写出pem密钥（私钥、公钥、证书）
     *
     * @param type    密钥类型（私钥、公钥、证书）
     * @param content 密钥内容，需为PKCS#1格式
     * @param writer  pemWriter
     * @since 5.5.9
     */
    public static void writePemObject(String type, byte[] content, Writer writer) throws IOException {
        writePemObject(new PemObject(type, content), writer);
    }

    /**
     * 写出pem密钥（私钥、公钥、证书）
     *
     * @param pemObject pem对象，包括密钥和密钥类型等信息
     * @param keyStream pem流
     * @since 5.1.6
     */
    public static void writePemObject(PemObjectGenerator pemObject, OutputStream keyStream) throws IOException {
        writePemObject(pemObject, new OutputStreamWriter(keyStream));
    }

    /**
     * 写出pem密钥（私钥、公钥、证书）
     *
     * @param pemObject pem对象，包括密钥和密钥类型等信息
     * @param writer    pemWriter
     * @since 5.5.9
     */
    public static void writePemObject(PemObjectGenerator pemObject, Writer writer) throws IOException {
        final PemWriter pemWriter = new PemWriter(writer);
        try {
            pemWriter.writeObject(pemObject);
        } finally {
            IOUtils.close(pemWriter);
        }
    }
}
