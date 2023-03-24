package com.example.splitter.domain.service;


class Balance implements Comparable<Balance> {

    final String personName;
    double total;
    Balance(String personName, double total) {
        this.personName = personName;
        this.total = total;
    }

    @Override
    public String toString() {
        return "{" + personName + "," + total + '}';
    }

    String getPersonName() {
        return personName;
    }

    double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    void sollHaben(double betrag) {
        this.total += betrag;
    }

    void sollBezahlen(double betrag) {
        this.total -= betrag;
    }

    @Override
    public int compareTo(Balance other) {
// Math.abs(this.mAmount - convertedAmount(other)) < EPSILON)
        if (Double.compare(this.total, other.getTotal()) != 0) {
            return Double.compare(this.total, other.getTotal());
        } else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.personName, other.getPersonName());
        }
    }

    public int compareToReverse(Balance other) {
        if (Double.compare(this.total, other.getTotal()) != 0) {
            return Double.compare(other.getTotal(), this.total);
        } else {
            return String.CASE_INSENSITIVE_ORDER.compare(other.getPersonName(), this.getPersonName());
        }

    }

}
