package com.macbeth.filter;

import com.macbeth.common.ServerResponse;
import com.macbeth.util.ControllerUtils;
import com.macbeth.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class PrivilegeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = request.getRequestURL().toString();

        String method = request.getMethod();
        if (! url.contains("managers") || url.contains("managers/user") && StringUtils.equalsIgnoreCase(method,"post")) {
            filterChain.doFilter(request,response);
            return;
        } else if (url.contains("managers")) {
            ServerResponse result = ControllerUtils.isLogin(request);
            if (result.isSuccess() && ControllerUtils.isAdminLoginNew(result).isSuccess()) {
                filterChain.doFilter(request,response);
                return;
            }
        } else {
            String result = JsonUtils.obj2String(ServerResponse.createByErrorMessage("需要管理员登陆"));
            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(result);
            response.getWriter().flush();
        }
    }

    @Override
    public void destroy() {

    }
}
