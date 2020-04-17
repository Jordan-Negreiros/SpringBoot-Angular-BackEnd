package com.api.rest.security;

import com.api.rest.service.ImplementacaoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/* Mapeia URL, endereços, autoriza ou bloqueia acesso a URL */
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private ImplementacaoUserDetailsService implementacaoUserDetailsService;

    /* Configura as solicitações de acesso por Http */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /* Ativando a proteção contra usuário que não estão validados por token */
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                /* Ativando a restrição a URL, Permitindo Acesso a página inicial do sistema */
                .disable().authorizeRequests().antMatchers("/").permitAll()
                .antMatchers("/index", "/recuperar/**").permitAll()

                // Liberação do CORS
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                /* URL de Logout - Redireciona após o user deslogar do sistema */
                .anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

                /* Mapeia URL de logout e invalida o usuário */
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

                /* Filtra as requisições de login para autenticação */
                .and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)

                /* Filtra demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP */
                .addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        /* Service que irá consultar o usário no banco de dados */
        auth.userDetailsService(implementacaoUserDetailsService)

                /* Padrão de codificação de senha */
                .passwordEncoder(new BCryptPasswordEncoder());

    }
}
