package com.example.splitter.domain.model.gruppe;

import com.example.splitter.stereotypes.Entity;

import java.util.Objects;

@Entity
class Transaktion implements Comparable<Transaktion> {

    final Integer id;
    final String beschreibung;
    final double betrag;
    final String senderName;
    final String empfaengerName;

    Transaktion(Integer id, String beschreibung, double betrag, String sender, String empfaenger) {
        this.id = id;
        this.beschreibung = beschreibung;
        this.betrag = betrag;
        this.senderName = sender;
        this.empfaengerName = empfaenger;
    }


    Integer getId() {
        return id;
    }


    @Override
    public int compareTo(Transaktion other) {
        return id.compareTo(other.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaktion that = (Transaktion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
