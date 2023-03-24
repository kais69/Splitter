package com.example.splitter.domain.model.person;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    @Test
    @DisplayName("eine Person gehört zu 2 Gruppe")
    void test_1() {
        //arrange
        Person p1=new Person("mark");
        //act
        p1.addGruppenIDs(Set.of(1,2));
        //assert
        assertThat(p1.getGruppenIDs()).containsExactly(1,2);

    }
    @Test
    @DisplayName("2 Personen mit unterschiedlichen Ids sind nicht identisch")
    void test_2() {
        //arrange
        Person p1=new Person(1,"sami");
        Person p2=new Person(2,"mark");
        //act
        int k= p1.compareTo(p2);
        //assert
        assertThat(k).isEqualTo(-1);
    }



}