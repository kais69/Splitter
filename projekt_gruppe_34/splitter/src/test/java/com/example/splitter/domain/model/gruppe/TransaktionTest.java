package com.example.splitter.domain.model.gruppe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class TransaktionTest {
    @Test
    @DisplayName("2 Transaktionen mit unterschiedlichen Ids sind nicht identisch")
    void test_1() {
        //arrange
        Transaktion t1=new Transaktion(1,"Hotel",20,"mark","sami");
        Transaktion t2=new Transaktion(2,"Essen",22,"mark","sami");
       //act
        int k= t1.compareTo(t2);
        //assert
        assertThat(k).isEqualTo(-1);


    }

    @Test
    @DisplayName("2 Transaktionen mit unterschiedlichen Ids sind nicht identisch")
    void test_2() {
        //arrange
        Transaktion t1=new Transaktion(1,"Hotel",20,"mark","sami");
        Transaktion t2=new Transaktion(2,"Essen",22,"mark","sami");
        //act
       boolean b=t1.equals(t2);
        //assert
        assertThat(b).isFalse();

    }

}