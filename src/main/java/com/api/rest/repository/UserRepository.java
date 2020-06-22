package com.api.rest.repository;

import com.api.rest.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u from User u where u.login = ?1")
    User findUserByLogin(String Login );

    List<User> findByNameContainingIgnoreCase(String nome);

    @Query(value = "SELECT constraint_name from information_schema.constraint_column_usage where table_name = 'user_role' and column_name = 'role_id'\n" +
            " and constraint_name <> 'unique_role_user';", nativeQuery = true)
    String consultConstraintRole();

    @Transactional
    @Modifying
    @Query(value = "insert into user_role (user_id, role_id) VALUES (?1, (select id from role where name_role = 'ROLE_USER'));", nativeQuery = true)
    public void insertStandardRole(Long idUser);

    @Transactional
    @Modifying
    @Query(value = "update user set password = ?1 where id = ?2", nativeQuery = true)
    void updatePassword(String password, Long codUser);

    default Page<User> findUserByNamePage(String name, PageRequest pageRequest) {

        User user = new User();
        user.setName(name);

        /* Configurando para pesquisa por nome e paginação */
        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("name",
                        ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<User> example = Example.of(user, exampleMatcher);

        return findAll(example, pageRequest);
    }
}
