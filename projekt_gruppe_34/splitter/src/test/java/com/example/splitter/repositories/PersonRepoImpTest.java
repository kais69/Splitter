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
import com.example.splitter.domain.model.person.Person;


import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(properties = "spring.flyway.enabled = false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Sql("classpath:/db/migration/testSql.sql")
class PersonRepoImpTest {

    @Autowired
    SpringDataPersonRepo springDataPersonRepo;

    PersonRepoImp personRepoImp;

    @BeforeEach
    void initiate() {
        personRepoImp=new PersonRepoImp(springDataPersonRepo);
    }
    @Test
    @DisplayName("nicht vorhandene Student kann nicht aus dem DatenBank  geladen werden ")
    void test_1(){
        assertThat(personRepoImp.findByGitHubName("´Sam")).isEmpty();
    }

    @Test
    @DisplayName("Vorhandene Student kann  aus dem DatenBank  geladen werden ")
    void test_2() {

        personRepoImp.save(new Person(null,"mark"));
        Person person = personRepoImp.findByGitHubName("mark").get();
        assertThat(person).isNotNull();
        assertThat(person.getId()).isEqualTo(1);
    }


    @Test
    @DisplayName("Personen werden in der Datenbank gespeichert")
    void test_3() {
        personRepoImp.save(new Person(null,"ahmad"));
        personRepoImp.save(new Person(null,"hamad"));
        assertThat(personRepoImp.findAll().size()).isEqualTo(2);
    }



    @Test
    void findByGruppeId() {
        personRepoImp.save(new Person(null,"ahmad"));
        personRepoImp.save(new Person(null,"hamad"));
        assertThat(personRepoImp.findById(1).get().getGitHubName()).isEqualTo("ahmad");
        assertThat(personRepoImp.findById(2).get().getGitHubName()).isEqualTo("hamad");

    }



}