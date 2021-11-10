package top.yang.dubbo;

import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import top.yang.constants.Globals;

public class TraceUtil {

  public static void putTraceInto(RpcContext context) {
    String traceId = MDC.get(Globals.REQUEST_ID);
    if (StringUtils.hasText(traceId)) {
      context.setAttachment(Globals.REQUEST_ID, traceId);
    }

//    String uri = MDC.get(TRACE_EXTENDED_INFO);
//    if (StringUtils.isNotBlank(uri)) {
//      context.setAttachment(TRACE_EXTENDED_INFO, uri);
//    }
  }

  public static void getTraceFrom(RpcContext context) {
    String traceId = context.getAttachment(Globals.REQUEST_ID);
    if (StringUtils.hasText(traceId)) {
      setTraceId(traceId);
    }
//    String uri = context.getAttachment(TRACE_EXTENDED_INFO);
//    if (StringUtils.isNotEmpty(uri)) {
//      MDC.put(TRACE_EXTENDED_INFO, uri);
//    }
  }

  private static void setTraceId(String traceId) {
    MDC.put(Globals.REQUEST_ID, traceId);
  }
}