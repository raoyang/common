package com.game.config.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ProtocalInterceptor implements HandlerInterceptor {
	@Value("${allow.plain.text.request}")
	private boolean allow;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getMethod().equals("POST")) {
			if(request.getContentType().contains("application/json") && !allow){
				response.setStatus(400);
				response.getWriter().write("No support for plaintext transmission, please encrypt.");
				return false;
			}
		}

		return true;
	}
}
