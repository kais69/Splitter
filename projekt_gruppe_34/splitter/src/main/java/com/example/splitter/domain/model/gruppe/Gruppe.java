package com.example.splitter.domain.model.gruppe;
import com.example.splitter.stereotypes.AggregateRoot;

import java.util.*;
@AggregateRoot
public class Gruppe implements Comparable<Gruppe> {

  private final Integer id;
  private final String name;
  private final Set<Integer> personenIds;
  private final Set<Transaktion> transaktionen;

    private boolean geschlossen = false;

    public Gruppe(Integer id, String name, Set<Integer> personen) {
        this.id= id;
        this.name = name;
        this.personenIds = personen;
        this.transaktionen = new HashSet<>();
    }

    public Gruppe(Integer id, String name) {
        this.id= id;
        this.name = name;
        this.personenIds = new HashSet<>();
        this.transaktionen = new HashSet<>();
        this.geschlossen= false;

    }

    public Gruppe(Integer id, String name, Set<Integer> personenIds, boolean geschlossen) {
        this.id = id;
        this.name = name;
        this.personenIds = personenIds;
        this.transaktionen= new HashSet<>();
        this.geschlossen = geschlossen;
    }

    public Gruppe(String name) {
        this.id = null;
        this.name = name;
        this.personenIds = new HashSet<>();
        this.transaktionen= new HashSet<>();
        this.geschlossen = false;
    }

    public void addTransaktion(Integer id,String beschreibung, double betrag, String sender, String empfaenger) {
        if (!geschlossen) {
            id= 1;
            if (!transaktionen.isEmpty())
                id = transaktionen.stream().map(Transaktion::getId).max(Integer::compareTo).get() +1 ;
            transaktionen.add(new Transaktion(id,beschreibung, betrag, sender, empfaenger));
        }
    }

    public Integer getAnzahlTransaktionen(){
        return transaktionen.size();
    }

    public List<TransaktionenDetail> getTransaktionen(){
        return transaktionen.stream()
                .map(p -> new TransaktionenDetail(p.beschreibung, p.betrag, p.senderName, p.empfaengerName)).toList();
    }
    public void addBestehendeTransaktionen(Integer id,String beschreibung, double betrag, String sender, String empfaenger) {
            transaktionen.add(new Transaktion(id,beschreibung, betrag, sender, empfaenger));
}

    public void addPerson(Integer personID) {
    if (!geschlossen && personErlaubt()) personenIds.add(personID);
    }

    private boolean personErlaubt() {
    return transaktionen.size()==0;}

    public boolean isGeschlossen() {
        return geschlossen;
    }

    public void setGeschlossen() {
        geschlossen = true;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Integer> getPersonenIds() {
        return personenIds;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gruppe gruppe = (Gruppe) o;
        return id.equals(gruppe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Gruppe other) {
        return id.compareTo(other.getId());
    }



}
