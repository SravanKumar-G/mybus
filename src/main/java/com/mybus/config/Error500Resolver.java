package com.mybus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Error500Resolver extends SimpleMappingExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(Error500Resolver.class);
    public static final String UNCAUGHT_EXCEPTION_VIEW = "uncaughtException";

    public Error500Resolver() {
        this.setDefaultErrorView(UNCAUGHT_EXCEPTION_VIEW);
    }

    @Override
    protected ModelAndView doResolveException(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex
    ) {
        logger.error("Unhandled exception occurred.  Internal Server Error (500) will be returned.", ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return super.doResolveException(request, response, handler, ex);
    }
}
