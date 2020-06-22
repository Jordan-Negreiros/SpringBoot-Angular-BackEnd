package com.api.rest.controller;

import com.api.rest.model.Occupation;
import com.api.rest.repository.OccupationRepository;
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
    private OccupationRepository occupationRepository;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<Occupation>> profissoes() {

        List<Occupation> occupationList = occupationRepository.findAll();

        return new ResponseEntity<List<Occupation>>(occupationList, HttpStatus.OK);
    }
}
