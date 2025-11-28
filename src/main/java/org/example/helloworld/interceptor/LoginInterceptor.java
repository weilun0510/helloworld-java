package org.example.helloworld.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.helloworld.utils.JwtUtil;
import org.example.helloworld.utils.Result;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录认证拦截器
 * 验证请求头中的 Token 是否有效
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * 
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @return true 表示继续执行，false 表示中断执行
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 打印请求信息（可选，用于调试）
        System.out.println("LoginInterceptor - 请求路径: " + request.getRequestURI());

        // 1. 从请求头中获取 Token
        String token = request.getHeader("Authorization");

        // 支持 Bearer Token 格式
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 2. 检查 Token 是否存在
        if (token == null || token.trim().isEmpty()) {
            System.out.println("LoginInterceptor - Token 不存在");
            sendUnauthorizedResponse(response, "未登录，请先登录");
            return false;
        }

        // 3. 验证 Token 是否有效
        try {
            boolean isValid = JwtUtil.validateToken(token);
            if (!isValid) {
                System.out.println("LoginInterceptor - Token 验证失败");
                sendUnauthorizedResponse(response, "Token 无效或已过期");
                return false;
            }

            // 4. Token 验证通过，从 Token 中提取用户ID和用户名并存储到请求中
            Integer userId = JwtUtil.getUserIdFromToken(token);
            String username = JwtUtil.getUsernameFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            System.out.println("LoginInterceptor - Token 验证通过，用户ID: " + userId + ", 用户名: " + username);

            return true;

        } catch (Exception e) {
            System.out.println("LoginInterceptor - Token 解析异常: " + e.getMessage());
            sendUnauthorizedResponse(response, "Token 无效或已过期");
            return false;
        }
    }

    /**
     * 发送 401 未授权响应
     * 
     * @param response 响应对象
     * @param message  错误信息
     * @throws Exception 异常
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK); // 401
        response.setContentType("application/json;charset=UTF-8");

        Result result = Result.error(401)
                .message(message);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);

        response.getWriter().write(json);
    }
}
