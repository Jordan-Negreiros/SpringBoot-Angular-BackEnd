package com.api.rest.controller;

import com.api.rest.model.Usuario;
import com.api.rest.repository.TelefoneRepository;
import com.api.rest.repository.UsuarioRepository;
import com.api.rest.service.ImplementacaoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") /* libera requisições ao Controller */
@RestController /* Arquitetura Rest */
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /* Injeção de Dependencia Spring, no CDI seria @Inject */
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Autowired
	private TelefoneRepository telefoneRepository;

	/* Serviço RESTful */
	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<List<Usuario>> usuario() {
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findByNomeContainingIgnoreCase(nome);

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
		
		for (int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insertAcessoPadrao(usuarioSalvo.getId());
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for (int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

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

	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
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
