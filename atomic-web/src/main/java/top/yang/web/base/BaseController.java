package top.yang.web.base;

import java.beans.PropertyEditorSupport;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import top.yang.constants.Globals;
import top.yang.time.DateUtils;

public class BaseController {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected HttpServletRequest request;

  protected HttpServletResponse response;

  protected HttpSession session;
  protected final String tempPath = System.getProperty("java.io.tmpdir").contains("\\") ? System.getProperty("java.io.tmpdir") : System.getProperty("java.io.tmpdir") + "/";

  /**
   * 将前台传递过来的日期格式的字符串，自动转化为Date类型
   */
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    // Date 类型转换
    binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) {
        try {
          setValue(DateUtils.parseDate(text));
        } catch (ParseException e) {
          logger.error("日期转化失败 {}", e.getMessage());
          e.printStackTrace();
        }
      }
    });
  }

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
