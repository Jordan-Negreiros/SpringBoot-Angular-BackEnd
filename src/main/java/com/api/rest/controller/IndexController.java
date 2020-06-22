package com.api.rest.controller;

import com.api.rest.model.User;
import com.api.rest.model.UserChart;
import com.api.rest.model.UserReport;
import com.api.rest.repository.PhoneRepository;
import com.api.rest.repository.UserRepository;
import com.api.rest.service.ImplementacaoUserDetailsService;
import com.api.rest.service.ServiceRelatorio;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*") /* libera requisições ao Controller */
@RestController /* Arquitetura Rest */
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /* Injeção de Dependencia Spring, no CDI seria @Inject */
	private UserRepository userRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Autowired
	private PhoneRepository phoneRepository;

	@Autowired
	private ServiceRelatorio serviceRelatorio;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/* Serviço RESTful */
	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<User> init(@PathVariable(value = "id") Long id) {
		
		Optional<User> usuario = userRepository.findById(id);
		
		return new ResponseEntity<User>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<Page<User>> usuario() throws InterruptedException{

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		Page<User> list = userRepository.findAll(page);

		return new ResponseEntity<Page<User>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/page/{page}", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<Page<User>> usuarioPage(@PathVariable("page") int pagina) throws InterruptedException{

		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		Page<User> list = userRepository.findAll(page);

		return new ResponseEntity<Page<User>>(list, HttpStatus.OK);
	}

	/*END-POINT consulta de usuário por nome*/
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<Page<User>> usuarioPorNome (@PathVariable("nome") String nome) throws InterruptedException{

		PageRequest pageRequest = null;
		Page<User> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) {/*Não informou nome*/

			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list =  userRepository.findAll(pageRequest);
		}else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = userRepository.findUserByNamePage(nome, pageRequest);
		}

		return new ResponseEntity<Page<User>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cashuser")
	public ResponseEntity<Page<User>> usuarioPorNomePage
			(@PathVariable("nome") String nome, @PathVariable("page") int page) throws Exception{

		PageRequest pageRequest = null;
		Page<User> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { /* não informou o nome */

			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = userRepository.findAll(pageRequest);
		} else {

			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = userRepository.findUserByNamePage(nome, pageRequest);
		}

		return new ResponseEntity<Page<User>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<User> cadastrar(@Valid @RequestBody User user) {
		
		for (int i = 0; i < user.getPhones().size(); i++) {
			user.getPhones().get(i).setUser(user);
		}

		String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getPassword());
		user.setPasswordUser(senhaCriptografada);
		User userSalvo = userRepository.save(user);

		implementacaoUserDetailsService.insertAcessoPadrao(userSalvo.getId());
		
		return new ResponseEntity<User>(userSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<User> atualizar(@RequestBody User user) {
		
		for (int i = 0; i < user.getPhones().size(); i++) {
			user.getPhones().get(i).setUser(user);
		}

		User userTemporario = userRepository.findById(user.getId()).get();

		if (!userTemporario.getPassword().equals(user.getPassword())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getPassword());
			user.setPasswordUser(senhaCriptografada);
		}
		
		User userSalvo = userRepository.save(user);
		
		return new ResponseEntity<User>(userSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		
		userRepository.deleteById(id);
		
		return "ok";
	}

	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		phoneRepository.deleteById(id);
		return "ok";
	}

	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario",new HashMap(), request.getServletContext());

		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
	}

	@PostMapping(value = "/relatorio/", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest request,
														 @RequestBody UserReport userReport) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DATA_INICIO", userReport.getStartDate());
		param.put("DATA_FIM", userReport.getEndDate());

		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario-param",param ,request.getServletContext());

		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
	}

	@GetMapping(value = "/grafico", produces = "application/json")
	public ResponseEntity<UserChart> grafico() {
		UserChart userChart = new UserChart();

		String sql = "select array_agg(name) from user where salary > 0 and name <> '' union all "
				+ " select cast(array_agg(salary) as character varying[]) from user where salary > 0 and name <> '' ";

		List<String> resultado = jdbcTemplate.queryForList(sql, String.class);

		if (!resultado.isEmpty()) {
			String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salario = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");

			userChart.setName(nomes);
			userChart.setSalary(salario);
		}

		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);
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
