package top.lytree.oss;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.model.AppendObjectRequest;
import com.qcloud.cos.model.AppendObjectResult;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.model.DeleteBucketRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.utils.VersionInfoUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import top.lytree.oss.model.Owner;

public class TencentOSSService extends AbstractClientService<COSClient> implements OSSService {

    public TencentOSSService(COSClient client) {
        super(client);
    }

    @Override
    public void createBuckets(String... bucketNames) {
        ArrayList<top.lytree.oss.model.Bucket> bucketList = new ArrayList<>();
        for (String bucket : bucketNames) {
            boolean bucketExist = client.doesBucketExist(bucket);
            if (!bucketExist) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
                Bucket clientBucket = client.createBucket(createBucketRequest);
                buildBucketList(bucketList, clientBucket);
            }
        }
        // 关闭客户端

    }

    @Override
    public List<top.lytree.oss.model.Bucket> listBuckets() {
        List<Bucket> buckets = client.listBuckets();
        ArrayList<top.lytree.oss.model.Bucket> bucketList = new ArrayList<>();
        for (Bucket bucket : buckets) {
            buildBucketList(bucketList, bucket);
        }
        // 关闭客户端
        return bucketList;
    }


    @Override
    public void deleteBuckets(String... bucketNames) {
        for (String bucket : bucketNames) {
            DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucket);
            client.deleteBucket(deleteBucketRequest);
        }
        // 关闭客户端

    }

    @Override
    public Boolean doesBucketExist(String bucketName) {
        // 关闭客户端

        return client.doesBucketExist(bucketName);
    }

    @Override
    public void simpleUpload(String bucketName, String key, File localFile) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        client.putObject(putObjectRequest);
        // 关闭客户端
    }


    /**
     * @param bucketName 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
     * @param key        文件的唯一key
     * @param files      追加文件
     */
    public void appendObject(String bucketName, String key, final File... files) {
        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        // 详细代码参见本页：简单操作 -> 创建 COSClient
        long nextAppendPosition = 0L;
        for (File file : files) {
            AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName, key, file);
            appendObjectRequest.setPosition(nextAppendPosition);
            AppendObjectResult appendObjectResult = client.appendObject(appendObjectRequest);
            nextAppendPosition = appendObjectResult.getNextAppendPosition();
        }
        // 关闭客户端
    }

    /**
     * 构建 存储桶列表信息
     *
     * @param bucketList 自定义存储桶信息列表
     * @param bucket     存储桶
     */
    private void buildBucketList(ArrayList<top.lytree.oss.model.Bucket> bucketList, Bucket bucket) {
        top.lytree.oss.model.Bucket result = new top.lytree.oss.model.Bucket();
        result.setCreationDate(bucket.getCreationDate());
        result.setLocation(bucket.getLocation());
        result.setName(bucket.getName());
        result.setRegion(client.getClientConfig().getRegion().getRegionName());
        result.setOwner(new Owner(bucket.getOwner().getId(), bucket.getOwner().getDisplayName()));
        result.setExtranetEndpoint(bucket.getBucketType() + "." + client.getClientConfig().getRegion().getRegionName() + ".myqcloud.com");
        bucketList.add(result);
    }

    /**
     * 获取 endpoint
     *
     * @return endpoint
     */
    private String createEndpoint() {
        // COS_REGION 请参照 https://cloud.tencent.com/document/product/436/6224
        return "cos." + client.getClientConfig().getRegion().getRegionName() + ".myqcloud.com";

    }
}
