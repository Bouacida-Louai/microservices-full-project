package org.sid.authservice.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class H2ConsoleConfig {

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServletRegistration() {
        ServletRegistrationBean<JakartaWebServlet> registrationBean =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");

        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("webAllowOthers", "true");
        initParameters.put("trace", "false");
        registrationBean.setInitParameters(initParameters);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
}
