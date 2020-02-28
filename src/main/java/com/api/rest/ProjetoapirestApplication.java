package com.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
public class ProjetoapirestApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoapirestApplication.class, args);
	}


	/* Cross Origin - Configuração centralizada */
	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/usuario/**")
		.allowedMethods("POST", "PUT", "DELETE")
		.allowedOrigins("cliente1.com", "cliente2.com");
	}
}
