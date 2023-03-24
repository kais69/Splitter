package com.example.splitter.domain.model.gruppe;

import com.example.splitter.domain.model.person.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;


class GruppeTest {



    @Test
    @DisplayName("eine Gruppe besteht aus 2 Personen")
    void test_1() {
        //arrange
        Gruppe gruppe=new Gruppe("g1");
        Person p1=new Person(1,"anton");
        Person p2=new Person(2,"sam");
        //act
        gruppe.addPerson(p1.getId());
        gruppe.addPerson(p2.getId());
        //assert
        assertThat(gruppe.getPersonenIds()).containsExactly(1,2);

    }
    @Test
    @DisplayName("eine Gruppe besteht aus 2 Transaktionen zwischen 2 Personen")
    void test_2() {
        //arrange
        Gruppe gruppe=new Gruppe(1,"g1");
        Person p1=new Person(1,"anton");
        Person p2=new Person(2,"sam");
        gruppe.addPerson(p1.getId());
        gruppe.addPerson(p2.getId());
        //act
        gruppe.addTransaktion(1,"hotel",22.2,p1.getGitHubName(),p2.getGitHubName());
        gruppe.addTransaktion(2,"Essen",10,p2.getGitHubName(),p1.getGitHubName());

        //assert
        assertThat(gruppe.getAnzahlTransaktionen()).isEqualTo(2);

    }

    @Test
    @DisplayName("eine Person zu geschlossen Gruppe könnte nicht hinzufügt werden")
    void test_3() {
        //arrange
        Gruppe gruppe=new Gruppe(1,"g1");
        Person p1=new Person(1,"anton");
        Person p2=new Person(2,"sam");
        gruppe.addPerson(p1.getId());
        gruppe.setGeschlossen();
        //act
        gruppe.addPerson(2);
        //assert
        assertThat(gruppe.getPersonenIds()).containsExactly(1);

    }
    @Test
    @DisplayName("eine Transaktion zu geschlossen Gruppe könnte nicht hinzufügt werden")
    void test_4() {
        //arrange
        Gruppe gruppe = new Gruppe(1,"g1", Set.of(1,2),false);
        gruppe.addTransaktion(1,"hotel",22.2,"anton","sam");
        gruppe.setGeschlossen();
        //act
        gruppe.addTransaktion(2,"essen",10,"mark","sam");
        //assert
        assertThat(gruppe.getAnzahlTransaktionen()).isEqualTo(1);
    }



    @Test
    @DisplayName("Sobald eine Ausgabe für die Gruppe eingetragen wurde, können keine weiteren Personen mehr hinzugefügt werden.")
    void test_5() {
        //arrange
        Person p1=new Person(1,"anton");
        Person p2=new Person(2,"sam");
        Person p3=new Person(3,"mark");
        Gruppe gruppe = new Gruppe(1,"g1");
        gruppe.addPerson(p1.getId());
        gruppe.addPerson(p2.getId());
        gruppe.addTransaktion(1,"hotel",22.2,p1.getGitHubName(), p2.getGitHubName());
        //act
        gruppe.addPerson(p3.getId());
        //assert
        assertThat(gruppe.getPersonenIds()).containsExactly(1,2);

    }




}