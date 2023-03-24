package com.example.splitter.application.service;

import com.example.splitter.domain.model.gruppe.Gruppe;
import com.example.splitter.domain.model.person.Person;
import com.example.splitter.domain.model.gruppe.TransaktionenDetail;
import com.example.splitter.domain.service.SplitterDomainService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SplitterApplicationService {
    private final SplitterDomainService splitterDomainService;
    private final PersonRepo personRepo;
    private final GruppenRepo gruppenRepo;


    public SplitterApplicationService(SplitterDomainService splitterDomainService, PersonRepo personRepo, GruppenRepo gruppenRepo) {
        this.splitterDomainService = splitterDomainService;
        this.personRepo = personRepo;
        this.gruppenRepo = gruppenRepo;
    }


    public Person erstellePerson(String name) {
        if (personExists(name)) {
            return getPersonPerName(name);
        }
        Person person = new Person(name);
        return personRepo.save(person);
    }


    //synchrone
    public Gruppe erstelleGruppe(Integer erstellerId, String gruppenName) {
        Gruppe gruppe = new Gruppe(gruppenName);
        if (personRepo.findById(erstellerId).isPresent()) {
            gruppe.addPerson(erstellerId);
            return gruppenRepo.save(gruppe);
        }
        return gruppe;
    }


    public boolean personExists(String gitHubName) {
        return personRepo.findByGitHubName(gitHubName).isPresent();
    }

    public boolean gruppeExist(Integer id) {
        return gruppenRepo.findByGruppeId(id).isPresent();
    }

    public Integer getPersonId(String gitHubName) {
        return getPersonPerName(gitHubName).getId();
    }

    private Person getPersonPerName(String gitHubName) {
        return personRepo.findByGitHubName(gitHubName).orElseThrow(NichtVorhandenException::new);
    }

    public Gruppe getGruppePerId(Integer gruppeId) {
        return gruppenRepo.findByGruppeId(gruppeId).orElseThrow(NichtVorhandenException::new);
    }


    public void addPersonZuGruppePerName(Integer gruppeId, String personName) {
        if (personExists(personName) && gruppeExist(gruppeId)) {
            Person person = personRepo.findByGitHubName(personName).orElseThrow(NichtVorhandenException::new);
            addPersonZuGruppePerId(gruppeId, person);
        }
    }


    public void addPersonZuGruppePerId(Integer gruppeId, Person person) {
        Gruppe gruppe = getGruppePerId(gruppeId);
        gruppe.addPerson(person.getId());
        gruppenRepo.save(gruppe);
    }


    public Set<Gruppe> getGruppenPerPersonName(String gitHubName) {
        Person person = getPersonPerName(gitHubName);
        return gruppenRepo.findAll().stream()
                .filter(g -> g.getPersonenIds().contains(person.getId()))
                .collect(Collectors.toSet());
    }


    public Set<String> getPersonenPerGruppe(Gruppe g) {
        Set<Integer> personenIds = g.getPersonenIds();
        return personRepo.findAll().stream()
                .filter(p -> personenIds.contains(p.getId()))
                .map(Person::getGitHubName)
                .collect(Collectors.toSet());
    }


    public void gruppeSchliessen(Integer gruppeId) {
        Gruppe gruppe = getGruppePerId(gruppeId);
        gruppe.setGeschlossen();
        gruppenRepo.save(gruppe);
    }


    public void addTransaktionenPerName(Integer gruppeId, String beschreibung, double betrag, String ausgeber,
                                        Set<String> beteiligte) {
        Gruppe gruppe = getGruppePerId(gruppeId);
        splitterDomainService.addTransaktionen(beschreibung, betrag, ausgeber, beteiligte, gruppe);
        gruppenRepo.save(gruppe);
    }


    public List<TransaktionenDetail> getAusgaben(Integer gruppeId) {
        Gruppe gruppe = getGruppePerId(gruppeId);
        return gruppe.getTransaktionen();
    }

    public List<TransaktionenDetail> ausgleichTransaktionen(Integer gruppeId) {
        Gruppe gruppe = getGruppePerId(gruppeId);
        Set<String> personenNamen = getPersonenPerGruppe(gruppe);
        return splitterDomainService.ausgleichTransaktionen(gruppe, personenNamen);
    }


    public Integer gruppenSize() {
        return gruppenRepo.findAll().size();
    }
}
