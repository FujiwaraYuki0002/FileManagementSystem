package fms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fms.ErrorHandler.SessionTimeoutInterceptor;

/**
 * インターセプターを設定し、セッションタイムアウトを管理する。
 *
 * @author 大塚 月愛
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionTimeoutInterceptor sessionTimeoutInterceptor;

    /**
     * インターセプターをリクエストに追加する設定
     *
     * @author 大塚 月愛
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // インターセプターを全てのリクエストに適用し、不要なURLは除外
        registry.addInterceptor(sessionTimeoutInterceptor)
                .addPathPatterns("/**", "/file_management_system/**") // 全てのリクエストに対してインターセプト
                .excludePathPatterns("/", "/login", "/css/**", "/js/**", "/images/**",
                        "/file/saveFileDelete", "/file_management_system/", "/file_management_system/login",
                        "/file_management_system/css/**", "/file_management_system/js/**",
                        "/file_management_system/images/**",
                        "/file_management_system/file/saveFileDelete"); // 一部URLと制的ファイルを除外

    }
}
