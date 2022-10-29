package top.lytree.oss;

import java.io.File;
import java.util.List;
import top.lytree.oss.model.Bucket;

public interface OSSService {

    /**
     * 创建 存储桶
     *
     * @param bucketNames 存储桶名称
     */
    void createBuckets(final String... bucketNames);

    /**
     * 获取所有存储桶
     */
    List<Bucket> listBuckets();

    /**
     * 删除指定存储桶
     *
     * @param bucketNames 存储桶名称
     */

    void deleteBuckets(final String... bucketNames);

    /**
     * 判断当前 bucketName 是否存在
     *
     * @param bucketName 存储桶
     *
     * @return 是否存在
     */
    Boolean doesBucketExist(String bucketName);

    /**
     * 简单上传文件
     *
     * @param bucketName 存储桶
     * @param key        存储文件唯一key
     * @param localFile  本地文件
     */
    void simpleUpload(String bucketName, String key, File localFile);
}
