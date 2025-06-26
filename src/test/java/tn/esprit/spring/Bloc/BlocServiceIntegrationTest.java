package tn.esprit.spring.Bloc;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlocServiceIntegrationTest {

    @Autowired
    private BlocService blocService;

    @Autowired
    private BlocRepository blocRepository;

    @Autowired
    private FoyerRepository foyerRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data before each test
        chambreRepository.deleteAll();
        blocRepository.deleteAll();
        foyerRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAddOrUpdate() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Alpha")
                .capaciteBloc(100)
                .build();

        // When
        Bloc result = blocService.addOrUpdate(bloc);

        // Then
        assertNotNull(result, "Bloc returned by service should not be null");
        assertNotNull(result.getIdBloc(), "Bloc ID should not be null after save");
        assertEquals("Bloc Alpha", result.getNomBloc());

        // Verify it was actually saved
        Bloc fromDb = blocRepository.findById(result.getIdBloc()).orElse(null);
        assertNotNull(fromDb, "Bloc should exist in database");
    }

    @Test
    @Order(2)
    void testFindAll() {
        // Given
        blocService.addOrUpdate(Bloc.builder().nomBloc("Bloc1").capaciteBloc(50).build());
        blocService.addOrUpdate(Bloc.builder().nomBloc("Bloc2").capaciteBloc(75).build());

        // When
        List<Bloc> blocs = blocService.findAll();

        // Then
        assertNotNull(blocs, "List of blocs should not be null");
        assertFalse(blocs.isEmpty(), "List of blocs should not be empty");
        assertThat(blocs)
                .extracting(Bloc::getNomBloc)
                .contains("Bloc1", "Bloc2");
    }

    @Test
    @Order(3)
    void testAddOrUpdateWithChambres() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Beta")
                .capaciteBloc(200)
                .chambres(new ArrayList<>())
                .build();

        Chambre chambre = Chambre.builder()
                .numeroChambre(101L)
                .bloc(bloc)
                .build();

        bloc.getChambres().add(chambre);

        // When
        Bloc saved = blocService.addOrUpdate(bloc);

        // Then
        assertNotNull(saved, "Saved bloc should not be null");
        assertNotNull(saved.getIdBloc(), "Saved bloc ID should not be null");

        // Verify chambres were saved and linked
        Bloc fromDb = blocRepository.findById(saved.getIdBloc())
                .orElseThrow(() -> new AssertionError("Bloc not found in DB"));

        assertThat(fromDb.getChambres())
                .isNotNull()
                .hasSize(1)
                .allMatch(c -> c.getBloc().equals(fromDb));
    }

    @Test
    @Order(4)
    void testAffecterBlocAFoyer() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer Central")
                .capaciteFoyer(200L)
                .build();
        foyer = foyerRepository.save(foyer);
        assertNotNull(foyer.getIdFoyer(), "Foyer should be saved");

        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc C")
                .capaciteBloc(80)
                .build();
        bloc = blocRepository.save(bloc);
        assertNotNull(bloc.getIdBloc(), "Bloc should be saved");

        // When
        Bloc updated = blocService.affecterBlocAFoyer(bloc.getNomBloc(), foyer.getNomFoyer());

        // Then
        assertNotNull(updated, "Updated bloc should not be null");
        assertNotNull(updated.getFoyer(), "Bloc's foyer should not be null");
        assertEquals(foyer.getIdFoyer(), updated.getFoyer().getIdFoyer());

        // Verify the relationship is bidirectional
        Foyer updatedFoyer = foyerRepository.findById(foyer.getIdFoyer())
                .orElseThrow(() -> new AssertionError("Foyer not found"));
        assertThat(updatedFoyer.getBlocs())
                .isNotNull()
                .contains(updated);
    }
}