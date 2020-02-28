package com.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.api.rest.model.Usuario;
import com.api.rest.repository.UsuarioRepository;

@CrossOrigin(origins = "www.sistemadocliente1.com.br") /* libera requisições ao Controller */
@RestController /* Arquitetura Rest */
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /* Injeção de Dependencia Spring, no CDI seria @Inject */
	private UsuarioRepository usuarioRepository;

	/* Serviço RESTful */
	@GetMapping(value = "/{id}", produces = "application/json")
	@Cacheable("cashuser")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cashuser")
	public ResponseEntity<List<Usuario>> usuario() {
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		for (int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for (int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());

		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return "ok";
	}
	
	/*
		 //Exemplo de Customização métodos RESTful e URLs 
		@GetMapping(value = "/{id}/relatóriopdf", produces = "application/json")
		public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id) {
	
			Optional<Usuario> usuario = usuarioRepository.findById(id);
	
			 //o retorno seria um relatório 
			return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
		}
	 */
}
