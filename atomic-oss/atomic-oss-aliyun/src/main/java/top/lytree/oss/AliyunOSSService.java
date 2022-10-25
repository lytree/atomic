package top.lytree.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import top.lytree.oss.model.Owner;

public class AliyunOSSService extends AbstractClientService<OSS> implements OSSService {

    public AliyunOSSService(OSS client) {
        super(client);
    }

    @Override
    public void createBuckets(String... bucketNames) {
        ArrayList<top.lytree.oss.model.Bucket> bucketList = new ArrayList<>();
        for (String bucket : bucketNames) {

            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
            Bucket clientBucket = client.createBucket(createBucketRequest);
            buildBucketList(bucketList, clientBucket);
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
            client.deleteBucket(bucket);
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
        PutObjectResult putObjectResult = client.putObject(putObjectRequest);
        // 关闭客户端
        
    }

    private void buildBucketList(ArrayList<top.lytree.oss.model.Bucket> bucketList, Bucket bucket) {
        top.lytree.oss.model.Bucket result = new top.lytree.oss.model.Bucket();
        result.setCreationDate(bucket.getCreationDate());
        result.setLocation(bucket.getLocation());
        result.setName(bucket.getName());
        result.setRegion(bucket.getRegion());
        result.setOwner(new Owner(bucket.getOwner().getId(), bucket.getOwner().getDisplayName()));
        result.setExtranetEndpoint(bucket.getExtranetEndpoint());
        bucketList.add(result);
    }
}
