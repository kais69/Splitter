package com.example.splitter.domain.service;

import com.example.splitter.domain.model.gruppe.Gruppe;
import com.example.splitter.domain.model.gruppe.TransaktionenDetail;
import com.example.splitter.domain.model.person.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SplitterDomainServiceTest {

    SplitterDomainService splitterDomainService;

    @BeforeEach
    void initiate() {
        splitterDomainService = new SplitterDomainService();
    }

    @Test
    @DisplayName("get Balance jeder Person")
    void getGruppenBalance() {
        Gruppe gruppe = new Gruppe(1, "g1");
        Set<String> personen = Set.of("hamad", "sam");

        gruppe.addTransaktion(1, "hotel", 20.0, "hamad", "sam");
        gruppe.addTransaktion(2, "essen", 10.0, "sam", "hamad");

        var balances =
                splitterDomainService.getGruppenBalance(gruppe, personen).stream().map(Object::toString);
        assertThat(balances)
                .containsExactlyInAnyOrder("{sam,-10.0}", "{hamad,10.0}");

    }


    @Test
    @DisplayName("add Gruppe zu Person")
    void addGruppeZuPerson01() {
        Gruppe gruppe = new Gruppe(1, "g1");
        Person p1 = new Person("mark");

        splitterDomainService.addGruppeZuPerson(gruppe.getId(), p1, gruppe);

        assertThat(p1.getGruppenIDs()).contains(gruppe.getId());
    }

    @Test
    @DisplayName(" Zu einer geschlossenen Gruppe kann eine Person nicht hinzufügt werden")
    void addGruppeZuPerson02() {
        Gruppe gruppe = new Gruppe(1, "g1");
        Person p1 = new Person("mark");
        gruppe.setGeschlossen();

        splitterDomainService.addGruppeZuPerson(gruppe.getId(), p1, gruppe);

        assertThat(p1.getGruppenIDs()).isEmpty();
    }

    @Test
    @DisplayName(" Zu einer Gruppe mit bestehenden Ausgaben, kann keine Person hinzufügt werden")
    void addGruppeZuPerson03() {
        Gruppe gruppe = new Gruppe(1, "g1");
        Person p1 = new Person("mark");
        Person p2 = new Person("anton");
        Person p3 = new Person("hamad");
        gruppe.addTransaktion(1, "Hotel", 50, "mark", "anton");

        splitterDomainService.addGruppeZuPerson(gruppe.getId(), p3, gruppe);

        assertThat(p3.getGruppenIDs()).isEmpty();
    }


    @Test
    @DisplayName("Ausgleichszahlungen in Gruppe von 2 Personen")
    void test7() {

        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Gruppe gruppe = new Gruppe(1, "G1");

        List<TransaktionenDetail> ausgaben = splitterDomainService.addTransaktionen("hotel", 10, "A", Set.of(person1.getGitHubName()
                , person2.getGitHubName()), gruppe);

        assertThat(ausgaben).contains(new TransaktionenDetail("hotel", 5, "A", "B"));
    }


    @Test
    @DisplayName("Szenario 1: Ausgleichszahlungen in Gruppe von 2 Personen")
    void test9() {
        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2));

        splitterDomainService.addTransaktionen("hotel", 10, "A", Set.of("A", "B"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 20, "A", Set.of("A", "B"), gruppe);

        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName()));

        assertThat(t).contains(new TransaktionenDetail("Ausgleich", 15.0, "B", "A"));
    }

    @Test
    @DisplayName("Szenario 2: Ausgleichszahlungen in Gruppe von 2 Personen")
    void test10() {

        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2));


        splitterDomainService.addTransaktionen("hotel", 10, "A", Set.of("A", "B"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 20, "B", Set.of("A", "B"), gruppe);
        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName()));


        assertThat(t).contains(new TransaktionenDetail("Ausgleich", 5.0, "A", "B"));
    }

    @Test
    @DisplayName("Szenario 3: Ausgleichszahlungen in Gruppe von 2 Personen")
    void test11() {
        // Arrange
        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2));

        // Act
        splitterDomainService.addTransaktionen("hotel", 10, "A", Set.of("B"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 20, "A", Set.of("A", "B"), gruppe);

        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName()));

        // assert
        assertThat(t).contains(new TransaktionenDetail("Ausgleich", 20.0, "B", "A"));
    }

    @Test
    @DisplayName("Szenario 4: Ausgleichszahlungen in Gruppe von 3 Personen")
    void test12() {
        // Arrange
        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Person person3 = new Person(3, "C");
        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2, 3));

        // Act
        splitterDomainService.addTransaktionen("hotel", 10, "A", Set.of("A", "B"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 10, "B", Set.of("B", "C"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 10, "C", Set.of("A", "C"), gruppe);

        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName(), person3.getGitHubName()));
        // assert
        assertThat(t).isEmpty();
    }

    @Test
    @DisplayName("Szenario 5: Ausgleichszahlungen in Gruppe von 2 Personen")
    void test13() {
        // Arrange//
        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Person person3 = new Person(3, "C");
        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2, 3));


        splitterDomainService.addTransaktionen("hotel", 60, "A", Set.of("A", "B", "C"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 30, "B", Set.of("A", "B", "C"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 100, "C", Set.of("C", "B"), gruppe);

        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName(), person3.getGitHubName()));

        assertThat(t).containsExactly(
                new TransaktionenDetail("Ausgleich", 30.0, "B", "A"),
                new TransaktionenDetail("Ausgleich", 20.0, "B", "C"));
    }

    @Test
    @DisplayName("Szenario 6: Ausgleichszahlungen in Gruppe von 6 Personen")
    void test14() {
        // Arrange//
        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Person person3 = new Person(3, "C");
        Person person4 = new Person(4, "D");
        Person person5 = new Person(5, "E");
        Person person6 = new Person(6, "F");
        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2, 3, 4, 5, 6));

        // Act
        splitterDomainService.addTransaktionen("hotel", 564, "A", Set.of("A", "B", "C", "D", "E", "F"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 38.58, "B", Set.of("A", "B"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 38.58, "B", Set.of("A", "B", "D"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 82.11, "C", Set.of("C", "E", "F"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 96, "D", Set.of("A", "B", "C", "D", "E", "F"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 95.37, "F", Set.of("B", "E", "F"), gruppe);

        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName(), person3.getGitHubName()
                , person4.getGitHubName(), person5.getGitHubName(), person6.getGitHubName()));
        // assert

        assertThat(t)
                .contains(
                        new TransaktionenDetail("Ausgleich", 169.16, "E", "A"),
                        new TransaktionenDetail("Ausgleich", 96.78, "B", "A"),
                        new TransaktionenDetail("Ausgleich", 73.79, "F", "A"),
                        new TransaktionenDetail("Ausgleich", 55.26, "C", "A"),
                        new TransaktionenDetail("Ausgleich", 26.86, "D", "A"));
    }


    @Test
    @DisplayName("Szenario 7: Ausgleichszahlungen in Gruppe von 7 Personen")
    void test15() {

        Person person1 = new Person(1, "A");
        Person person2 = new Person(2, "B");
        Person person3 = new Person(3, "C");
        Person person4 = new Person(4, "D");
        Person person5 = new Person(5, "E");
        Person person6 = new Person(6, "F");
        Person person7 = new Person(7, "G");

        Gruppe gruppe = new Gruppe(1, "G1", Set.of(1, 2, 3, 4, 5, 6, 7));


        splitterDomainService.addTransaktionen("hotel", 20, "D", Set.of("D", "F"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 10, "G", Set.of("B"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 75, "E", Set.of("A", "C", "E"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 50, "F", Set.of("A", "F"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 40, "E", Set.of("D"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 40, "F", Set.of("B", "F"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 5, "F", Set.of("C"), gruppe);
        splitterDomainService.addTransaktionen("hotel", 30, "G", Set.of("A"), gruppe);


        List<TransaktionenDetail> t = splitterDomainService.ausgleichTransaktionen(gruppe, Set.of(person1.getGitHubName(), person2.getGitHubName(), person3.getGitHubName()
                , person4.getGitHubName(), person5.getGitHubName(), person6.getGitHubName(), person7.getGitHubName()));
        double sumOf = t.stream().mapToDouble(TransaktionenDetail::betrag).sum();
        assertThat(t).containsExactlyInAnyOrder(
                new TransaktionenDetail("Ausgleich", 80, "A", "E"),
                new TransaktionenDetail("Ausgleich", 30, "B", "G"),
                new TransaktionenDetail("Ausgleich", 30, "C", "F"),
                new TransaktionenDetail("Ausgleich", 10, "D", "G"),
                new TransaktionenDetail("Ausgleich", 10, "D", "F"),
                new TransaktionenDetail("Ausgleich", 10, "D", "E"));
        assertThat(sumOf).isEqualTo(170);


    }


}