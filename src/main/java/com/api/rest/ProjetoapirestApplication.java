package com.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = {"com.api.rest.model"}) /* Configura as classes de modelo */
@ComponentScan(basePackages = {"com.*"}) /* Configura injeção de dependencias */
@EnableJpaRepositories(basePackages = {"com.api.rest.repository"}) /* Configura os repositórios */
@EnableTransactionManagement /* Configura transações com o banco de dados */
@EnableWebMvc /* Habilita o Spring MVC */
@RestController /* Habilita serviços Rest */
@EnableAutoConfiguration
public class ProjetoapirestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoapirestApplication.class, args);
	}

}
