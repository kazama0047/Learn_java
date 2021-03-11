package com.naoki.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Kazama
 * @create 2021-03-05-11:56
 */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        chain.doFilter(req, resp);
    }
}
