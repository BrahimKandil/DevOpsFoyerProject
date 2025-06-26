package tn.esprit.spring.Foyer;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Foyer.FoyerService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FoyerServiceIntegrationTest {

    @Autowired
    private FoyerService foyerService;

    @Autowired
    private FoyerRepository foyerRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private BlocRepository blocRepository;

    @BeforeEach
    void clearData() {
        blocRepository.deleteAll();
        foyerRepository.deleteAll();
        universiteRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAddOrUpdateAndFind() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("Integration Foyer")
                .capaciteFoyer(200L)
                .blocs(new ArrayList<>())
                .build();

        // When
        Foyer saved = foyerService.addOrUpdate(foyer);

        // Then
        assertNotNull(saved, "Saved foyer should not be null");
        assertNotNull(saved.getIdFoyer(), "Foyer ID should be generated");

        // Verify find
        Foyer found = foyerService.findById(saved.getIdFoyer());
        assertNotNull(found, "Should be able to find saved foyer");
        assertEquals("Integration Foyer", found.getNomFoyer());

        // Verify in repository
        assertTrue(foyerRepository.existsById(saved.getIdFoyer()));
    }

    @Test
    @Order(2)
    void testAjouterFoyerEtAffecterAUniversite() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("Test Uni")
                .build();
        universite = universiteRepository.save(universite);
        assertNotNull(universite.getIdUniversite(), "Universite should be saved");

        Bloc bloc1 = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(50)
                .build();
        Bloc bloc2 = Bloc.builder()
                .nomBloc("Bloc B")
                .capaciteBloc(60)
                .build();

        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer With Blocs")
                .capaciteFoyer(110L)
                .blocs(List.of(bloc1, bloc2))
                .build();

        // When
        Foyer savedFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, universite.getIdUniversite());

        // Then
        assertNotNull(savedFoyer, "Foyer should be saved");
        assertNotNull(savedFoyer.getIdFoyer(), "Foyer ID should be generated");

        // Verify universite-foyer association
        Universite updatedUni = universiteRepository.findById(universite.getIdUniversite())
                .orElseThrow(() -> new AssertionError("Universite not found"));
        assertNotNull(updatedUni.getFoyer(), "Universite should have associated foyer");
        assertEquals(savedFoyer.getIdFoyer(), updatedUni.getFoyer().getIdFoyer());

        // Verify blocs are saved and linked
        List<Bloc> blocs = blocRepository.findAll();
        assertThat(blocs)
                .hasSize(2)
                .allMatch(b -> b.getFoyer() != null &&
                        Long.valueOf(b.getFoyer().getIdFoyer()).equals(savedFoyer.getIdFoyer()));
    }

    @Test
    @Order(3)
    void testAffecterAndDesaffecterFoyerAUniversite() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("Test Foyer")
                .capaciteFoyer(100L)
                .build();
        foyer = foyerRepository.save(foyer);
        assertNotNull(foyer.getIdFoyer(), "Foyer should be saved");

        Universite universite = Universite.builder()
                .nomUniversite("Test Uni")
                .build();
        universite = universiteRepository.save(universite);
        assertNotNull(universite.getIdUniversite(), "Universite should be saved");

        // When - Affect
        Universite affected = foyerService.affecterFoyerAUniversite(foyer.getIdFoyer(), universite.getIdUniversite());

        // Then - Affect
        assertNotNull(affected, "Affected universite should not be null");
        assertNotNull(affected.getFoyer(), "Universite should have foyer");
        assertEquals(foyer.getIdFoyer(), affected.getFoyer().getIdFoyer());

        // Verify bidirectional relationship
        Foyer updatedFoyer = foyerRepository.findById(foyer.getIdFoyer())
                .orElseThrow(() -> new AssertionError("Foyer not found"));
        assertThat(updatedFoyer.getUniversite())
                .isNotNull()
                .extracting(Universite::getIdUniversite)
                .isEqualTo(universite.getIdUniversite());

        // When - Desaffect
        Universite desaffected = foyerService.desaffecterFoyerAUniversite(universite.getIdUniversite());

        // Then - Desaffect
        assertNotNull(desaffected, "Desaffected universite should not be null");
        assertNull(desaffected.getFoyer());

        // Verify bidirectional relationship is cleared
        Foyer clearedFoyer = foyerRepository.findById(foyer.getIdFoyer())
                .orElseThrow(() -> new AssertionError("Foyer not found"));
        assertNull(clearedFoyer.getUniversite());
    }

    @Test
    @Order(4)
    void testAjoutFoyerEtBlocs() {
        // Given
        Bloc bloc1 = Bloc.builder()
                .nomBloc("Bloc 1")
                .capaciteBloc(25)
                .build();
        Bloc bloc2 = Bloc.builder()
                .nomBloc("Bloc 2")
                .capaciteBloc(30)
                .build();

        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer Test")
                .capaciteFoyer(55L)
                .blocs(List.of(bloc1, bloc2))
                .build();

        // When
        Foyer saved = foyerService.ajoutFoyerEtBlocs(foyer);

        // Then
        assertNotNull(saved, "Foyer should be saved");
        assertNotNull(saved.getIdFoyer(), "Foyer ID should be generated");

        // Verify blocs
        List<Bloc> savedBlocs = blocRepository.findAll();
        assertThat(savedBlocs)
                .hasSize(2)
                .allMatch(b -> b.getFoyer() != null &&
                        Long.valueOf(b.getFoyer().getIdFoyer()).equals(saved.getIdFoyer()));
    }
}