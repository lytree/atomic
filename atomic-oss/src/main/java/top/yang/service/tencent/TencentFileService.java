package top.yang.service.tencent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import top.yang.FileService;
import top.yang.model.Bucket;
import top.yang.service.tencent.Headers;
import top.yang.service.tencent.utils.VersionInfoUtils;
import top.yang.utils.XmlResponsesSaxParser;

public class TencentFileService implements FileService {

    private OkHttpClient okHttpClient;

    public TencentFileService() {
        okHttpClient = new OkHttpClient();
        System.out.println("初始化完成");
    }

    @Override
    public List<Bucket> listBuckets() {
        Builder builder = new Builder();
        builder.addHeader(Headers.USER_AGENT, VersionInfoUtils.getUserAgent());
        builder.addHeader(Headers.HOST, "cos.ap-nanjing.myqcloud.com");
        builder.addHeader(Headers.COS_AUTHORIZATION,
                "q-sign-algorithm=sha1&q-ak=AKID18yxJZ269gD_gJ_AvyMloDPZSQKqth7r7orB6Bbxm0s5E8RWHWfDlrHD13j5Dcey&q-sign-time=1646494345;1646495245&q-key-time=1646494345;1646495245&q-header-list=host&q-url-param-list=&q-signature=88833ec356fb75692a67faf679005c5662cfea6f");
        builder.addHeader(Headers.SECURITY_TOKEN,
                "t9a1zW3tmPp9WqoG1E2DRYVkcwQMOYha7f2b641d50b637107d714275e5b733fdsYEgxfCO-a8JjRdcjnyFIT98q-GRE4pHXBK09i_uN2ndL2KDCXyemXtDnbVrDveSLlfW4fPOumCDTCma5U-FsVvRjQq-bT4W5gygc87HzO5FLyd4gwy4ywvgHHFe2hbYLi-I_SqdisfCeOJaaPyMU9yjevf7v1Xvyhc4tm6G4VrEvmJqbFGDign6rp8BjFsu");
        builder.get();
        builder.url("https://cos.ap-nanjing.myqcloud.com/");
        try {
            Response execute = okHttpClient.newCall(builder.build()).clone().execute();

            List<Bucket> buckets = new XmlResponsesSaxParser().parseListMyBucketsResponse(execute.body().source().inputStream()).getBuckets();
            System.out.println(buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String buildUrlAndHost(
            String bucket, String key, boolean isServiceRequest) {
        key = formatKey(key);
        String endpoint = "";
        String endpointAddr = "";
        return "";
    }

    private String formatKey(String key) {
        if (key == null) {
            return "/";
        }
        if (!key.startsWith("/")) {
            key = "/" + key;
        }
        return key;
    }

    public static void main(String[] args) throws IOException {
        String s1 = ""
                + "<ListAllMyBucketsResult:tencent>\n"
                + "\t<Owner>\n"
                + "\t\t<ID>qcs::cam::uin/100007925180:uin/100007925180</ID>\n"
                + "\t\t<DisplayName>100007925180</DisplayName>\n"
                + "\t</Owner>\n"
                + "\t<Buckets>\n"
                + "\t\t<Bucket>\n"
                + "\t\t\t<Name>admin-1257980368</Name>\n"
                + "\t\t\t<Location>ap-nanjing</Location>\n"
                + "\t\t\t<CreationDate>2021-08-01T06:26:56Z</CreationDate>\n"
                + "\t\t\t<BucketType>cos</BucketType>\n"
                + "\t\t</Bucket>\n"
                + "\t\t<Bucket>\n"
                + "\t\t\t<Name>config-1257980368</Name>\n"
                + "\t\t\t<Location>ap-nanjing</Location>\n"
                + "\t\t\t<CreationDate>2021-05-30T12:55:41Z</CreationDate>\n"
                + "\t\t\t<BucketType>cos</BucketType>\n"
                + "\t\t</Bucket>\n"
                + "\t\t<Bucket>\n"
                + "\t\t\t<Name>image-1257980368</Name>\n"
                + "\t\t\t<Location>ap-nanjing</Location>\n"
                + "\t\t\t<CreationDate>2021-02-24T13:32:37Z</CreationDate>\n"
                + "\t\t\t<BucketType>cos</BucketType>\n"
                + "\t\t</Bucket>\n"
                + "\t\t<Bucket>\n"
                + "\t\t\t<Name>image-edu-1257980368</Name>\n"
                + "\t\t\t<Location>ap-nanjing</Location>\n"
                + "\t\t\t<CreationDate>2020-12-18T12:20:23Z</CreationDate>\n"
                + "\t\t\t<BucketType>cos</BucketType>\n"
                + "\t\t</Bucket>\n"
                + "\t\t<Bucket>\n"
                + "\t\t\t<Name>video-edu-1257980368</Name>\n"
                + "\t\t\t<Location>ap-nanjing</Location>\n"
                + "\t\t\t<CreationDate>2020-12-18T12:21:13Z</CreationDate>\n"
                + "\t\t\t<BucketType>cos</BucketType>\n"
                + "\t\t</Bucket>\n"
                + "\t</Buckets>\n"
                + "</ListAllMyBucketsResult:tencent>";
        byte[] bytes = s1.getBytes(StandardCharsets.UTF_8);
        List<Bucket> buckets = new XmlResponsesSaxParser().parseListMyBucketsResponse(new ByteArrayInputStream(bytes)).getBuckets();
        System.out.println(buckets);
    }
}
