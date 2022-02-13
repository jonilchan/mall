package com.atguigu.auth.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.auth.feign.MemberFeignService;
import com.atguigu.auth.feign.ThirdPartyFeignService;
import com.atguigu.auth.vo.UserLoginVo;
import com.atguigu.auth.vo.UserRegistVo;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class LoginController {

    @Resource
    private ThirdPartyFeignService thirdPartyFeignService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null){
            return "login";
        } else {
            return "redirect:http://gulimall.com";
        }
    }


    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){

        //接口防止恶意使用
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000){
                //60秒内不重发信息
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //验证码采用随机生成的方式
        int codeNum = (int) ((Math.random() * 9 + 1) * 100000);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();
        //在redis中储存验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, redisStorage, 10, TimeUnit.MINUTES);

//        因为第三方短信服务要钱，而且要审核，这里的短信就做在这里，但是不启用（有需要的可以去gulimall-third-party中更改相关信息，启用该功能），改为控制台输出信息
//        thirdPartyFeignService.sendCode(phone, String.valueOf(codeNum));
        log.error("本次注册的验证码为{}", codeNum);

        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes attributes){

        if (result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //验证码
        String code = vo.getCode();

        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());

        if (!StringUtils.isEmpty(redisCode)){

            if (code.equalsIgnoreCase(redisCode.split("_")[0])){
                //令牌机制-删除验证码
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //验证码通过，调用远程服务进行注册
                R regist = memberFeignService.regist(vo);
                if (regist.getCode() == 0){
                    //成功-跳转到登录页面
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg", (String) regist.get("msg"));
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            } else {
                //验证码错误回到注册页面
                HashMap<String, String> errors = new HashMap<>();
                errors.put("msg", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            //验证码错误回到注册页面
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg", "验证码为空");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session){

        R login = memberFeignService.login(vo);
        if (login.getCode() == 0){
            String json = JSON.toJSONString(login.get("data"));
            MemberResponseVo data = JSON.parseObject(json, MemberResponseVo.class);
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg", (String) login.get("msg"));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}
