package top.lytree.codec.net;

import top.lytree.exception.NetException;
import top.lytree.lang.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * url and html utils.
 *
 *
 * 
 */
public class UrlUtils {

    private static final String PATH_DELIMITER = "/";
    private static final String ENCODE_DELIMITER = "%2F";

    private static final Pattern patternForProtocal = Pattern.compile("[\\w]+://");
    /**
     * 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL 前缀表示文件: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";
    /**
     * URL 前缀表示jar: "jar:"
     */
    public static final String JAR_URL_PREFIX = "jar:";
    /**
     * URL 前缀表示war: "war:"
     */
    public static final String WAR_URL_PREFIX = "war:";
    /**
     * URL 协议表示文件: "file"
     */
    public static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL 协议表示Jar文件: "jar"
     */
    public static final String URL_PROTOCOL_JAR = "jar";
    /**
     * URL 协议表示zip文件: "zip"
     */
    public static final String URL_PROTOCOL_ZIP = "zip";
    /**
     * URL 协议表示WebSphere文件: "wsjar"
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL 协议表示JBoss zip文件: "vfszip"
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL 协议表示JBoss文件: "vfsfile"
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL 协议表示JBoss VFS资源: "vfs"
     */
    public static final String URL_PROTOCOL_VFS = "vfs";
    /**
     * Jar路径以及内部文件路径的分界符: "!/"
     */
    public static final String JAR_URL_SEPARATOR = "!/";
    /**
     * WAR路径及内部文件路径分界符
     */
    public static final String WAR_URL_SEPARATOR = "*/";

    /**
     * canonicalizeUrl
     *
     * @param url   url
     * @param refer refer
     * @return canonicalizeUrl
     */
    public static String canonicalizeUrl(String url, String refer) {
        URL base;
        try {
            try {
                base = new URL(refer);
            } catch (MalformedURLException e) {
                // the base is unsuitable, but the attribute may be abs on its own, so try that
                URL abs = new URL(refer);
                return abs.toExternalForm();
            }
            // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
            if (url.startsWith("?")) {
                url = base.getPath() + url;
            }
            URL abs = new URL(base, url);
            return abs.toExternalForm();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static String fixIllegalCharacterInUrl(String url) {
        return url.replace(" ", "%20").replaceAll("#+", "#");
    }

    public static String getHost(String url) {
        String host = url;
        int i = StringUtils.ordinalIndexOf(url, "/", 3);
        if (i > 0) {
            host = StringUtils.substring(url, 0, i);
        }
        return host;
    }


    public static String removeProtocol(String url) {
        return patternForProtocal.matcher(url).replaceAll("");
    }

    public static String getDomain(String url) {
        String domain = removeProtocol(url);
        int i = StringUtils.indexOf(domain, "/", 1);
        if (i > 0) {
            domain = StringUtils.substring(domain, 0, i);
        }
        return removePort(domain);
    }

    public static String removePort(String domain) {
        int portIndex = domain.indexOf(":");
        if (portIndex != -1) {
            return domain.substring(0, portIndex);
        } else {
            return domain;
        }
    }

    private static final Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)", Pattern.CASE_INSENSITIVE);

    public static String getCharset(String contentType) {
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            String charset = matcher.group(1);
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }
        return null;
    }


    public static String encode(String originUrl) throws UnsupportedEncodingException {
        return URLEncoder.encode(originUrl, StringUtils.DEFAULT_ENCODING).replace("+", "%20").replace("*", "%2A")
                .replace("%7E", "~");
    }

    // encode路径, 不包括分隔符
    public static String encodeEscapeDelimiter(String urlPath) throws UnsupportedEncodingException {
        StringBuilder pathBuilder = new StringBuilder();
        String[] pathSegmentsArr = urlPath.split(PATH_DELIMITER);

        boolean isFirstSegMent = true;
        for (String pathSegment : pathSegmentsArr) {
            if (isFirstSegMent) {
                pathBuilder.append(encode(pathSegment));
                isFirstSegMent = false;
            } else {
                pathBuilder.append(PATH_DELIMITER).append(encode(pathSegment));
            }
        }
        if (urlPath.endsWith(PATH_DELIMITER)) {
            pathBuilder.append(PATH_DELIMITER);
        }
        return pathBuilder.toString();
    }

    // encode url path, replace the continuous slash with %2F except the first slash
    public static String encodeUrlPath(String urlPath) throws UnsupportedEncodingException {
        if (urlPath.length() <= 1) {
            return urlPath;
        }

        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(PATH_DELIMITER);
        int start = 1, end = 1;
        while (end < urlPath.length()) {
            if ('/' == urlPath.charAt(end)) {
                if ('/' == urlPath.charAt(end - 1)) {
                    pathBuilder.append(ENCODE_DELIMITER);
                } else {
                    pathBuilder.append(encode(urlPath.substring(start, end))).append(PATH_DELIMITER);
                }
                start = end + 1;
            }
            end++;
        }
        if (start < end) {
            pathBuilder.append(encode(urlPath.substring(start, end)));
        }
        return pathBuilder.toString();
    }

    /**
     * Decode a string for use in the path of a URL; uses URLDecoder.decode, which decodes a string for use in the query portion of a URL.
     *
     * @param value The value to decode
     * @return The decoded value if parameter is not null, otherwise, null is returned.
     */
    public static String urlDecode(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return URLDecoder.decode(value, StringUtils.DEFAULT_ENCODING);

        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将{@link URI}转换为{@link URL}
     *
     * @param uri {@link URI}
     * @return URL对象
     * @throws NetException {@link MalformedURLException}包装，URI格式有问题时抛出
     * @see URI#toURL()
     * 
     */
    public static URL url(URI uri) throws NetException {
        if (null == uri) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new NetException(e);
        }
    }

    /**
     * Data URI Scheme封装，数据格式为Base64。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br> 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param data     编码后的数据
     * @return Data URI字符串
     * 
     */
    public static String getDataUriBase64(String mimeType, String data) {
        return getDataUri(mimeType, null, "base64", data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br> 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     * 
     */
    public static String getDataUri(String mimeType, String encoding, String data) {
        return getDataUri(mimeType, null, encoding, data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br> 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param charset  可选项（null表示无），源文本的字符集编码方式
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     * 
     */
    public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
        final StringBuilder builder = new StringBuilder("data:");
        if (StringUtils.isNotBlank(mimeType)) {
            builder.append(mimeType);
        }
        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }
        if (StringUtils.isNotBlank(encoding)) {
            builder.append(';').append(encoding);
        }
        builder.append(',').append(data);

        return builder.toString();
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url URL
     * @return URL对象
     */
    public static URL url(String url) {
        return url(url, null);
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url     URL
     * @param handler {@link URLStreamHandler}
     * @return URL对象
     * 
     */
    public static URL url(String url, URLStreamHandler handler) {
        if (null == url) {
            return null;
        }

        // 兼容Spring的ClassPath路径
        if (url.startsWith(CLASSPATH_URL_PREFIX)) {
            url = url.substring(CLASSPATH_URL_PREFIX.length());
            return Thread.currentThread().getContextClassLoader().getResource(url);
        }

        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            // 尝试文件路径
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new NetException(e);
            }
        }
    }

}
