package com.example.gifserverv3.global.login;

import com.example.gifserverv3.global.exception.CustomException;
import com.example.gifserverv3.global.type.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청한 메서드에 LoginCheck 어노테이션이 있는지 확인
        if (handler instanceof HandlerMethod && ((HandlerMethod) handler).hasMethodAnnotation(LoginCheck.class)) {
            HttpSession session = request.getSession();
            if (session == null || session.getAttribute("user") == null) {
                throw new CustomException(ErrorCode.NOT_LOGIN);
            }
        }
        return true;
    }
}
