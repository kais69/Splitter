package com.example.splitter.application.service;

import com.example.splitter.domain.model.person.Person;

import java.util.Optional;
import java.util.Set;

public interface PersonRepo {
    Set<Person> findAll();

    Person save(Person person);

    Optional<Person> findById(Integer id);

    Optional<Person> findByGitHubName(String gitHubName);

}
