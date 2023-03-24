package com.example.splitter.application.service;

import com.example.splitter.domain.model.gruppe.Gruppe;
import com.example.splitter.domain.model.person.Person;
import com.example.splitter.domain.model.gruppe.TransaktionenDetail;
import com.example.splitter.domain.service.SplitterDomainService;
import com.example.splitter.repositories.GruppeRepoImp;
import com.example.splitter.repositories.PersonRepoImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class SplitterApplicationServiceTest {

  SplitterApplicationService splitterApplicationService;


  GruppenRepo gruppenRepo;

  SplitterDomainService splitterDomainService;
  PersonRepo personRepo;

  @BeforeEach
  void initiate() {
    splitterDomainService = mock(SplitterDomainService.class);
    gruppenRepo = mock(GruppeRepoImp.class);
    personRepo = mock(PersonRepoImp.class);
    splitterApplicationService = new SplitterApplicationService(splitterDomainService, personRepo, gruppenRepo);
  }

  @Test
  @DisplayName("person existiert")
  void test_002() {
    Person person = new Person(1, "anton");
    when(personRepo.findByGitHubName("anton")).
            thenReturn(Optional.of(person));
    assertThat(splitterApplicationService.personExists("anton"))
            .isEqualTo(true);
  }
  @Test
  @DisplayName("gruppe nicht existiert ")
  void test_022() {

    when(gruppenRepo
            .findByGruppeId(anyInt())).
            thenReturn(Optional.empty());
    assertThrows(NichtVorhandenException.class,()->splitterApplicationService.getGruppePerId(100));

  }

  @Test
  @DisplayName("Erstelle neue person, falls er nicht vorhanden ist")
  void test_0002() {
    when(personRepo.findByGitHubName("anton")).
            thenReturn(Optional.empty());
    splitterApplicationService.erstellePerson("anton");
    verify(personRepo).save(any(Person.class));

  }

  @Test
  @DisplayName("Erstelle neue person, falls er vorhanden ist")
  void test_00002() {
    Person person = new Person(1, "anton");
    when(personRepo.findByGitHubName("anton")).
            thenReturn(Optional.of(person));
    splitterApplicationService.erstellePerson("anton");
    verify(personRepo, never()).save(any(Person.class));

  }

  @Test
  @DisplayName("Gruppe exists")
  void test_001() {
    Gruppe gruppe = new Gruppe(1, "g1");
    when(gruppenRepo.findByGruppeId(1)).thenReturn(Optional.of(gruppe));
    assertThat(splitterApplicationService.gruppeExist(1)).isTrue();
  }




  @Test
  @DisplayName("erstelle Gruppe")
  void test_003() {
    Person person=new Person(1,"sam");
    when(personRepo.findById(person.getId())).thenReturn(Optional.of(person));
    splitterApplicationService.erstelleGruppe(1, "G1");
    verify(gruppenRepo).save(any(Gruppe.class));
  }

  @Test
  @DisplayName("ohne existierende Person darf keine Gruppe erstellt werden")
  void test_033() {
    when(personRepo.findById(anyInt())).thenReturn(Optional.empty());
    splitterApplicationService.erstelleGruppe(1, "G1");
    verify(gruppenRepo,never()).save(any(Gruppe.class));
  }

  @Test
  @DisplayName("gruppe schließen")
  void test_005() {
    Gruppe gruppe=new Gruppe(1,"G1");
    when(gruppenRepo.findByGruppeId(1)).thenReturn(Optional.of(gruppe));

    splitterApplicationService.gruppeSchliessen(gruppe.getId());

    verify(gruppenRepo).save(gruppe);
    assertThat(gruppe.isGeschlossen()).isTrue();
  }

  @Test
  @DisplayName("eine Person zu einer Gruppe mit einer Person hinzufügen")
  void test2() {

    Person person1 = new Person(1, "Anton");
    Person person2 = new Person(2, "hamad");
    Gruppe gruppe = new Gruppe(1, "G1");
    gruppe.addPerson(person1.getId());

    when(personRepo.findByGitHubName("hamad")).
            thenReturn(Optional.of(person2));
    when(gruppenRepo.findByGruppeId(anyInt())).thenReturn(Optional.of(gruppe));


    splitterApplicationService.addPersonZuGruppePerName(1, "hamad");

    verify(gruppenRepo).save(gruppe);
    assertThat(gruppe.getPersonenIds()).contains(2);
  }


  @Test
  @DisplayName("eine Person zu zwei Gruppen hinzufügen")
  void test3() {
    Person person1 = new Person(1, "Anton");
    Gruppe gruppe1 = new Gruppe(1, "G1");
    Gruppe gruppe2 = new Gruppe(2, "G2");

    when(gruppenRepo.findByGruppeId(1)).thenReturn(Optional.of(gruppe1));
    when(gruppenRepo.findByGruppeId(2)).thenReturn(Optional.of(gruppe2));
    when(personRepo.findByGitHubName("Anton")).
            thenReturn(Optional.of(person1));

    splitterApplicationService.addPersonZuGruppePerName(1, "Anton");
    splitterApplicationService.addPersonZuGruppePerName(2, "Anton");
    verify(gruppenRepo).save(gruppe1);
    verify(gruppenRepo).save(gruppe2);

    assertThat(gruppe1.getPersonenIds()).contains(1);
    assertThat(gruppe2.getPersonenIds()).contains(1);

  }

  @Test
  @DisplayName("eine Person darf nicht zu einer geschlossen Gruppe hinzugefügt werden")
  void test4() {


    Person person1 = new Person(1, "Anton");
    Gruppe gruppe1 = new Gruppe(1, "G1");
    gruppe1.addPerson(person1.getId());
    Person person2 = new Person(2, "mark");

    gruppe1.setGeschlossen();
    splitterApplicationService.addPersonZuGruppePerName(1, person2.getGitHubName());

    verify(gruppenRepo, never()).save(any(Gruppe.class));
    assertThat(gruppe1.getPersonenIds()).containsExactly(1);
  }

  @Test
  @DisplayName("eine Gruppe ohne existierenden Person darf nicht erstellt werden")
  void test44() {
    Person person = new Person(1,"anton");
    Gruppe gruppe=new Gruppe(1,"G1");
    when(personRepo.findById(person.getId())).thenReturn(Optional.of(person));
    splitterApplicationService.erstelleGruppe(1, "G1");
    verify(gruppenRepo).save(any(Gruppe.class));


  }



  @Test
  @DisplayName("add Transaktionen zu gruppe")
  void test5() {


    Person person1 = new Person(1, "A");
    Person person2 = new Person(2, "B");
    Person person3 = new Person(3, "C");
    Gruppe gruppe = new Gruppe(1, "G1");

    addPersonenIdsZuGruppe(gruppe, Set.of(person1.getId(), person2.getId(), person3.getId()));
    when(gruppenRepo.findByGruppeId(1)).thenReturn(Optional.of(gruppe));



    splitterApplicationService.addTransaktionenPerName(1, "hotel", 60, "A", Set.of("A", "B", "C"));

    verify(splitterDomainService).addTransaktionen("hotel", 60, "A",Set.of("A", "B", "C"),gruppe);
    verify(gruppenRepo).save(gruppe);

  }
  @Test
  @DisplayName("Übersicht von Ausgaben")
  void test55() {
    Gruppe gruppe = new Gruppe(1, "G1");
    Person person1 = new Person(1, "A");
    Person person2 = new Person(2, "B");
    addPersonenIdsZuGruppe(gruppe,Set.of(1,2));
    gruppe.addTransaktion(1,"hotel", 60, person1.getGitHubName(), person2.getGitHubName());
    when(gruppenRepo.findByGruppeId(1)).thenReturn(Optional.of(gruppe));

    List <TransaktionenDetail> ausgaben =splitterApplicationService.getAusgaben(gruppe.getId());

    assertThat(ausgaben).containsExactly(new TransaktionenDetail("hotel", 60,"A","B"));
  }






  @Test
  @DisplayName("Ausgeber bezahlt nur für die anderen D--> A,B,C ")
  void test8() {
    Gruppe gruppe=new Gruppe(1,"G1");
    Person p1 = new Person(1,"anton");
    Person p2 = new Person(2,"sam");
   addPersonenIdsZuGruppe(gruppe,Set.of(1,2));

    when(gruppenRepo.findByGruppeId(1)).thenReturn(Optional.of(gruppe));
    when(personRepo.findAll()).thenReturn(Set.of(p1,p2));
    splitterApplicationService.ausgleichTransaktionen(gruppe.getId());
    verify(splitterDomainService).ausgleichTransaktionen(gruppe,Set.of(p1.getGitHubName(),p2.getGitHubName()));
  }
  @Test
  @DisplayName("Übersicht über Gruppen einer Person")
  void getGruppenPerPersonName(){
    Gruppe gruppe1=new Gruppe(1,"G1");
    Gruppe gruppe2=new Gruppe(2,"G2") ;
    Person person=new Person(1,"anton");
    gruppe1.addPerson(1);
    gruppe2.addPerson(1);

    when(gruppenRepo.findAll()).thenReturn(Set.of(gruppe1,gruppe2));
    when(personRepo.findByGitHubName("anton")).
            thenReturn(Optional.of(person));

    Set<Gruppe> gruppen=splitterApplicationService.getGruppenPerPersonName("anton");
    assertThat(gruppen).containsExactlyInAnyOrder(gruppe1,gruppe2);
  }

  void addPersonenIdsZuGruppe(Gruppe gruppe, Set<Integer> personenIds) {
    for (Integer personid : personenIds)
      gruppe.addPerson(personid);
  }
}