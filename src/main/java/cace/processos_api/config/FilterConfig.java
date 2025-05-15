package cace.processos_api.config;

import cace.processos_api.interceptor.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter(RateLimitingFilter filter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*"); // Limita todas as rotas da API
        registration.setOrder(1);
        return registration;
    }
}