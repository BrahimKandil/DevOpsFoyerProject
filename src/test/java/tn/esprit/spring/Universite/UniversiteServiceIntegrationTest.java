package tn.esprit.spring.Universite;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Universite.UniversiteService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UniversiteServiceIntegrationTest {

    @Autowired
    private UniversiteService universiteService;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private FoyerRepository foyerRepository;

    @BeforeEach
    void setUp() {
        // Clear data before each test
        foyerRepository.deleteAll();
        universiteRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAddOrUpdateAndFindById() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("Test University")
                .adresse("Test Address")
                .build();

        // When
        Universite saved = universiteService.addOrUpdate(universite);

        // Then
        assertNotNull(saved, "Saved university should not be null");
        assertNotNull(saved.getIdUniversite(), "University ID should be generated");

        // Verify find
        Universite found = universiteService.findById(saved.getIdUniversite());
        assertNotNull(found, "Should be able to find saved university");
        assertEquals("Test University", found.getNomUniversite());
    }

    @Test
    @Order(2)
    void testFindAll() {
        // Given
        universiteRepository.save(Universite.builder()
                .nomUniversite("University 1")
                .adresse("Address 1")
                .build());

        universiteRepository.save(Universite.builder()
                .nomUniversite("University 2")
                .adresse("Address 2")
                .build());

        // When
        List<Universite> list = universiteService.findAll();

        // Then
        assertNotNull(list, "List of universities should not be null");
        assertTrue(list.size() >= 2, "Should find at least 2 universities");
    }

    @Test
    @Order(3)
    void testDeleteAndDeleteById() {
        // Given
        Universite saved = universiteRepository.save(Universite.builder()
                .nomUniversite("To Delete")
                .adresse("Delete Address")
                .build());

        Long id = saved.getIdUniversite();

        // When - delete by ID
        universiteService.deleteById(id);

        // Then
        assertFalse(universiteRepository.existsById(id), "University should be deleted");

        // Given - new entity
        Universite saved2 = universiteRepository.save(Universite.builder()
                .nomUniversite("To Delete 2")
                .adresse("Delete Address 2")
                .build());

        // When - delete by entity
        universiteService.delete(saved2);

        // Then
        assertFalse(universiteRepository.existsById(saved2.getIdUniversite()),
                "University should be deleted");
    }

    @Test
    @Order(4)
    void testAjouterUniversiteEtSonFoyer() {
        // Given
        Universite u = Universite.builder()
                .nomUniversite("University with Foyer")
                .adresse("Foyer Address")
                .foyer(Foyer.builder()
                        .nomFoyer("Foyer Name")
                        .capaciteFoyer(100L)
                        .build())
                .build();

        // When
        Universite saved = universiteService.ajouterUniversiteEtSonFoyer(u);

        // Then
        assertNotNull(saved, "University should be saved");
        assertNotNull(saved.getIdUniversite(), "University ID should be generated");
        assertNotNull(saved.getFoyer(), "Foyer should be set");
        assertNotNull(saved.getFoyer().getIdFoyer(), "Foyer ID should be generated");

        // Verify both entities were saved
        assertTrue(universiteRepository.existsById(saved.getIdUniversite()));
        assertTrue(foyerRepository.existsById(saved.getFoyer().getIdFoyer()));
    }
}