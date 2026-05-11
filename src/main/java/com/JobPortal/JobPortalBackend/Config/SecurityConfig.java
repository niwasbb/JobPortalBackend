package com.JobPortal.JobPortalBackend.Config;

import com.JobPortal.JobPortalBackend.SecurityLayer.CustomAccessDeniedHandler;
import com.JobPortal.JobPortalBackend.SecurityLayer.JWTFilter;
import com.JobPortal.JobPortalBackend.SecurityLayer.JwtAuthenticationEntryPoint;
import com.JobPortal.JobPortalBackend.Services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final UserDetailsService userDetailsService;
    private final JWTFilter jwtFilter;

    @Autowired
    public SecurityConfig(MyUserDetailsService userDetailsService,JWTFilter jwtFilter){
        this.userDetailsService=userDetailsService;
        this.jwtFilter=jwtFilter;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSec){

        return httpSec.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth.requestMatchers("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**",
                                                                                                            "/resume/{resumeFileName}","/login","/register").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/recruiter").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT,"/jobseeker").hasRole("JOB_SEEKER")
                        .requestMatchers(HttpMethod.GET,"/applications/my_applications").hasRole("JOB_SEEKER")
                        .requestMatchers(HttpMethod.GET,"/applications/{jobId}").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT,"/applications/shortlist/{applicationId}").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT,"/applications/reject/{applicationId}").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.POST,"/applications/{jobPostId}/apply").hasRole("JOB_SEEKER")
                        .requestMatchers(HttpMethod.PUT,"/applications/cancel/{applicationId}").hasRole("JOB_SEEKER")
                        .requestMatchers(HttpMethod.POST,"/jobs").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT ,"/jobs/{jobPostId}").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.DELETE ,"/jobs/{jobPostId}").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.POST,"/resume").hasRole("JOB_SEEKER")
                        .requestMatchers(HttpMethod.DELETE,"/resume").hasRole("JOB_SEEKER")
                        .anyRequest().authenticated()
                ).exceptionHandling(customizer->customizer.accessDeniedHandler(new CustomAccessDeniedHandler())
                                                                                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
}

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));


        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConf){
        return authConf.getAuthenticationManager();
    }



}
