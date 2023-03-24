package com.example.splitter.repositories;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("TRANSAKTION")
class Transaktion {
    @Column("BESCHREIBUNG")
    final String beschreibung;
    @Column("BETRAG")
    final double betrag;
    @Column("SENDER")
    final String sender;
    @Column("EMPFAENGER")
    final String empfaenger;
    @Column("ID")
    @Id
    Integer id;


    @PersistenceCreator
    Transaktion(String beschreibung, double betrag, String sender, String empfaenger) {
        this.beschreibung = beschreibung;
        this.betrag = betrag;
        this.sender = sender;
        this.empfaenger = empfaenger;
    }


    Integer id() {
        return id;
    }

    String beschreibung() {
        return beschreibung;
    }

    double betrag() {
        return betrag;
    }

    String sender() {
        return sender;
    }

    String empfaenger() {
        return empfaenger;
    }

}
