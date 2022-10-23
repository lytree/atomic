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
        client.shutdown();
    }

    @Override
    public List<top.lytree.oss.model.Bucket> listBuckets() {
        List<Bucket> buckets = client.listBuckets();
        ArrayList<top.lytree.oss.model.Bucket> bucketList = new ArrayList<>();
        for (Bucket bucket : buckets) {
            buildBucketList(bucketList, bucket);
        }
        // 关闭客户端
        client.shutdown();
        return bucketList;
    }


    @Override
    public void deleteBuckets(String... bucketNames) {
        for (String bucket : bucketNames) {
            DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucket);
            client.deleteBucket(deleteBucketRequest);
        }
        // 关闭客户端
        client.shutdown();
    }

    @Override
    public Boolean doesBucketExist(String bucketName) {
        boolean bucketExist = client.doesBucketExist(bucketName);
        // 关闭客户端
        client.shutdown();
        return bucketExist;
    }

    @Override
    public void simpleUpload(String bucketName, String key, File localFile) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        client.putObject(putObjectRequest);
        // 关闭客户端
        client.shutdown();
    }


    public void formUpload(String bucketName, Map<String, String> formFields, File localFile) throws IOException {
// 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式

// COS_REGION 请参照 https://cloud.tencent.com/document/product/436/6224
        String endpoint = "cos.{COS_REGION}.myqcloud.com";
// 对象键(Key)是对象在存储桶中的唯一标识。
        String key = "exampleobject";
// SECRETID 和 SECRETKEY 请登录访问管理控制台进行查看和管理
        String secretId = "AKIDXXXXXXXX";
        String seretKey = "1A2Z3YYYYYYYYYY";

        String contentType = "image/jpeg";

        long startTimestamp = System.currentTimeMillis() / 1000;
        long endTimestamp = startTimestamp + 30 * 60;
        String endTimestampStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(endTimestamp * 1000);

        String keyTime = startTimestamp + ";" + endTimestamp;
        String boundary = "----WebKitFormBoundaryZBPbaoYE2gqeB21N";

// 设置表单的body字段值
        formFields.put("q-sign-algorithm", "sha1");
        formFields.put("key", key);
        formFields.put("q-ak", secretId);
        formFields.put("q-key-time", keyTime);

// 构造policy，参考文档: https://cloud.tencent.com/document/product/436/14690
        String policy = "{\n" +
                "    \"expiration\": \"" + endTimestampStr + "\",\n" +
                "    \"conditions\": [\n" +
                "        { \"bucket\": \"" + bucketName + "\" },\n" +
                "        { \"q-sign-algorithm\": \"sha1\" },\n" +
                "        { \"q-ak\": \"" + secretId + "\" },\n" +
                "        { \"q-sign-time\":\"" + keyTime + "\" }\n" +
                "    ]\n" +
                "}";

// policy需要base64后算放入表单中
        String encodedPolicy = new String(Base64.encodeBase64(policy.getBytes()));
// 设置policy
        formFields.put("policy", encodedPolicy);
// 根据编码后的policy和secretKey计算签名
        COSSigner cosSigner = new COSSigner();
        String signature = cosSigner.buildPostObjectSignature(seretKey, keyTime, policy);
// 设置签名
        formFields.put("q-signature", signature);

// 根据以上表单参数，构造最开始的body部分
        String formBody = buildPostObjectBody(boundary, formFields,
                localFile.getAbsolutePath(), "image/jpeg");

        HttpURLConnection conn = null;
        try {
            String urlStr = "http://" + bucketName + "." + endpoint;
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", VersionInfoUtils.getUserAgent());
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 写入表单的最开始部分
            out.write(formBody.getBytes());

            // 将文件内容写入到输出流中
            DataInputStream in = new DataInputStream(new FileInputStream(localFile));
            int readBytes;
            byte[] bytes = new byte[4096];
            while ((readBytes = in.read(bytes)) != -1) {
                out.write(bytes, 0, readBytes);
            }
            in.close();

            // 添加最后一个分割符，行首和行尾都是--
            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取响应头部
            for (Map.Entry<String, List<String>> entries : conn.getHeaderFields().entrySet()) {
                StringBuilder values = new StringBuilder();
                for (String value : entries.getValue()) {
                    values.append(value).append(",");
                }
                if (entries.getKey() == null) {
                    System.out.println("reponse line:" + values);
                } else {
                    System.out.println(entries.getKey() + ":" + values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void appendObject(final File... file) {
        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        // 详细代码参见本页：简单操作 -> 创建 COSClient

        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = "examplebucket-1250000000";
        // 对象键(Key)是对象在存储桶中的唯一标识。
        String key = "exampleobject";

        // 这里假定要追加上传的是这两个文件里的内容
        File part1File = new File("/path/to/part1File");
        File part2File = new File("/path/to/part2File");

        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName, key, part1File);
        appendObjectRequest.setPosition(0L);
        AppendObjectResult appendObjectResult = client.appendObject(appendObjectRequest);
        long nextAppendPosition = appendObjectResult.getNextAppendPosition();
        System.out.println(nextAppendPosition);

        appendObjectRequest = new AppendObjectRequest(bucketName, key, part2File);
        appendObjectRequest.setPosition(nextAppendPosition);
        appendObjectResult = client.appendObject(appendObjectRequest);
        nextAppendPosition = appendObjectResult.getNextAppendPosition();
        System.out.println(nextAppendPosition);

        // 关闭客户端
        client.shutdown();
    }

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

}
