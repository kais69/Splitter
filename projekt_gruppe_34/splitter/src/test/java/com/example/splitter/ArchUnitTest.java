package com.example.splitter;

import com.example.splitter.stereotypes.AggregateRoot;
import com.example.splitter.stereotypes.ClassOnly;
import com.example.splitter.stereotypes.Entity;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packagesOf = SplitterApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {


    @ArchTest
    ArchRule AggregateRootMustBePublic = classes()
            .that()
            .resideInAPackage("..domain.model..")
            .and().areAnnotatedWith(AggregateRoot.class)
            .should().bePublic();

    @ArchTest
    ArchRule EntityMustNotBePublic = classes()
            .that()
            .resideInAPackage("..domain.model..")
            .and().areAnnotatedWith(Entity.class)
            .should().notBePublic();
    @ArchTest
    ArchRule noSetterInjectionAreUsed = noMethods()
            .should()
            .beAnnotatedWith(Autowired.class);

    @ArchTest
    ArchRule onionTest = onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application.service..")
            .adapter("web","..controllers..")
            .adapter("db", "..repositories..")
            .adapter("security", "..config..")
            .adapter("api","..api..")
            ;


    @ArchTest
    ArchRule serviceClassAnnotated = classes()
            .that()
            .resideInAPackage("..domain.service..")
            .and().arePublic()
            .should().beAnnotatedWith(Service.class);

    @ArchTest
    ArchRule methodsNotMitClassOnlyArePublic =methods()
            .that()
            .areAnnotatedWith(ClassOnly.class)
            .should()
            .bePrivate();

    @ArchTest
    ArchRule controllerClassAnnotated = classes()
            .that()
            .resideInAPackage("..controllers..")
            .should().beAnnotatedWith(Controller.class);

    @ArchTest
    ArchRule repositoriesClassAnnotated = classes()
            .that()
            .areNotInterfaces()
            .and()
            .arePublic()
            .and()
            .resideInAPackage("..repositories..")
            .should().beAnnotatedWith(Repository.class);

    @ArchTest
    ArchRule configClassAnnotated = classes()
            .that()
            .resideInAPackage("..config..")
            .should().beAnnotatedWith(Configuration.class);


    @ArchTest
    ArchRule appServiceClassAnnotated = classes()
            .that()
            .areNotInterfaces()
            .and()
            .arePublic()
            .and()
            .resideInAPackage("..application.service..")
            .should().beAnnotatedWith(Service.class);

}
