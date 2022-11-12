package top.lytree.dubbo;

import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;


public class TraceUtil {

    public static final String TRACE_ID = "traceId";

    public static void putTraceInto(RpcContext context) {
        String traceId = MDC.get(TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            context.setAttachment(TRACE_ID, traceId);
        }

    }

    public static void getTraceFrom(RpcContext context) {
        String traceId = context.getAttachment(TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            setTraceId(traceId);
        }
    }

    private static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }
}