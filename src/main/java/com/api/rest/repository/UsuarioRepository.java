package com.api.rest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.rest.model.Usuario;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

    @Query("SELECT u from Usuario u where u.login = ?1")
    Usuario findUserByLogin( String Login );

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

}
