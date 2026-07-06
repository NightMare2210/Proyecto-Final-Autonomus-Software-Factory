package org.ups.citasaludservice.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "org.ups.citasaludservice",
    importOptions = com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_does_not_depend_on_infrastructure =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_application =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
                .resideInAPackage("..application..");

    @ArchTest
    static final ArchRule application_does_not_depend_on_infrastructure =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule controllers_do_not_depend_on_application_services =
        noClasses().that().resideInAPackage("..adapter.in.web..")
            .should().dependOnClassesThat()
                .resideInAPackage("..application.usecase..");
}
