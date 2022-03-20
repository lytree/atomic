package top.yang.oss;


import java.util.List;
import top.yang.oss.model.Bucket;

public interface FileService {

    List<Bucket> listBuckets();
    
}
