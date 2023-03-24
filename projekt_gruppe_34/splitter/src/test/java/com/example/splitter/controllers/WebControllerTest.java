package com.example.splitter.controllers;


import com.example.splitter.application.service.SplitterApplicationService;
import com.example.splitter.controllers.helper.WithMockOAuth2User;
import com.example.splitter.domain.model.gruppe.Gruppe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashSet;
import java.util.List;import java.util.Set;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import({WebSecurityConfiguration.class})
@WebMvcTest

class WebControllerTest {

    @MockBean
    SplitterApplicationService splitterApplicationService;

    @Autowired
     MockMvc mockMvc;

    @Test
    @DisplayName("get the mainpage")
    void index() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(view().name("mainpage"));
    }

    @Test
    @WithMockOAuth2User(login = "User")
    @DisplayName("model attribute überprüfen , wenn user vorhanden ist")
    void test01() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", "User"))
                .andExpect(model().attribute("gruppen", new HashSet<>()));
    }

    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("erstelle Gruppe")
    void test02() throws Exception {
        when(splitterApplicationService.getPersonId("user1")).thenReturn(1);
        mockMvc.perform(post("/")
                        .sessionAttr("name","user1")
                        .param("name", "G1").with(csrf()))
                        .andExpect(redirectedUrl("/"));
        verify(splitterApplicationService).erstelleGruppe(1,"G1");
    }

    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Gruppen Name muss nicht blank sein")
    void test0201() throws Exception {
        when(splitterApplicationService.getPersonId("user1")).thenReturn(1);
        mockMvc.perform(post("/")
                        .sessionAttr("name","user1")
                        .param("name", "").with(csrf()))
                .andExpect(redirectedUrl("/"));
        verify(splitterApplicationService, never()).erstelleGruppe(any(),any());
    }




    @Test
    @DisplayName("Gruppen Zugriff ohne Authentifizierung")
    void test_3() throws Exception {
        Gruppe gruppe = new Gruppe(1,"G1");
        Set<String> personennamen = Set.of("User");
        when(splitterApplicationService.getGruppePerId(1))
                .thenReturn(gruppe);
        when(splitterApplicationService.getPersonenPerGruppe(gruppe))
                .thenReturn(personennamen);
        mockMvc.perform(get("/detail")
                        .param("nr", "1"))
                .andExpect(status().is(401));
    }


    @Test
    @WithMockOAuth2User(login = "User")
    @DisplayName("zur Gruppe uebersicht weitergeleitet")
    void test_4() throws Exception {
        Gruppe gruppe = new Gruppe(1,"G1");
        Set<String> personennamen = Set.of("User");
        when(splitterApplicationService.getGruppePerId(1))
                .thenReturn(gruppe);
        when(splitterApplicationService.getPersonenPerGruppe(gruppe))
                .thenReturn(personennamen);
        when(splitterApplicationService.getAusgaben(gruppe.getId()))
                .thenReturn(List.of());

        mockMvc.perform(get("/detail")
                        .param("nr", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("personenNamen",personennamen))
                .andExpect(model().attribute("ausgaben",List.of()))
                .andExpect(view().name("detail"));
    }


    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Person zur Gruppe hinzufügen")
    void test05() throws Exception {
        Integer gruppeNummer = 1;
        when(splitterApplicationService.personExists("user2")).thenReturn(true);
        mockMvc.perform(post("/detail/addPerson")
                        .sessionAttr("gruppeNr",gruppeNummer)
                        .param("personName", "user2").with(csrf()));
    verify(splitterApplicationService).addPersonZuGruppePerName(gruppeNummer, "user2");
    }

    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Person darf nich blank sein")
    void test0501() throws Exception {
        Integer gruppeNummer = 1;
        when(splitterApplicationService.personExists("user2")).thenReturn(true);
        mockMvc.perform(post("/detail/addPerson")
                .sessionAttr("gruppeNr",gruppeNummer)
                .param("personName", "").with(csrf()))
                .andExpect(redirectedUrl("/detail?nr=1"));
        verify(splitterApplicationService,never()).addPersonZuGruppePerName(any(), any());
    }


    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Personen können ihren Gruppe schließen")
    void test06() throws Exception {
        Integer gruppeNummer = 1;
        mockMvc.perform(post("/detail/closeGroup")
                .sessionAttr("gruppeNr",gruppeNummer)
                .with(csrf()));
        verify(splitterApplicationService).gruppeSchliessen(gruppeNummer);
    }

    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Ausgaben innerhalb einer Gruppe hinzufuegen")
    void test07() throws Exception {
        mockMvc.perform(post("/detail/addExpenses")
                        .param("beschreibung","Hotel")
                        .param("betrag", "20.0")
                        .param("ausgeber","user1")
                        .param("empfanger", "user1,user2")
                .sessionAttr("gruppeNr",1)
                .with(csrf()));
        verify(splitterApplicationService).addTransaktionenPerName(1,"Hotel",20.0
                ,"user1",Set.of("user1","user2"));
    }

    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Ohne Beschreibung kann keine Transaktionen stattgefunden werden")
    void test0701() throws Exception {
        mockMvc.perform(post("/detail/addExpenses")
                .param("beschreibung","")
                .param("betrag", "20.0")
                .param("ausgeber","user1")
                .param("empfanger", "user1,user2")
                .sessionAttr("gruppeNr",1)
                .with(csrf()))
                .andExpect(redirectedUrl("/detail?nr=1"));
        verify(splitterApplicationService,never()).addTransaktionenPerName(any(),any(),anyDouble()
                ,any(),anySet());
    }

    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Ohne Beschreibung kann keine Transaktionen stattgefunden werden")
    void test0702() throws Exception {
        mockMvc.perform(post("/detail/addExpenses")
                        .param("beschreibung","")
                       // .param("betrag", "20.0")
                        .param("ausgeber","user1")
                        .param("empfanger", "user1,user2")
                        .sessionAttr("gruppeNr",1)
                        .with(csrf()))
                .andExpect(redirectedUrl("/detail?nr=1"));
        verify(splitterApplicationService,never()).addTransaktionenPerName(any(),any(),anyDouble()
                ,any(),anySet());
    }


    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Ohne Ausgeber kann keine Transaktionen stattgefunden werden")
    void test0703() throws Exception {
        mockMvc.perform(post("/detail/addExpenses")
                        .param("beschreibung","")
                         .param("betrag", "20.0")
                        //.param("ausgeber","user1")
                        .param("empfanger", "user1,user2")
                        .sessionAttr("gruppeNr",1)
                        .with(csrf()))
                .andExpect(redirectedUrl("/detail?nr=1"));
        verify(splitterApplicationService,never()).addTransaktionenPerName(any(),any(),anyDouble()
                ,any(),anySet());
    }


    @Test
    @WithMockOAuth2User(login = "user1")
    @DisplayName("Ohne Empfaenger kann keine Transaktionen stattgefunden werden")
    void test0704() throws Exception {
        mockMvc.perform(post("/detail/addExpenses")
                        .param("beschreibung","")
                        .param("betrag", "20.0")
                        .param("ausgeber","user1")
                       // .param("empfanger", "user1,user2")
                        .sessionAttr("gruppeNr",1)
                        .with(csrf()))
                .andExpect(redirectedUrl("/detail?nr=1"));
        verify(splitterApplicationService,never()).addTransaktionenPerName(any(),any(),anyDouble()
                ,any(),anySet());
    }


}