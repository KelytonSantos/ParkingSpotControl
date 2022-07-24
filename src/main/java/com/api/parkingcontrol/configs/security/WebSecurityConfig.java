package com.api.parkingcontrol.configs.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override//sobrescrevendo dados basicos com os nossos dados
    protected void configure(HttpSecurity http) throws Exception {//método que pertence ao configurer adapter(utilizado para customizar a config do http security)
        http //iniciando http
            .httpBasic()//utilizando httpBasic
            .and()//unindo configs
            .authorizeHttpRequests()//autorizando requisição
            .anyRequest().permitAll(); //para qualquer um que a(request) fizer vamos permitir acesso
            //ou seja, estou falando que mesmo eu tendo o spring security, para todas as solicitações http eu estou permitindo acesso sem auth;
    }
}