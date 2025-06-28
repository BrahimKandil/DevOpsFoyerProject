package tn.esprit.spring.Foyer;


import org.junit.jupiter.api.Test;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.Universite;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

 class FoyerTest {

    @Test
     void testNoArgsConstructor() {
        Foyer foyer = new Foyer();
        assertThat(foyer).isNotNull();
    }

    @Test
     void testAllArgsConstructor() {
        Bloc bloc1 = new Bloc();
        Bloc bloc2 = new Bloc();

        Foyer foyer = new Foyer(
                1L,
                "Main Foyer",
                100L,
                null,  // Assuming Universite can be null here
                List.of(bloc1, bloc2)
        );

        assertThat(foyer.getIdFoyer()).isEqualTo(1L);
        assertThat(foyer.getNomFoyer()).isEqualTo("Main Foyer");
        assertThat(foyer.getCapaciteFoyer()).isEqualTo(100L);
        assertThat(foyer.getBlocs()).containsExactly(bloc1, bloc2);
    }

    @Test
     void testBuilder() {
        Foyer foyer = Foyer.builder()
                .idFoyer(2L)
                .nomFoyer("Secondary Foyer")
                .capaciteFoyer(50L)
                .blocs(List.of())
                .build();

        assertThat(foyer.getIdFoyer()).isEqualTo(2L);
        assertThat(foyer.getNomFoyer()).isEqualTo("Secondary Foyer");
        assertThat(foyer.getCapaciteFoyer()).isEqualTo(50L);
        assertThat(foyer.getBlocs()).isEmpty();
    }

    @Test
     void testSettersAndGetters() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(3L);
        foyer.setNomFoyer("Test Foyer");
        foyer.setCapaciteFoyer(200L);

        assertThat(foyer.getIdFoyer()).isEqualTo(3L);
        assertThat(foyer.getNomFoyer()).isEqualTo("Test Foyer");
        assertThat(foyer.getCapaciteFoyer()).isEqualTo(200L);
    }

    @Test
     void testDefaultBlocsInitialization() {
        Foyer foyer = new Foyer();
        assertThat(foyer.getBlocs()).isNotNull();
        assertThat(foyer.getBlocs()).isEmpty();
    }
    @Test
    void testSetAndGetUniversite() {
       Foyer foyer = new Foyer();
       Universite universite = new Universite();
       foyer.setUniversite(universite);

       assertThat(foyer.getUniversite()).isEqualTo(universite);
    }
}
