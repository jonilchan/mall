package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;


    //阿里云OSS测试
    @Test
    public void uploadTest() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("D:\\Photo\\june 5.jpg");

        ossClient.putObject("jonil", "june 5 test.jpg", inputStream);

        System.out.println("上传完成！");
    }

}
