package top.lytree.web.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestUtil {
  private final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

  public static final String LOCALHOST_IP_V4 = "127.0.0.1";
  public static final String LOCALHOST_IP_V6 = "0:0:0:0:0:0:0:1";
  public static final String UNKNOWN = "unknown";
  public static final int IP_ADDRESS_ONE_LENGTH = 16;

  /**
   * 获取请求真实IP地址
   */
  public static String getRequestIp(HttpServletRequest request) {
    //通过HTTP代理服务器转发时添加
    String ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
      // 从本地访问时根据网卡取本机配置的IP
      if (LOCALHOST_IP_V4.equals(ipAddress) || LOCALHOST_IP_V6.equals(ipAddress)) {
        InetAddress inetAddress = null;
        try {
          inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        assert inetAddress != null;
        ipAddress = inetAddress.getHostAddress();
      }
    }
    // 通过多个代理转发的情况，第一个IP为客户端真实IP，多个IP会按照','分割
    if (ipAddress != null && ipAddress.length() >= IP_ADDRESS_ONE_LENGTH) {
      if (ipAddress.indexOf(",") > 0) {
        ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
      }
    }
    return ipAddress;
  }
}
