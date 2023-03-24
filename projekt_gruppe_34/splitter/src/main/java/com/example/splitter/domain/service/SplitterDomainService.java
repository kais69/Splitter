package com.example.splitter.domain.service;

import com.example.splitter.domain.model.gruppe.Gruppe;
import com.example.splitter.domain.model.gruppe.TransaktionenDetail;
import com.example.splitter.domain.model.person.Person;
import com.example.splitter.stereotypes.ClassOnly;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SplitterDomainService {

    public void addGruppeZuPerson(Integer gruppeId, Person person, Gruppe gruppe) {
        if (!gruppe.isGeschlossen() && gruppe.getAnzahlTransaktionen() == 0) person.addGruppeID(gruppeId);
    }

    public List<TransaktionenDetail> addTransaktionen(
            String beschreibung, double betrag, String ausgeber, Set<String> beteiligte, Gruppe gruppe) {

        for (String empfaenger : beteiligte) {
            if (!empfaenger.equals(ausgeber)) {

                gruppe.addTransaktion(
                        1,
                        beschreibung,
                        Math.round(100.0 * (betrag / beteiligte.size())) / 100.0,
                        ausgeber,
                        empfaenger);
            }
        }
        return gruppe.getTransaktionen();
    }


    List<Balance> getGruppenBalance(Gruppe gruppe, Set<String> personenNamen) {
        List<TransaktionenDetail> transaktionen = gruppe.getTransaktionen();

        List<Balance> balances = personenNamen.stream().map(name -> new Balance(name, 0)).toList();

        balanceBerechnen(transaktionen, balances);
        return balances;
    }

    @ClassOnly
    private void balanceBerechnen(List<TransaktionenDetail> transaktionen, List<Balance> balances) {
        for (TransaktionenDetail t : transaktionen) {
            balances.stream()
                    .filter(b -> b.getPersonName().equals(t.absender()))
                    .forEach(b -> b.sollHaben(t.betrag()));
            balances.stream()
                    .filter(b -> b.getPersonName().equals(t.empfaenger()))
                    .forEach(b -> b.sollBezahlen(t.betrag()));
        }
    }

    public List<TransaktionenDetail> ausgleichTransaktionen(
            Gruppe gruppe, Set<String> personenNamen) {
        var balances = getGruppenBalance(gruppe, personenNamen);
        BalanceRechner balanceRechner = new BalanceRechner(balances);
        return balanceRechner.notwendigeTransaktionen();
    }
}
