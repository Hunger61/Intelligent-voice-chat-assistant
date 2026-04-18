package host.hunger.vocalchat.api.rest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import host.hunger.vocalchat.api.rest.interceptor.RequestInterceptor;
import host.hunger.vocalchat.api.rest.interceptor.UserInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestInterceptor requestInterceptor;
    private final UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/**")
                .order(0);

        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .order(1);
    }
}
