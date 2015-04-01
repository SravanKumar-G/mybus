package com.mybus.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

public class JSONPrefixTaintingPostProcessor implements BeanPostProcessor {

    public static final String JSON_PREFIX_TAINTING = ")]}',\n";

    private boolean taintingEnabled = false;

    public JSONPrefixTaintingPostProcessor(boolean taintingEnabled) {
        this.taintingEnabled = taintingEnabled;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!taintingEnabled) {
            return bean;
        }
        if (bean instanceof RequestMappingHandlerAdapter) {
            List<HttpMessageConverter<?>> convs = ((RequestMappingHandlerAdapter) bean).getMessageConverters();
            for (HttpMessageConverter<?> conv: convs) {
                if (conv instanceof MappingJackson2HttpMessageConverter) {
                    ((MappingJackson2HttpMessageConverter)conv).setJsonPrefix(JSON_PREFIX_TAINTING);
                }
            }
        }
        return bean;
    }
}
