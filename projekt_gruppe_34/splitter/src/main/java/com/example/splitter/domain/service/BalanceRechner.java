package com.example.splitter.domain.service;

import com.example.splitter.domain.model.gruppe.TransaktionenDetail;

import java.util.*;

class BalanceRechner {

    final List<Balance> balances;
    List<Balance> posBalances;
    List<Balance> negBalances;
    BalanceRechner(List<Balance> balances) {
        this.balances = balances;
        posBalances = new ArrayList<>();
        negBalances = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "BalanceRechner{" +
                "balances=" + balances +
                ", posBalances=" + posBalances +
                ", negBalances=" + negBalances +
                '}';
    }

    void splitBalances() {
        negBalances = balances.stream()
                .filter(t -> t.total < 0)
                .sorted(Comparator.comparing(Balance::getTotal))

                .toList();
        posBalances = balances.stream()
                .filter(t -> t.total > 0)
                .sorted(Comparator.comparing(Balance::getTotal, Comparator.reverseOrder()))
                .toList();
    }


    List<TransaktionenDetail> notwendigeTransaktionen() {
        //neg -50       ----> // -20 , 0
        //pos 30, 20    ----> // 20  , 0

        splitBalances();

        List<TransaktionenDetail> transaktionen = new ArrayList<>();


        while (negBalances.stream().mapToDouble(b -> b.total).sum() < -0.01) {
            Balance posBalance = posBalances.get(0);
            Balance negBalance = negBalances.get(0);

            if (posBalance.total < -negBalance.total) {
                TransaktionenDetail transaktionenDetail =
                        new TransaktionenDetail(
                                "Ausgleich",
                                Math.round(100.0 * (posBalance.getTotal())) / 100.0,
                                negBalance.getPersonName(),
                                posBalance.getPersonName());

                transaktionen.add(transaktionenDetail);

                negBalance.sollBezahlen(-posBalance.total);
                posBalance.setTotal(0);
            } else {
                TransaktionenDetail transaktionenDetail =
                        new TransaktionenDetail(
                                "Ausgleich",
                                Math.round(100.0 * (-negBalance.getTotal())) / 100.0,
                                negBalance.getPersonName(),
                                posBalance.getPersonName());

                transaktionen.add(transaktionenDetail);

                posBalance.sollHaben(negBalance.total);
                negBalance.setTotal(0);
            }


            negBalances = negBalances.stream()
                    .filter(t -> t.total <= 0)
                    .sorted(Balance::compareTo)
                    .toList();
            posBalances = posBalances.stream()
                    .filter(t -> t.total >= 0)
                    .sorted(Balance::compareToReverse)
                    .toList();


        }


        return transaktionen;
    }


}
