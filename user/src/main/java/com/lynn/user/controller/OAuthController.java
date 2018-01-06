package com.lynn.user.controller;

import com.lynn.user.model.in.AccessTokenIn;
import com.lynn.user.model.in.AuthorizeIn;
import com.lynn.user.model.in.RefreshTokenIn;
import com.lynn.user.model.out.AccessTokenOut;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 提供登录统一的登录界面
 * 用户输入账号和密码，如果请求成功，调用authorize接口
 * 客户端拿到code获得access_token
 * 客户端拿到access_token和openid可以获得用户信息
 */
@RequestMapping("oauth2")
@RestController
public class OAuthController extends BaseController{

    /**
     * 授权页面
     * 生成code，重定向到response_uri，带上code
     * 若有state参数，则原样带上state
     * @param authorize
     * @return
     */
    @GetMapping("authorize")
    public void authorize(@Valid AuthorizeIn authorize, BindingResult ret, HttpServletResponse response)throws IOException{
        validate(ret);
        //生成code(放到redis中，有效期10分钟，使用后清除)，重定向到response_uri,带上code和state
        if("code".equals(authorize.getResponseType())){
            //判断client_id是否存在



            String code = getAuthorizeCode();
            StringBuilder uri = new StringBuilder(authorize.getRedirectUri());
            uri.append("?code=").append(code);
            if(StringUtils.isNotBlank(authorize.getState())){
                uri.append("&state=").append(authorize.getState());
            }
            //将code存入redis,有效期设置为10分钟


            response.sendRedirect(uri.toString());
        }else {
            Assert.isTrue(false,"response_type参数固定值为code");
        }
    }

    @GetMapping("access_token")
    public AccessTokenOut getAccessToken(@Valid AccessTokenIn accessTokenIn,BindingResult ret){
        validate(ret);
        AccessTokenOut accessTokenOut = new AccessTokenOut();
        if("authorization_code".equals(accessTokenIn.getGrantType())){
            //根据redirect_uri、client_id 判断code是否合法
            //如果合法则生成access_token和refresh_token
            //其中access_token默认有效期为7200秒，refresh_token有效期为半年
        }else {
            Assert.isTrue(false,"grant_type参数固定值为authorization_code");
        }
        return accessTokenOut;
    }

    @GetMapping("refresh_token")
    public AccessTokenOut getAccessToken(@Valid RefreshTokenIn refreshTokenIn,BindingResult ret){
        validate(ret);
        AccessTokenOut accessTokenOut = new AccessTokenOut();
        if("refresh_token".equals(refreshTokenIn.getGrantType())){
            //根据client_id判断refresh_token是否有效
            //重新生成access_token和refresh_token
        }else {
            Assert.isTrue(false,"grant_type参数固定值为refresh_token");
        }
        return accessTokenOut;
    }
}