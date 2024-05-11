package com.chuzhi.xzyx.ui.bean.bbs;

/**
 * 用户信息，token
 */
public class BbsUserEntity {

    private String token;
    private String token_type;
    private String username;
    private String nickname;
    private String avatar;

    public String getToken() {
        return token == null ? "" : token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_type() {
        return token_type == null ? "" : token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname == null?"":nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar == null ? "" : avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
