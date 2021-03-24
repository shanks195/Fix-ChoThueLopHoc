package vn.thuephonghoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.thuephonghoc.annotation.AnonymousAccess;
import vn.thuephonghoc.utils.SpringContextHolder;

@EnableAsync
@RestController
//Turn on the audit function
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableTransactionManagement
@SpringBootApplication
public class CfrBackendApplication extends SpringBootServletInitializer  {

    public static void main(String[] args) {
        SpringApplication.run(CfrBackendApplication.class, args);
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    /**
     * Solve the problem of inputting the []{} symbol when querying
     */
    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
        return tomcatServletWebServerFactory;
    }


    /**
     *Visit homepage tips
     * @return /
     */
    @GetMapping("/")
    @AnonymousAccess
    public String index() {
        return "Backend service was started!!! (by Devteam - https://thuephonghoc.vn)";
    }

}
