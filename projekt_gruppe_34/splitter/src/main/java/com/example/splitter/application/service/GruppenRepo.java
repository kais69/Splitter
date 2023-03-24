package com.example.splitter.application.service;

import com.example.splitter.domain.model.gruppe.Gruppe;

import java.util.Optional;
import java.util.Set;


public interface GruppenRepo {
    Set<Gruppe> findAll();

    Gruppe save(Gruppe gruppe);

    Optional<Gruppe> findByGruppeId(Integer id);
}
