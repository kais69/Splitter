package com.example.splitter.controllers;

import com.example.splitter.application.service.SplitterApplicationService;
import com.example.splitter.domain.model.gruppe.Gruppe;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Qualifier
@Controller
@SessionAttributes({"name", "gruppeNr"})
public class WebController {
    private final SplitterApplicationService splitterApplicationService;

    public WebController(SplitterApplicationService splitterApplicationService) {
        this.splitterApplicationService = splitterApplicationService;
    }

    @GetMapping("/")
    public String main(Model model, @AuthenticationPrincipal OAuth2User userObject) {
        model.addAttribute("user", userObject != null ? userObject.getAttribute("login") : null);
        if (userObject != null) {
            String gitHubName = userObject.getAttribute("login");
            model.addAttribute("name", userObject.getAttribute("login"));
            splitterApplicationService.erstellePerson(gitHubName);
            var gruppen = splitterApplicationService.getGruppenPerPersonName(gitHubName);
            model.addAttribute("gruppen", gruppen);
        }
        return "mainpage";
    }

    @PostMapping("/")
    public String erstelleGruppe(
            @ModelAttribute("name") String gitHubName,
            @Valid @NotBlank(message = "not valid") String name,
            RedirectAttributes redirectAttributes) {
        if (!name.isBlank())
            splitterApplicationService.erstelleGruppe(
                    splitterApplicationService.getPersonId(gitHubName), name);
        else redirectAttributes.addFlashAttribute("error", "Error, Gruppenname darf nicht leer sein");
        return "redirect:/";
    }

    @GetMapping("/detail")
    public String getDetails(Model model, Integer nr) {

        Gruppe gruppe = splitterApplicationService.getGruppePerId(nr);
        Set<String> personenNamen = splitterApplicationService.getPersonenPerGruppe(gruppe);
        var notwendigeTransaktionen = splitterApplicationService.ausgleichTransaktionen(nr);
        model.addAttribute("geschlossen", !gruppe.isGeschlossen());
        model.addAttribute("personenNamen", personenNamen);
        model.addAttribute("gruppeName", gruppe.getName());
        model.addAttribute("gruppeNr", nr);
        model.addAttribute("ausgaben", splitterApplicationService.getAusgaben(nr));
        model.addAttribute("notwendigeTransaktionen", notwendigeTransaktionen);

        return "detail";
    }

    @PostMapping("/detail/addPerson")
    public String personHinzufuegen(String personName, @ModelAttribute("gruppeNr") Integer nr,
                                    RedirectAttributes redirectAttributes) {
        if (!personName.isBlank() && splitterApplicationService.personExists(personName))
            splitterApplicationService.addPersonZuGruppePerName(nr, personName);
        else redirectAttributes.addFlashAttribute("error1", "Error, Person ist nicht vorhanden");
        return "redirect:/detail?nr=" + nr;
    }

    @PostMapping("/detail/closeGroup")
    public String gruppeSchliessen(@ModelAttribute("gruppeNr") Integer nr) {
        splitterApplicationService.gruppeSchliessen(nr);
        return "redirect:/detail?nr=" + nr;
    }

    @PostMapping("/detail/addExpenses")
    public String addAusgaben(
            String beschreibung,
            @RequestParam(value = "betrag", defaultValue = "0") double betrag,
            @RequestParam(value = "ausgeber", defaultValue = "") String ausgeber,
            @RequestParam(value = "empfanger", defaultValue = "") String empfangerString,
            @ModelAttribute("gruppeNr") Integer nr, RedirectAttributes redirectAttributes) {

        if (!beschreibung.isBlank()
                && betrag > 0
                && !ausgeber.isBlank()
                && !empfangerString.isEmpty()) {
            Set<String> empfanger = Set.of(empfangerString.split(","));
            splitterApplicationService.addTransaktionenPerName(
                    nr, beschreibung, betrag, ausgeber, empfanger);
        }
        if (beschreibung.isBlank())
            redirectAttributes.addFlashAttribute("beschreibungError", "Error, Beschreibung darf nicht leer sein");

        if (betrag <= 0)
            redirectAttributes.addFlashAttribute("betragError", "Error, Betrag darf nicht kleiner gleich Null sein");

        if (ausgeber.isBlank())
            redirectAttributes.addFlashAttribute("ausgeberError", "Error, Ausgeber darf nicht leer sein");

        if (empfangerString.isEmpty())
            redirectAttributes.addFlashAttribute("empfangerError", "Error, Empfänger darf nicht leer sein");

        return "redirect:/detail?nr=" + nr;
    }
}
