package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TestBuilder;
 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String BASE_URL = "/usuarios";
	private static final String USUARIO = "root@root.com";
	private static final String SENHA= "rootroot";
	
	
	@BeforeAll
	void inicio() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Root", USUARIO, SENHA));
	}
	
	@Test
	@DisplayName("01 - Deve Cadastrar um novo usuário com sucesso")
	void deveCadastarUsuario(){
		
		// given
		Usuario usuario = TestBuilder.criarUsuario(null, "João Vitor Diniz",
				"joao.vitor@email.com", "jv123456");
		
		
		// when
		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);
		
		// Enviar a Requisição
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		// than
		
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
		
	}
	
	@Test
	@DisplayName("02 - Não deve cadastrar usuário duplicado")
	void NaoDeveCadastarUsuarioDuplicado(){
		
		// given
		Usuario usuario = TestBuilder.criarUsuario(null, "Luiza Guimarães",
				"luiza@email.com", "luiza1234");
		usuarioService.cadastrarUsuario(usuario);
		
		
		// when
		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);
		
		// Enviar a Requisição
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		// than
		
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertNull(resposta.getBody());
		
	}
   
	@Test
	@DisplayName("03 - Deve listar todos os usuários")
	void deveListarTodosUsuarios(){
		
		// given
	 
		usuarioService.cadastrarUsuario( TestBuilder.criarUsuario(null, "Kaue Dota", 
				"kaue@email.com.br","kaue1234"));
		usuarioService.cadastrarUsuario( TestBuilder.criarUsuario(null, "Edson Nascimento", 
				"edson@email.com.br","edson1234"));
		
		
		// when
		
		//obter token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO,SENHA);
		
		// Corpo da Requisição
		HttpEntity<Void> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(token);
		
		// Enviar a Requisição
		ResponseEntity<Usuario[]> resposta = testRestTemplate.exchange(
				BASE_URL + "/all", HttpMethod.GET, cabecalhoRequisicao, Usuario[].class);
		
		// than
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
		
	}
	
	@Test
	@DisplayName("04 - Deve atualizar os dados do usuário com sucesso")
	void deveAtualizarUsuario(){
		
		// given
	 
		Usuario usuario = TestBuilder.criarUsuario(null, "Daniel ",
				 "daniel@email.com", "danie123");
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(),
				"Daniel Araujo",
				 "daniel@email.com", "danie123");
		// when
		
		//obter token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO,SENHA);
		
		// Corpo da Requisição
		HttpEntity<Usuario> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate,token);
		
		// Enviar a Requisição
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/atualizar", HttpMethod.PUT, cabecalhoRequisicao, Usuario.class);
		
		// than
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
		
	}
}