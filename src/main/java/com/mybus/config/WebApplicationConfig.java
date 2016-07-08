package com.mybus.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.mail.internet.MimeMessage;
import javax.xml.transform.Source;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.SystemProperties;
import com.mybus.SystemProperties.SysProps;
import com.mybus.interceptors.AuthenticationInterceptor;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc

@EnableScheduling
@ComponentScan(basePackages = "com.mybus")
@Import({CoreAppConfig.class })
//@PropertySource(name = "redisProperties", value = "classpath:redis-config.properties")
@EnableAsync
@EnableMongoAuditing(
        auditorAwareRef = "springSecurityAuditorAware"
)
@EnableSwagger2
public class WebApplicationConfig extends WebMvcConfigurerAdapter implements AsyncConfigurer {

    private static final int MAX_UPLOAD_SIZE_DEFAULT = 52428800;

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfig.class);

    @Autowired
    private SystemProperties props;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/api/.*"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        /*
        ApiInfo apiInfo = new ApiInfo("My Project's REST API",
                "This is a description of your API.",
                "API TOS",
                "me@wherever.com",
                "API License",
                "API License URL"
        );
        */
        ApiInfo apiInfo = new ApiInfo("MyBus Project's REST API",
                "Here is the list of REST APIs available",
                "V1", "SwaggerUI", "Arg5", "", "");
        return apiInfo;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/font/**").addResourceLocations("/font/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
        registry.addResourceHandler("/bootstrap303/**").addResourceLocations("/bootstrap303/");
        registry.addResourceHandler("/*").addResourceLocations("/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pageableArgumentResolver());
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        final int maxUploadSize = props.getIntegerProperty(SystemProperties.SysProps.MAX_UPLOAD_SIZE_BYTES, MAX_UPLOAD_SIZE_DEFAULT);
        multipartResolver.setMaxUploadSize(maxUploadSize);
        return multipartResolver;
    }

    public static PageableHandlerMethodArgumentResolver getPageableHandlerMethodArgumentResolver() {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setMaxPageSize(Integer.MAX_VALUE);
        resolver.setFallbackPageable(new PageRequest(0, Integer.MAX_VALUE, null));
        resolver.setPrefix("page.");
        resolver.setOneIndexedParameters(true);
        return resolver;
    }

    @Bean
    public PageableHandlerMethodArgumentResolver pageableArgumentResolver() {
        return getPageableHandlerMethodArgumentResolver();
    }

    @Bean
    public JSONPrefixTaintingPostProcessor jsonPrefixTaintingPostProcessor() {
        boolean jsonTaintingEnabled = props.getBooleanProperty(SystemProperties.SysProps.JSON_PREFIX_TAINT_ENABLED, false);
        return new JSONPrefixTaintingPostProcessor(jsonTaintingEnabled);
    }

    @Bean
    public ContentNegotiatingViewResolver viewResolver() {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        List<ViewResolver> viewResolvers = new ArrayList<>(1);
        viewResolvers.add(jspViewResolver());
        resolver.setViewResolvers(viewResolvers);
        List<View> views = new ArrayList<>();
        views.add(jsonView());
        resolver.setDefaultViews(views);
        return resolver;
    }

    @Bean
    public MappingJackson2JsonView jsonView() {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setObjectMapper(objectMapper);
        jsonView.setContentType("application/json");
        boolean jsonTaintingEnabled = props.getBooleanProperty(SysProps.JSON_PREFIX_TAINT_ENABLED, false);
        if (jsonTaintingEnabled) {
            jsonView.setJsonPrefix(JSONPrefixTaintingPostProcessor.JSON_PREFIX_TAINTING);
        }
        return jsonView;
    }

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setOrder(1);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setRedirectHttp10Compatible(false);
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    @Bean
    public HandlerExceptionResolverComposite getHandlerExceptionResolverComposite() {
        HandlerExceptionResolverComposite result = new HandlerExceptionResolverComposite();
        List<HandlerExceptionResolver> resolverList = new ArrayList<>();
        resolverList.add(new ExceptionHandlerExceptionResolver());
        resolverList.add(new ResponseStatusExceptionResolver());
        resolverList.add(new Error500Resolver());
        resolverList.add(new DefaultHandlerExceptionResolver());
        result.setExceptionResolvers(resolverList);
        return result;
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //List<MediaType> supportedTypes = new ArrayList<MediaType>();
//        supportedTypes.add(MediaType.ALL);
//        converter.setSupportedMediaTypes(supportedTypes);
        boolean jsonTaintingEnabled = props.getBooleanProperty(SysProps.JSON_PREFIX_TAINT_ENABLED, false);
        converter.setPrefixJson(jsonTaintingEnabled);
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);

        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<Source>());
        converters.add(new AllEncompassingFormHttpMessageConverter());

        converters.add(mappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("portal");
        return messageSource;
    }

    @Override
    public Executor getAsyncExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("SimpleExecutor-");
        executor.setConcurrencyLimit(10);
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/api/**");
    }
    @Bean
    public AuthenticationInterceptor authenticationInterceptor(){
        return new AuthenticationInterceptor();
        
    }

    @Bean
    public SpringSecurityAuditorAware springSecurityAuditorAware() {
        return (SpringSecurityAuditorAware) applicationContext.getBean("springSecurityAuditorAware");
    }
   
    @Bean
	public VelocityEngine velocityEngine() throws VelocityException, IOException{
		VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class","org.apache.velocity.runtime.resource.loader." +"ClasspathResourceLoader");
		factory.setVelocityProperties(props);
		return factory.createVelocityEngine();
	}
}