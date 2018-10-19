package com.macbeth.util;

import com.macbeth.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CookieUtils {

    public static String readLoginToken(HttpServletRequest request){
        Optional<String> optional = Arrays.stream(request.getCookies())
                .filter(cookie -> StringUtils.equals(cookie.getName(),Constant.COOKIE_NAME))
                .map(Cookie::getValue)
                .findAny();
        StringBuffer result = new StringBuffer();
        optional.ifPresent(result::append);
        return result.toString();
    }

    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(Constant.COOKIE_NAME, token);
//        cookie.setDomain(Constant.COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletResponse response, String token, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        Arrays.stream(cookies).filter(cookie -> StringUtils.equals(cookie.getName(),token)).forEach(cookie -> {
            cookie.setDomain(Constant.COOKIE_DOMAIN);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        });
    }
}
