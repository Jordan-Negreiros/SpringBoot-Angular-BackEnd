package com.api.rest.service;

import com.api.rest.model.Usuario;
import com.api.rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /* Consultar no banco o usuario */
        Usuario usuario = usuarioRepository.findUserByLogin(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
    }

    public void insertAcessoPadrao(Long id) {

        /* Descobre a constraint de restrição */
        String constraint = usuarioRepository.consultaConstraintRole();
        /* Remove Contraint */
        if (constraint != null) {
            jdbcTemplate.execute("alter table usuarios_role DROP CONSTRAINT " + constraint);
        }
        /* Insere o acesso padrão */
        usuarioRepository.insertRolePadrao(id);
    }
}
