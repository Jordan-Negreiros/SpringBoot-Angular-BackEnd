package com.api.rest.controller;

import com.api.rest.model.Profissao;
import com.api.rest.repository.ProfissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/profissao")
public class ProfissaoController {

    @Autowired
    private ProfissaoRepository profissaoRepository;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<Profissao>> profissoes() {

        List<Profissao> profissaoList = profissaoRepository.findAll();

        return new ResponseEntity<List<Profissao>>(profissaoList, HttpStatus.OK);
    }
}
