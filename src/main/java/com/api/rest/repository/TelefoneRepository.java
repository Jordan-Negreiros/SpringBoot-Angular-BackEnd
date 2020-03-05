package com.api.rest.repository;

import com.api.rest.model.Telefone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone,Long> {
}
