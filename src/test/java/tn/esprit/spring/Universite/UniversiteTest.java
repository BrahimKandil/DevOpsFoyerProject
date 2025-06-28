package tn.esprit.spring.Universite;


import org.junit.jupiter.api.Test;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.Universite;

import static org.assertj.core.api.Assertions.*;

class UniversiteTest {

    @Test
    void testBuilderAndGettersSetters() {
        Foyer foyer = Foyer.builder()
                .idFoyer(1L)
                .nomFoyer("Main Foyer")
                .capaciteFoyer(200L)
                .build();

        Universite universite = Universite.builder()
                .idUniversite(10L)
                .nomUniversite("Université de Tunis")
                .adresse("Rue de la Paix")
                .foyer(foyer)
                .build();

        // Check builder fields
        assertThat(universite.getIdUniversite()).isEqualTo(10L);
        assertThat(universite.getNomUniversite()).isEqualTo("Université de Tunis");
        assertThat(universite.getAdresse()).isEqualTo("Rue de la Paix");
        assertThat(universite.getFoyer()).isEqualTo(foyer);

        // Test setters
        universite.setNomUniversite("Université de Carthage");
        universite.setAdresse("Avenue Bourguiba");
        assertThat(universite.getNomUniversite()).isEqualTo("Université de Carthage");
        assertThat(universite.getAdresse()).isEqualTo("Avenue Bourguiba");

        // Test setting foyer
        Foyer newFoyer = new Foyer();
        newFoyer.setIdFoyer(2L);
        universite.setFoyer(newFoyer);
        assertThat(universite.getFoyer()).isEqualTo(newFoyer);
    }
}
