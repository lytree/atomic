package top.lytree.dubbo;

import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import top.lytree.oss.model.constants.Globals;


public class TraceUtil {

    public static void putTraceInto(RpcContext context) {
        String traceId = MDC.get(Globals.REQUEST_ID);
        if (StringUtils.hasText(traceId)) {
            context.setAttachment(Globals.REQUEST_ID, traceId);
        }

    }

    public static void getTraceFrom(RpcContext context) {
        String traceId = context.getAttachment(Globals.REQUEST_ID);
        if (StringUtils.hasText(traceId)) {
            setTraceId(traceId);
        }
    }

    private static void setTraceId(String traceId) {
        MDC.put(Globals.REQUEST_ID, traceId);
    }
}