package top.yang.dubbo.filter;

import com.alibaba.fastjson.JSON;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.registry.Constants;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.dubbo.TraceUtil;
import top.yang.dubbo.pojo.DubboRequest;
import top.yang.dubbo.pojo.DubboResponse;


@Activate(order = 999, group = {Constants.PROVIDER_PROTOCOL, Constants.CONSUMER_PROTOCOL})
public class DubboTraceFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(DubboTraceFilter.class);
  private static final int SLOW_METHOD_THRESHOLD = 3000;

  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    // 处理 Trace 信息
    printRequest(invocation);
    // 执行前
    handleTraceId();
    long start = System.currentTimeMillis();
    Result result = invoker.invoke(invocation);
    long end = System.currentTimeMillis();
    // 执行后
    final long executionTime = end - start;
    printResponse(invocation, result, executionTime);
    return result;
  }


  private void printRequest(Invocation invocation) {
    DubboRequest request = new DubboRequest();
    request.setInterfaceClass(invocation.getInvoker().getInterface().getName());
    request.setMethodName(invocation.getMethodName());
    request.setArgs(getArgs(invocation));
    logger.info("RPC请求开始 , {}", request);
  }


  private void printResponse(Invocation invocation, Result result, long spendTime) {
    DubboResponse response = new DubboResponse();
    response.setInterfaceClassName(invocation.getInvoker().getInterface().getName());
    response.setMethodName(invocation.getMethodName());
    response.setResult(JSON.toJSONString(result.getValue()));
    response.setSpendTime(spendTime);
  }

  private Object[] getArgs(Invocation invocation) {
    Object[] args = invocation.getArguments();
    args = Arrays.stream(args).filter(arg -> {
      // 过滤大参
      if (arg instanceof byte[] || arg instanceof Byte[] || arg instanceof InputStream || arg instanceof File) {
        return false;
      }
      return true;
    }).toArray();
    return args;
  }

  private void handleTraceId() {
    RpcContext context = RpcContext.getContext();
    if (context.isConsumerSide()) {
      TraceUtil.putTraceInto(context);
    } else if (context.isProviderSide()) {
      TraceUtil.getTraceFrom(context);
    }
  }

}
