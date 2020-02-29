package com.api.rest.security;

import com.api.rest.ApplicationContexLoad;
import com.api.rest.model.Usuario;
import com.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
@Component
public class JWTTokenAutenticacaoService {

    /* Validade TOKEN 2 dias */
    private static final long EXPIRATION_TIME = 172800000;

    /* Senha única para compor a autenticação e ajudar na segurança */
    private static final String SECRET = "SenhaExemplo";

    /* Prefixo padrão do TOKEN */
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    /* Gerando TOKEN de autenticação e adicionando ao cabeçalho e resposta Http */
    public void addAuthentication(HttpServletResponse response, String username) throws IOException {

        /* Montagem do TOKEN */
        String JWT = Jwts.builder() /* Chama o gerador de TOKEN */
                .setSubject(username) /* adiciona o usuario */
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de Expiração */
                .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /* Compactação e algoritmo de geração senha */

        /* Junta o TOKEN com o PREFIXO */
        String token = TOKEN_PREFIX + " " + JWT;

        /* Adiciona no cabeçalho Http */
        response.addHeader(HEADER_STRING, token);

        /* Liberando resposta para porta diferente do projeto Angular */
        response.addHeader("Access-Control-Allow-Origin", "*");

        /* Escreve o TOKEN como resposta no corpo HTtp */
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    /* Retorna Usuário validado com TOKEN ou caso não seja válido retorna null */
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        /* Pega o TOKEN enviado no cabeçalho Http */
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            /* faz a validação do TOKEN do usuário */
            String user = Jwts.parser().setSigningKey(SECRET) /* Retorna TOKEN */
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "")) /* Rentorna o TOKEN sem o prefixo */
                    .getBody().getSubject(); /* Retorna o Usuário */
            if (user != null) {

                Usuario usuario = ApplicationContexLoad.getApplicationContext()
                        .getBean(UsuarioRepository.class).findUserByLogin(user);

                /* Retornar usuário logado */
                if (usuario != null) {

                    return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());

                }
            }
        }
        /* Liberando resposta para porta diferente do projeto Angular */
        response.addHeader("Access-Control-Allow-Origin", "*");
        return null; /* Não autorizado */
    }

}
