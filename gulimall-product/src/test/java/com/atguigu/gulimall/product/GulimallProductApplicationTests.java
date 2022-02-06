package com.atguigu.gulimall.product;

import com.aliyun.oss.OSSClient;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testStringRedisTemplate() {

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        ops.set("hello", "world" + UUID.randomUUID());

        String hello = ops.get("hello");

        System.out.println(hello);
    }


}
