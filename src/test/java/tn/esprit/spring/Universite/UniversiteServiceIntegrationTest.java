package tn.esprit.spring.Universite;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Universite.UniversiteService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UniversiteServiceIntegrationTest {

    @Autowired
    private UniversiteService universiteService;

    @Autowired
    private UniversiteRepository universiteRepository;

    private Universite universiteTemplate;

    @BeforeAll
    void init() {
        assertThat(universiteService).isNotNull();
        assertThat(universiteRepository).isNotNull();

        // Clear any existing data
        universiteRepository.deleteAll();

        universiteTemplate = Universite.builder()
                .nomUniversite("Université de Sousse")
                .adresse("Avenue Habib Bourguiba")
                .build();
    }

    @BeforeEach
    void setUp() {
        // Ensure clean state before each test
        universiteRepository.deleteAll();
    }

    private Universite createTestUniversite() {
        return Universite.builder()
                .nomUniversite(universiteTemplate.getNomUniversite())
                .adresse(universiteTemplate.getAdresse())
                .build();
    }

    @Test
    @Order(1)
    void testAddOrUpdateAndFindById() {
        // Given
        Universite universite = createTestUniversite();

        // When
        Universite saved = universiteService.addOrUpdate(universite);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getIdUniversite()).isNotNull();

        Universite found = universiteService.findById(saved.getIdUniversite());
        assertThat(found)
                .isNotNull()
                .extracting(Universite::getNomUniversite)
                .isEqualTo(universite.getNomUniversite());
    }

    @Test
    @Order(2)
    void testFindAll() {
        // Given
        universiteService.addOrUpdate(createTestUniversite());
        universiteService.addOrUpdate(Universite.builder()
                .nomUniversite("Second University")
                .adresse("Second Address")
                .build());

        // When
        List<Universite> list = universiteService.findAll();

        // Then
        assertThat(list)
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(3)
    void testDeleteAndDeleteById() {
        // Given
        Universite saved = universiteService.addOrUpdate(createTestUniversite());
        Long id = saved.getIdUniversite();

        // When - delete by ID
        universiteService.deleteById(id);

        // Then
        assertThat(universiteRepository.findById(id)).isEmpty();

        // Given - new entity
        Universite saved2 = universiteService.addOrUpdate(Universite.builder()
                .nomUniversite("To be deleted")
                .adresse("Delete me")
                .build());

        // When - delete by entity
        universiteService.delete(saved2);

        // Then
        assertThat(universiteRepository.findById(saved2.getIdUniversite())).isEmpty();
    }

    @Test
    @Order(4)
    void testAjouterUniversiteEtSonFoyer() {
        // Given
        Universite u = Universite.builder()
                .nomUniversite("Université with Foyer")
                .adresse("Foyer Address")
                .foyer(Foyer.builder()
                        .nomFoyer("Foyer Name")
                        .capaciteFoyer(100L)
                        .build())
                .build();

        // When
        Universite saved = universiteService.ajouterUniversiteEtSonFoyer(u);

        // Then
        assertThat(saved)
                .isNotNull()
                .extracting(
                        Universite::getIdUniversite,
                        Universite::getNomUniversite,
                        u2 -> u2.getFoyer() != null
                )
                .containsExactly(
                        saved.getIdUniversite(),
                        "Université with Foyer",
                        true
                );

        // Verify the foyer was properly saved and linked
        Optional<Universite> found = universiteRepository.findById(saved.getIdUniversite());
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Universite::getFoyer)
                .isNotNull();
    }
}