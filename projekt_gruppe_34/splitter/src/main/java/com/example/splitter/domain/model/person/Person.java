package com.example.splitter.domain.model.person;

import com.example.splitter.stereotypes.AggregateRoot;

import java.util.HashSet;
import java.util.Set;

@AggregateRoot
public class Person implements Comparable<Person> {

    private final Integer id;


    private final String gitHubName;

    private Set<Integer> gruppenIDs;

    public Person(Integer id, String gitHubName, Set<Integer> gruppenIDs) {
        this.id = id;
        this.gitHubName = gitHubName;
        this.gruppenIDs = gruppenIDs;
    }

    public Person(String gitHubName) {
        this.id = null;
        this.gitHubName = gitHubName;
        this.gruppenIDs = new HashSet<>();
    }

    public Person(Integer id, String gitHubName) {
        this.id = id;
        this.gitHubName = gitHubName;
        this.gruppenIDs = new HashSet<>();
    }

    public void addGruppeID(Integer id) {
        if (id != null) {
            System.err.println(id);
            gruppenIDs.add(id);
        }
    }

    public String getGitHubName() {
        return gitHubName;
    }

    public Set<Integer> getGruppenIDs() {
        return gruppenIDs;
    }

    public void setGruppenIDs(Set<Integer> gruppenIDs) {
        this.gruppenIDs = gruppenIDs;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(Person other) {
        return id.compareTo(other.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return id.equals(person.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void addGruppenIDs(Set<Integer> gruppenIdsPerPerson) {
        this.gruppenIDs.addAll(gruppenIdsPerPerson);
    }


}
