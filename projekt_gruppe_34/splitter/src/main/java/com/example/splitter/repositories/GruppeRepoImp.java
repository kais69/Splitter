package com.example.splitter.repositories;


import com.example.splitter.application.service.GruppenRepo;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class GruppeRepoImp implements GruppenRepo {

    private final SpringDataGruppeRepo gruppeRepo;


    public GruppeRepoImp(SpringDataGruppeRepo gruppeRepo) {

        this.gruppeRepo = gruppeRepo;
    }


    @Override
    public Set<com.example.splitter.domain.model.gruppe.Gruppe> findAll() {
        Set<Gruppe> all = gruppeRepo.findAll();
        return all.stream().map(this::toGruppe).collect(Collectors.toSet());
    }

    private com.example.splitter.domain.model.gruppe.Gruppe toGruppe(Gruppe gruppeDTO) {
        var gruppe =
                new com.example.splitter.domain.model.gruppe.Gruppe(
                        gruppeDTO.gruppeId(),
                        gruppeDTO.name(),
                        gruppeDTO.personenIds().stream()
                                .map(GruppePerson::personenIds)
                                .collect(Collectors.toSet()),
                        gruppeDTO.geschlossen());
        gruppeDTO.transaktionen().forEach(t -> gruppe.addBestehendeTransaktionen(t.id, t.beschreibung(), t.betrag(), t.sender(), t.empfaenger()));
        return gruppe;
    }


    @Override
    public com.example.splitter.domain.model.gruppe.Gruppe save(com.example.splitter.domain.model.gruppe.Gruppe gruppe) {
        Gruppe dto = fromGruppe(gruppe);
        Gruppe saved = gruppeRepo.save(dto);
        return toGruppe(saved);
    }


    private Gruppe fromGruppe(com.example.splitter.domain.model.gruppe.Gruppe gruppe) {
        var transaktionen = gruppe.getTransaktionen().stream().map(t -> new Transaktion(t.beschreibung(), t.betrag(), t.absender(), t.empfaenger())).collect(Collectors.toSet());
        var getPersonenIds =
                gruppe.getPersonenIds().stream().map(GruppePerson::new).collect(Collectors.toSet());
        return new Gruppe(
                gruppe.getId(), gruppe.getName(), getPersonenIds, transaktionen, gruppe.isGeschlossen());
    }

    @Override
    public Optional<com.example.splitter.domain.model.gruppe.Gruppe> findByGruppeId(Integer id) {
        return gruppeRepo.findByGruppeId(id).map(this::toGruppe);
    }
}
