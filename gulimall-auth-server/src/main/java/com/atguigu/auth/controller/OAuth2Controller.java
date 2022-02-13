package com.atguigu.auth.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.auth.feign.MemberFeignService;
import com.atguigu.auth.vo.SocialUser;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 */
@Log4j2
@Controller
public class OAuth2Controller {

    @Resource
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth/login")
    public String giteeLogin(@RequestParam("code") String code, HttpSession session) throws Exception {

        //认证网址-获取code
        //https://gitee.com/oauth/authorize?client_id=57bda1aed3e1d30855b20ed80f4f61783aa840d4aea066f51d0bcca98d321637&redirect_uri=http://auth.gulimall.com/oauth/login&response_type=code
        //获取access-token
        //https://gitee.com/oauth/token?grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
        Map<String, String> map = new HashMap<>();
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("client_id","57bda1aed3e1d30855b20ed80f4f61783aa840d4aea066f51d0bcca98d321637");
        map.put("redirect_uri","http://auth.gulimall.com/oauth/login");
        map.put("client_secret","904f1c3fea5ec4723608a9aebeef29efc48e5740b3ef899db559f8e05bc61a15");

        //1、根据用户授权返回的code换取access_token
        HttpResponse response1 = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), map, new HashMap<>());



        //2、处理
        if (response1.getStatusLine().getStatusCode() == 200) {
            //获取到了access_token,转为通用社交登录对象
            String json1 = EntityUtils.toString(response1.getEntity());

            SocialUser socialUserOnlyToken = JSON.parseObject(json1, SocialUser.class);


            Map<String,String> query = new HashMap<>();
            query.put("access_token",socialUserOnlyToken.getAccessToken());
            HttpResponse response2 = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<String, String>(), query);

            if (response2.getStatusLine().getStatusCode() == 200){
                String json2 = EntityUtils.toString(response2.getEntity());
                SocialUser socialUser = JSON.parseObject(json2, SocialUser.class);
                R oauthLogin = memberFeignService.oauthLogin(socialUser);
                if (oauthLogin.getCode() == 0) {
                    String json3 = JSON.toJSONString(oauthLogin.get("data"));
                    MemberResponseVo data = JSON.parseObject(json3, MemberResponseVo.class);
                    //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
                    //以后浏览器访问哪个网站就会带上这个网站的cookie
                    //1、默认发的令牌。当前域（解决子域session共享问题）
                    //2、使用JSON的序列化方式来序列化对象到Redis中
                    session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                    //2、登录成功跳回首页
                    return "redirect:http://gulimall.com";
                } else {
                    return "redirect:http://auth.gulimall.com/login.html";
                }
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
