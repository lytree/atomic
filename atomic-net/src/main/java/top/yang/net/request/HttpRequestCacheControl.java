package top.yang.net.request;

import okhttp3.CacheControl;

/**
 * 缓存控制
 * builder.noCache();//不使用缓存，全部走网络
 * builder.noStore();//不使用缓存，也不存储缓存
 * builder.onlyIfCached();//只使用缓存
 * builder.noTransform();//禁止转码
 * builder.maxAge(10, TimeUnit.MILLISECONDS);//指示客户机可以接收生存期不大于指定时间的响应。
 * builder.maxStale(10, TimeUnit.SECONDS);//指示客户机可以接收超出超时期间的响应消息
 * builder.minFresh(10, TimeUnit.SECONDS);//指示客户机可以接收响应时间小于当前时间加上指定时间的响应。
 */
public class HttpRequestCacheControl {
    /**
     * 不使用缓存，全部走网络
     *
     * @return CacheControl
     */
    public static CacheControl noCache() {
        return new CacheControl.Builder().noCache().build();
    }
}
