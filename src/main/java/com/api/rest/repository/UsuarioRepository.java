package com.api.rest.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.rest.model.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

    @Query("SELECT u from Usuario u where u.login = ?1")
    Usuario findUserByLogin( String Login );

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    @Query(value = "SELECT constraint_name from information_schema.constraint_column_usage where table_name = 'usuarios_role' and column_name = 'role_id'\n" +
            " and constraint_name <> 'unique_role_user';", nativeQuery = true)
    String consultaConstraintRole();

    @Transactional
    @Modifying
    @Query(value = "insert into usuarios_role (usuario_id, role_id) VALUES (?1, (select id from role where nome_role = 'ROLE_USER'));", nativeQuery = true)
    public void insertRolePadrao(Long idUser);

}
