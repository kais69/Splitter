package com.example.splitter.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;


public interface SpringDataPersonRepo extends CrudRepository<Person, Integer> {
    Set<Person> findAll();

    Optional<Person> findPersonByGitHubName(String name);

    Optional<Person> findById(Integer id);
}

