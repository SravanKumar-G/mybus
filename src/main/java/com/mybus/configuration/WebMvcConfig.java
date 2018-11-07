package com.mybus.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.SystemProperties;
import com.mybus.interceptors.AuthenticationInterceptor;
import com.mybus.interceptors.DomainFilterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration

@ComponentScan(basePackages = {"com.mybus"} )
@EnableMongoRepositories(basePackages = "com.mybus")
public class WebMvcConfig extends WebMvcConfigurerAdapter implements AsyncConfigurer {

    @Autowired
    private SystemProperties props;

    @Autowired
    private DomainFilterInterceptor domainFilterInterceptor;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(domainFilterInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**");
    }


    /*

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pageableArgumentResolver());
    }


    public static PageableHandlerMethodArgumentResolver getPageableHandlerMethodArgumentResolver() {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setMaxPageSize(Integer.MAX_VALUE);
        resolver.setFallbackPageable(new PageRequest(0, Integer.MAX_VALUE, null));
        //resolver.setPrefix("page.");
        resolver.setOneIndexedParameters(true);
        return resolver;
    }

    @Bean
    public PageableHandlerMethodArgumentResolver pageableArgumentResolver() {
        return getPageableHandlerMethodArgumentResolver();
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
        boolean jsonTaintingEnabled = props.getBooleanProperty(SystemProperties.SysProps.JSON_PREFIX_TAINT_ENABLED, false);

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
        resolverList.add(new DefaultHandlerExceptionResolver());
        result.setExceptionResolvers(resolverList);
        return result;
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //List<MediaType> supportedTypes = new ArrayList<MediaType>();
//        supportedTypes.add(MediaType.ALL);
//        converter.setSupportedMediaTypes(supportedTypes);
        boolean jsonTaintingEnabled = props.getBooleanProperty(SystemProperties.SysProps.JSON_PREFIX_TAINT_ENABLED, false);
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



    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }


    @Bean
    public JavaMailSenderImpl getJavaMailSenderImpl(){
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost("");
        javaMailSenderImpl.setPort(1);
        javaMailSenderImpl.setUsername("");
        javaMailSenderImpl.setPassword("");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", true);
        javaMailProperties.put("mail.smtp.starttls.enable", false);
        javaMailProperties.put("mail.smtp.quitwait", false);
        javaMailProperties.put("mail.smtp.ssl.trust","");
        javaMailSenderImpl.setJavaMailProperties(javaMailProperties);
        return javaMailSenderImpl;
    }
    */


}