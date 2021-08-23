package top.yang.net.commom;

import java.util.concurrent.ExecutorService;

public final class ClientConfiguration implements Cloneable {

  /**
   * 特殊默认域名
   */
  public static String defaultRsHost = "rs.qiniu.com";
  public static String defaultApiHost = "api.qiniu.com";
  public static String defaultUcHost = "uc.qbox.me";
  /**
   * 空间相关上传管理操作是否使用 https , 默认 是
   */
  public boolean useHttpsDomains = true;
  /**
   * 空间相关上传管理操作是否使用代理加速上传，默认 是
   */
  public boolean accUpHostFirst = true;
  /**
   * 使用 AutoRegion 时，如果从区域信息得到上传 host 失败，使用默认的上传域名上传，默认 是 upload.qiniup.com, upload-z1.qiniup.com, upload-z2.qiniup.com, upload-na0.qiniup.com, upload-as0.qiniup.com
   */
  public boolean useDefaultUpHostIfNone = true;
  /**
   * 使用分片 V2 上传时的分片大小 范围为：1M ~ 1GB， 注：每个文件最大分片数量为 10000
   */
  public int resumableUploadAPIV2BlockSize = Constants.BLOCK_SIZE;
  /**
   * 分片上传每个文件传时的最大并发任务数，并发数量会影响内存使用，请合理配置 当 resumableUploadMaxConcurrentTaskCount 小于或等于 1 时，使用同步上传，resumableUploadConcurrentTaskExecutorService 不被使用 当
   * resumableUploadMaxConcurrentTaskCount 大于 1 时，使用并发上传
   * <p>
   * 分片上传，每个上传操作会占用 blockSize 大小内存，blockSize 也即分片大小， 在分片 v1 中 blockSize 为 4M； 分片 v2 可自定义，定义方式为：Configuration.resumableUploadAPIV2BlockSize，范围为：1M ~ 1GB，分片 v2 需要注意每个文件最大分片数量为 10000；
   * 当采用并发分片时，占用内存大小和当时启用并发任务数量有关，即：blockSize * 并发数量， 并发任务数量配置方式：Configuration.resumableUploadMaxConcurrentTaskCount
   */
  public int resumableUploadMaxConcurrentTaskCount = 1;
  /**
   * 分片上传并发任务的 ExecutorService 当 resumableUploadMaxConcurrentTaskCount 小于或等于 1，此设置无效； 当 resumableUploadMaxConcurrentTaskCount 大于 1 且 resumableUploadConcurrentTaskExecutorService
   * 为空，则会创建 Executors.newFixedThreadPool(maxConcurrentTaskCount) 当 resumableUploadMaxConcurrentTaskCount 大于 1 且 resumableUploadConcurrentTaskExecutorService 不为空，则直接使用
   * resumableUploadConcurrentTaskExecutorService
   */
  public ExecutorService resumableUploadConcurrentTaskExecutorService = null;
  /**
   * 如果文件大小大于此值则使用断点上传, 否则使用Form上传
   */
  public int putThreshold = Constants.BLOCK_SIZE;
  /**
   * 连接超时时间 单位秒(默认10s)
   */
  public int connectTimeout = Constants.CONNECT_TIMEOUT;
  /**
   * 写超时时间 单位秒(默认 0 , 不超时)
   */
  public int writeTimeout = Constants.WRITE_TIMEOUT;
  /**
   * 回复超时时间 单位秒(默认30s)
   */
  public int readTimeout = Constants.READ_TIMEOUT;
  /**
   * 底层HTTP库所有的并发执行的请求数量
   */
  public int dispatcherMaxRequests = Constants.DISPATCHER_MAX_REQUESTS;
  /**
   * 底层HTTP库对每个独立的Host进行并发请求的数量
   */
  public int dispatcherMaxRequestsPerHost = Constants.DISPATCHER_MAX_REQUESTS_PER_HOST;
  /**
   * 底层HTTP库中复用连接对象的最大空闲数量
   */
  public int connectionPoolMaxIdleCount = Constants.CONNECTION_POOL_MAX_IDLE_COUNT;
  /**
   * 底层HTTP库中复用连接对象的回收周期（单位分钟）
   */
  public int connectionPoolMaxIdleMinutes = Constants.CONNECTION_POOL_MAX_IDLE_MINUTES;
  /**
   * 上传失败重试次数
   */
  public int retryMax = 5;
  /**
   * 外部dns
   */
  public Dns dns;
  /**
   * 代理对象
   */
  public ProxyConfiguration proxy;

  public ClientConfiguration() {
  }

  public ClientConfiguration clone() {
    try {
      return (ClientConfiguration) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }

}
