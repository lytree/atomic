package top.yang.io;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.string.CharUtils;
import top.yang.string.StringUtils;

/**
 * url and html utils.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class UrlUtils {

  private static final Logger log = LoggerFactory.getLogger(UrlUtils.class);
  private static final String PATH_DELIMITER = "/";
  private static final String ENCODE_DELIMITER = "%2F";

  private static final Pattern patternForProtocal = Pattern.compile("[\\w]+://");

  /**
   * canonicalizeUrl
   * <br>
   * Borrowed from Jsoup.
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


  public static String encode(String originUrl) {
    try {
      return URLEncoder.encode(originUrl, StringUtils.DEFAULT_ENCODING).replace("+", "%20").replace("*", "%2A")
          .replace("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      log.error("URLEncoder error, exception: {}", e);
    }
    return null;
  }

  // encode路径, 不包括分隔符
  public static String encodeEscapeDelimiter(String urlPath) {
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
  public static String encodeUrlPath(String urlPath) {
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
}
