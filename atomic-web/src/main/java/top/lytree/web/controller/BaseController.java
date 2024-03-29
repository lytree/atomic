package top.lytree.web.controller;

import java.text.MessageFormat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ModelAttribute;
import top.lytree.model.constants.Globals;

public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected HttpSession session;
    protected final String tempPath = System.getProperty("java.io.tmpdir").contains("\\") ? System.getProperty("java.io.tmpdir") : System.getProperty("java.io.tmpdir") + "/";

    public String requestId() {
        return MDC.get(Globals.TRACE_ID);
    }

    /**
     * 重定向
     *
     * @param format
     * @param arguments
     * @return
     */
    public static String redirect(String format, Object... arguments) {
        return "redirect:" + MessageFormat.format(format, arguments);
    }

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {

        this.request = request;

        this.response = response;

        this.session = request.getSession();

    }
}
