package com.example.splitter.repositories;

import com.example.splitter.application.service.PersonRepo;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PersonRepoImp implements PersonRepo {


    private final SpringDataPersonRepo personRepo;


    public PersonRepoImp(SpringDataPersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public Set<com.example.splitter.domain.model.person.Person> findAll() {
        Set<Person> all = personRepo.findAll();
        return all.stream().map(this::toPerson).collect(Collectors.toSet());
    }

    private com.example.splitter.domain.model.person.Person toPerson(Person personDTO) {
        return new com.example.splitter.domain.model.person.Person(
                personDTO.personId(), personDTO.gitHubName(), Set.of());
    }

    @Override
    public com.example.splitter.domain.model.person.Person save(com.example.splitter.domain.model.person.Person person) {
        Person dto = fromPerson(person);
        Person saved = personRepo.save(dto);
        return toPerson(saved);
    }

    private Person fromPerson(com.example.splitter.domain.model.person.Person person) {
        return new Person(person.getId(), person.getGitHubName());
    }


    @Override
    public Optional<com.example.splitter.domain.model.person.Person> findById(Integer id) {
        return personRepo.findById(id).map(this::toPerson);
    }


    @Override
    public Optional<com.example.splitter.domain.model.person.Person>
    findByGitHubName(String gitHubName) {
        return personRepo.findPersonByGitHubName(gitHubName).map(this::toPerson);
    }

}
