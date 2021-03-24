package vn.thuephonghoc.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import vn.thuephonghoc.annotation.AnonymousAccess;
import vn.thuephonghoc.security.JwtAccessDeniedHandler;
import vn.thuephonghoc.security.JwtAuthenticationEntryPoint;
import vn.thuephonghoc.security.TokenConfigurer;
import vn.thuephonghoc.security.TokenProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ApplicationContext applicationContext;

    public SecurityConfig(TokenProvider tokenProvider, CorsFilter corsFilter, JwtAuthenticationEntryPoint authenticationErrorHandler, JwtAccessDeniedHandler jwtAccessDeniedHandler, ApplicationContext applicationContext) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.applicationContext = applicationContext;
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Password encryption method
        return new BCryptPasswordEncoder();
    }


    /**
     * anyRequest          |   Match all request paths
     * access              |   Can be accessed when SpringEl expression result is true
     * anonymous           |   Anonymously accessible
     * denyAll             |   User can't access
     * fullyAuthenticated  |   The user is fully authenticated and can access (automatically log in under non-remember-me)
     * hasAnyAuthority     |   If there are parameters, the parameters indicate permissions, and any one of them can access
     * hasAnyRole          |   If there are parameters, the parameters indicate roles, any one of them can access
     * hasAuthority        |   If there are parameters, the parameters indicate permissions, and the permissions can be accessed
     * hasIpAddress        |   If there is a parameter, the parameter indicates the IP address, if the user IP matches the parameter, you can visit
     * hasRole             |   If there is a parameter, the parameter indicates a role, and its role can access
     * permitAll           |   Users can access at will
     * rememberMe          |   Allow access to users who log in through remember-me
     * authenticated       |   Accessible after user login
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // Search for anonymous tag url: @AnonymousAccess
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        Set<String> anonymousUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            if (null != anonymousAccess) {
                anonymousUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
            }
        }
        httpSecurity
                // disable CSRF
                .csrf().disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                // authorization exception
                .exceptionHandling()
                .authenticationEntryPoint(authenticationErrorHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler)

             // Prevent iframe from causing cross-domain
                .and()
                .headers()
                .frameOptions()
                .disable()

             // do not create a session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
             // static resources etc.
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/webSocket/**"
                ).permitAll()

                // swagger
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/*/api-docs").permitAll()

                // file
                .antMatchers("/avatar/**").permitAll()
                .antMatchers("/file/**").permitAll()
                // druid
                .antMatchers("/druid/**").permitAll()
                .antMatchers("/api/**").permitAll()

                // Release OPTIONS request
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Custom anonymous access to all urls: Allow anonymous and authorized and logged-in users to access
                .antMatchers(anonymousUrls.toArray(new String[0])).permitAll()
                // All requests require authentication
                .anyRequest().authenticated()
                .and().apply(securityConfigurerAdapter());
    }

    private TokenConfigurer securityConfigurerAdapter() {
        return new TokenConfigurer(tokenProvider);
    }
}
