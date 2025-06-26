package tn.esprit.spring.Chambre;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.Services.Chambre.ChambreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChambreServiceIntegrationTest {

    @Autowired
    private ChambreService chambreService;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private BlocRepository blocRepository;

    @BeforeEach
    void setUp() {
        // Clear data before each test
        chambreRepository.deleteAll();
        blocRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAddOrUpdateChambre() {
        // Given
        Bloc bloc = blocRepository.save(
                Bloc.builder()
                        .nomBloc("Bloc Test")
                        .capaciteBloc(100)
                        .build()
        );
        assertNotNull(bloc.getIdBloc(), "Bloc should be saved");

        Chambre chambre = Chambre.builder()
                .numeroChambre(1L)
                .typeC(TypeChambre.SIMPLE)
                .bloc(bloc)
                .build();

        // When
        Chambre saved = chambreService.addOrUpdate(chambre);

        // Then
        assertNotNull(saved, "Chambre should be saved");
        assertNotNull(saved.getIdChambre(), "Chambre ID should be generated");
        assertEquals(1L, saved.getNumeroChambre());
        assertEquals(bloc.getIdBloc(), saved.getBloc().getIdBloc());
    }

    @Test
    @Order(2)
    void testFindAllChambres() {
        // Given
        Bloc bloc = blocRepository.save(
                Bloc.builder()
                        .nomBloc("Bloc B")
                        .capaciteBloc(50)
                        .build()
        );

        chambreRepository.save(
                Chambre.builder()
                        .numeroChambre(101L)
                        .typeC(TypeChambre.SIMPLE)
                        .bloc(bloc)
                        .build()
        );

        chambreRepository.save(
                Chambre.builder()
                        .numeroChambre(102L)
                        .typeC(TypeChambre.DOUBLE)
                        .bloc(bloc)
                        .build()
        );

        // When
        List<Chambre> chambres = chambreService.findAll();

        // Then
        assertNotNull(chambres, "List of chambres should not be null");
        assertTrue(chambres.size() >= 2, "Should find at least 2 chambres");
        assertThat(chambres)
                .extracting(Chambre::getNumeroChambre)
                .contains(101L, 102L);
    }

    @Test
    @Order(3)
    void testFindById() {
        // Given
        Bloc bloc = blocRepository.save(
                Bloc.builder()
                        .nomBloc("Bloc C")
                        .capaciteBloc(75)
                        .build()
        );

        Chambre chambre = chambreRepository.save(
                Chambre.builder()
                        .numeroChambre(303L)
                        .typeC(TypeChambre.TRIPLE)
                        .bloc(bloc)
                        .build()
        );

        // When
        Chambre found = chambreService.findById(chambre.getIdChambre());

        // Then
        assertNotNull(found, "Chambre should be found");
        assertEquals(303L, found.getNumeroChambre());
        assertEquals(TypeChambre.TRIPLE, found.getTypeC());
    }

    @Test
    @Order(4)
    void testDeleteById() {
        // Given
        Bloc bloc = blocRepository.save(
                Bloc.builder()
                        .nomBloc("Bloc D")
                        .capaciteBloc(60)
                        .build()
        );

        Chambre chambre = chambreRepository.save(
                Chambre.builder()
                        .numeroChambre(404L)
                        .typeC(TypeChambre.SIMPLE)
                        .bloc(bloc)
                        .build()
        );

        // When
        chambreService.deleteById(chambre.getIdChambre());

        // Then
        assertFalse(chambreRepository.existsById(chambre.getIdChambre()),
                "Chambre should be deleted");
    }

    @Test
    @Order(5)
    void testGetChambresParNomBloc() {
        // Given
        Bloc bloc = blocRepository.save(
                Bloc.builder()
                        .nomBloc("Bloc Special")
                        .capaciteBloc(80)
                        .build()
        );

        chambreRepository.save(
                Chambre.builder()
                        .numeroChambre(505L)
                        .typeC(TypeChambre.DOUBLE)
                        .bloc(bloc)
                        .build()
        );

        // When
        List<Chambre> chambres = chambreService.getChambresParNomBloc("Bloc Special");

        // Then
        assertNotNull(chambres, "List of chambres should not be null");
        assertFalse(chambres.isEmpty(), "Should find at least one chambre");
        assertEquals("Bloc Special", chambres.get(0).getBloc().getNomBloc());
    }
}