package com.example.splitter.api;

import com.example.splitter.application.service.SplitterApplicationService;
import com.example.splitter.domain.model.gruppe.Gruppe;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class InventoryApi {

    private final SplitterApplicationService splitterApplicationService;


    public InventoryApi(SplitterApplicationService splitterApplicationService) {
        this.splitterApplicationService = splitterApplicationService;
    }


    @PostMapping("/api/gruppen")
    public ResponseEntity<String> neueGruppe(@RequestBody Input input) {
        if (input.name() == null || input.name().isEmpty()) return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        if (input.personen().isEmpty())
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        for (String person : input.personen())
            splitterApplicationService.erstellePerson(person);
        Integer id = splitterApplicationService.getPersonId(input.personen().stream().findFirst().get());
        Gruppe gruppe = splitterApplicationService.erstelleGruppe(id, input.name());

        for (String person : input.personen())
            splitterApplicationService.addPersonZuGruppePerName(gruppe.getId(), person);

        return new ResponseEntity<>(String.valueOf(gruppe.getId()), HttpStatus.CREATED);
    }


    @GetMapping("/api/user/{GITHUB-LOGIN}/gruppen")
    public ResponseEntity<Set<GruppenInfoDTO>> gruppenPerPerson(@PathVariable("GITHUB-LOGIN") String gitHubName) {
        if (splitterApplicationService.personExists(gitHubName) && splitterApplicationService.gruppenSize() > 0)
            return new ResponseEntity<>(splitterApplicationService.getGruppenPerPersonName(gitHubName).stream()
                    .map(g -> new GruppenInfoDTO(String.valueOf(g.getId()),
                            g.getName(), splitterApplicationService.getPersonenPerGruppe(g)))
                    .collect(Collectors.toSet()), HttpStatus.OK);
        return new ResponseEntity<>(Set.of(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/api/gruppen/{id}")
    public ResponseEntity<GruppeDTO> getGruppePerId(@PathVariable("id") String id) {
        Integer idInt = 0;
        if (NumberUtils.isParsable(id)) {
            idInt = Integer.valueOf(id);
        }
        if (!splitterApplicationService.gruppeExist(idInt))
            return new ResponseEntity<>(new GruppeDTO("", "", Set.of(), false, List.of()), HttpStatus.NOT_FOUND);
        Gruppe gruppe = splitterApplicationService.getGruppePerId(idInt);
        List<TransaktionDTO> transaktion = gruppe.getTransaktionen().stream().map(g -> new TransaktionDTO(g.beschreibung(), g.absender(), g.betrag(), Set.of(g.empfaenger()))).toList();
        return new ResponseEntity<>(new GruppeDTO(String.valueOf(gruppe.getId()), gruppe.getName(),
                splitterApplicationService.getPersonenPerGruppe(gruppe), gruppe.isGeschlossen(),
                transaktion), HttpStatus.OK);
    }

    @PostMapping("/api/gruppen/{id}/schliessen")
    public ResponseEntity<String> gruppeSchliessen(@PathVariable("id") String id) {
        Integer idInt = 0;
        if (NumberUtils.isParsable(id)) {
            idInt = Integer.valueOf(id);
        }
        if (!splitterApplicationService.gruppeExist(idInt))
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        splitterApplicationService.gruppeSchliessen(idInt);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/api/gruppen/{id}/auslagen")
    public ResponseEntity<String> postAusgabe(@PathVariable("id") String id, @RequestBody TransaktionDTO transaktionDTO) {
        Integer idInt = 0;
        if (NumberUtils.isParsable(id)) {
            idInt = Integer.valueOf(id);
        }
        if (!splitterApplicationService.gruppeExist(idInt))
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        if (splitterApplicationService.getGruppePerId(idInt).isGeschlossen())
            return new ResponseEntity<>("", HttpStatus.CONFLICT);
        if (transaktionDTO.grund() == null || transaktionDTO.grund().isEmpty() || transaktionDTO.glaeubiger() == null || transaktionDTO.glaeubiger().isEmpty() ||
                transaktionDTO.cent() <= 0 || transaktionDTO.schuldner() == null || transaktionDTO.schuldner().isEmpty())
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        splitterApplicationService.addTransaktionenPerName(idInt, transaktionDTO.grund(), transaktionDTO.cent(),
                transaktionDTO.glaeubiger(), transaktionDTO.schuldner());
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @GetMapping("/api/gruppen/{id}/ausgleich")
    public ResponseEntity<List<AusgleichDTO>> ausgleich(@NotNull @PathVariable("id") String id) {
        Integer idInt = 0;
        if (NumberUtils.isParsable(id)) {
            idInt = Integer.valueOf(id);
        }
        if (id == null || !splitterApplicationService.gruppeExist(idInt))
            return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
        var transaktionenDetail = splitterApplicationService.ausgleichTransaktionen(idInt);
        List<AusgleichDTO> ausgleichDTOS = transaktionenDetail.stream().map(t -> new AusgleichDTO(t.absender(), t.empfaenger(), t.betrag())).toList();
        return new ResponseEntity<>(ausgleichDTOS, HttpStatus.OK);
    }
}



