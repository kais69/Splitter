package com.example.splitter.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;


public interface SpringDataGruppeRepo extends CrudRepository<Gruppe, Integer> {
    Set<Gruppe> findAll();

    Optional<Gruppe> findByGruppeId(Integer integer);
}
