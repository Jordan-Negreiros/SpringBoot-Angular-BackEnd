package com.api.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.rest.model.Usuario;

@RestController /* Arquitetura Rest */
@RequestMapping(value = "/usuario")
public class IndexController {

	/* Serviço RESTful */
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> init() {

		Usuario usuario = new Usuario();
		usuario.setId(50L);
		usuario.setLogin("jordannegreirossantos@gmail.com");
		usuario.setNome("Jordan Negreiros");
		usuario.setSenha("123456789");

		Usuario usuario2 = new Usuario();

		usuario2.setId(30L);
		usuario2.setLogin("jordannegreirossantos@hotmail.com");
		usuario2.setNome("Jordan Negreiros dos Santos");
		usuario2.setSenha("999999999");
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		usuarios.add(usuario);
		usuarios.add(usuario2);

		return new ResponseEntity(usuarios, HttpStatus.OK);
	}
}
