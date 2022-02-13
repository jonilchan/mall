package com.atguigu.auth.vo;

import lombok.Data;

@Data
public class SocialUser {

    private String id;

    private String email;

    private String name;

    private String avatarUrl;

    private String accessToken;

}
