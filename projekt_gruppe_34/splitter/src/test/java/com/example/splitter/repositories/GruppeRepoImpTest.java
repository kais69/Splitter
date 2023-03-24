package com.example.splitter.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import com.example.splitter.domain.model.gruppe.Gruppe;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(properties = "spring.flyway.enabled = false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Sql("classpath:/db/migration/testSql.sql")
class GruppeRepoImpTest {

    @Autowired
    SpringDataGruppeRepo springDataGruppeRepo;

    GruppeRepoImp gruppeRepoImp;

    @BeforeEach
    void initiate() {
        gruppeRepoImp=new GruppeRepoImp(springDataGruppeRepo);
    }
    @Test
    @DisplayName("nicht vorhandene gruppe kann nicht aus dem DatenBank  geladen werden ")
    void test_1(){
        assertThat(gruppeRepoImp.findByGruppeId(2)).isEmpty();
    }



    @Test
    @DisplayName("Gruppe wird in der Datenbank gespeichert")
    void test_2() {

        //gruppe.addTransaktion(1,"hotel",20,"hamd","sam");
        gruppeRepoImp.save(new Gruppe( null,"g2"));

        assertThat(gruppeRepoImp.findAll().size()).isEqualTo(1);

    }


    @Test
    @DisplayName("vorhandene gruppe kann aus dem DatenBank geladen werden ")
    void test_3() {

        gruppeRepoImp.save(new Gruppe( null,"g1"));
        Gruppe gruppe = gruppeRepoImp.findByGruppeId(1).get();
        assertThat(gruppe).isNotNull();
        assertThat(gruppe.getName()).isEqualTo("g1");
    }

}