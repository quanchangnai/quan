package quan.editor;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by quanchangnai on 2021/4/9.
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class EditorMain implements WebMvcConfigurer, WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public static void main(String[] args) {
        SpringApplication.run(EditorMain.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            @SuppressWarnings("NullableProblems")
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                System.err.println("RequestURI:" + request.getRequestURI());
                JSONObject parameter = new JSONObject();
                parameter.putAll(request.getParameterMap());
                System.err.println("ParameterMap:" + parameter);
                return true;
            }
        }).addPathPatterns("/**");
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
    }

}
