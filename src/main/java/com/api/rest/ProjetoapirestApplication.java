package com.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {"com.api.rest.model"}) /* Configura as classes de modelo */
@ComponentScan(basePackages = {"com.*"}) /* Configura injeção de dependencias */
@EnableJpaRepositories(basePackages = {"com.api.rest.repository"}) /* Configura os repositórios */
@EnableTransactionManagement /* Configura transações com o banco de dados */
@EnableWebMvc /* Habilita o Spring MVC */
@RestController /* Habilita serviços Rest */
@EnableAutoConfiguration
@EnableCaching
public class ProjetoapirestApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoapirestApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}


	// Libera acesso às origens específicas (mapeamento global)
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		//registry.addMapping("/**"); // todos
		registry.addMapping("/usuario/**")
				.allowedMethods("POST", "PUT", "DELETE") // todos os endpoints do usuario
				.allowedOrigins("localhost:8080", "www.google.com");
	}

}
