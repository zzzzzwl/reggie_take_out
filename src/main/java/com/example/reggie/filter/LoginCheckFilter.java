package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录过滤器
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获得本次请求uri
        String requestURI = request.getRequestURI();
//        log.info("拦截到请求:{}",requestURI);
        //不处理直接放行的url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //判断是否需要过滤
        boolean check = check(urls,requestURI);
        if(check){
//            log.info("本次请求不需要处理:{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //需要处理判断登录状态
        if(request.getSession().getAttribute("employee")!=null){
//            log.info("用户已登录，id:{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        if(request.getSession().getAttribute("user")!=null){
//            log.info("用户已登录，id:{}",request.getSession().getAttribute("employee"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        //不放行,通过输出流向客户端页面响应数据
//        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，请求是否放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
