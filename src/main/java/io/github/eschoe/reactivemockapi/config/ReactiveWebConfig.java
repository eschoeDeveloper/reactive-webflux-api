package io.github.eschoe.reactivemockapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebFlux
public class ReactiveWebConfig implements WebFluxConfigurer {

    /**
     * Resource 경로 및 Cache 지정
     * */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public", "classpath:/static")
                .setCacheControl(CacheControl.maxAge(20, TimeUnit.SECONDS));

    }

    /**
     * 모든 요청에서 전달되는 /api/xxx에 대하여 RestController에 기본 path인 /api를 지정하는 메서드
    * */
    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
    }
    
}
