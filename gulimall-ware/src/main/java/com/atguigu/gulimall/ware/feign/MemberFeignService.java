package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 查询当前用户的全部收货地址
     * @return
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);

}
