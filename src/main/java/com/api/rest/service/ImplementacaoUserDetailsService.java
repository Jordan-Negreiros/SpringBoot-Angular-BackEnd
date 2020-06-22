package com.api.rest.service;

import com.api.rest.model.User;
import com.api.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /* Consultar no banco o usuario */
        User user = userRepository.findUserByLogin(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), user.getAuthorities());
    }

    public void insertAcessoPadrao(Long id) {

        /* Descobre a constraint de restrição */
        String constraint = userRepository.consultConstraintRole();
        /* Remove Contraint */
        if (constraint != null) {
            jdbcTemplate.execute("alter table usuarios_role DROP CONSTRAINT " + constraint);
        }
        /* Insere o acesso padrão */
        userRepository.insertStandardRole(id);
    }
}
