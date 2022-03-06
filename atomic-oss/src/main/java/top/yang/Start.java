package top.yang;

import top.yang.service.tencent.TencentFileService;

public class Start {

    public static void main(String[] args) {
        FileService tencentFileService = new TencentFileService();
        tencentFileService.listBuckets();
    }
}
