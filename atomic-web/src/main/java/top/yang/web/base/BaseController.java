package top.yang.web.base;

import java.text.MessageFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ModelAttribute;
import top.yang.constants.Globals;

public class BaseController {

  protected HttpServletRequest request;

  protected HttpServletResponse response;

  protected HttpSession session;
  protected final String tempPath = System.getProperty("java.io.tmpdir").contains("\\") ? System.getProperty("java.io.tmpdir") : System.getProperty("java.io.tmpdir") + "/";

  public String requestId() {
    return MDC.get(Globals.REQUEST_ID);
  }

  /**
   * 重定向
   *
   * @param format
   * @param arguments
   * @return
   */
  public static String redirect(String format, Object... arguments) {
    return new StringBuffer("redirect:").append(MessageFormat.format(format, arguments)).toString();
  }

  @ModelAttribute
  public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {

    this.request = request;

    this.response = response;

    this.session = request.getSession();

  }
}
