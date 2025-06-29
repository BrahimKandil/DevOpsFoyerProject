package tn.esprit.spring.Chambre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.*;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.services.chambre.ChambreService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ChambreServiceTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private BlocRepository blocRepository;

    // Use real service with injected mocks:
    private ChambreService chambreService;

    private Chambre sampleChambre;
    private Bloc sampleBloc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize sample data
        sampleBloc = new Bloc(1L, "Bloc A", 10, null, null);
        sampleChambre = new Chambre(1L, 101, TypeChambre.DOUBLE, sampleBloc, new ArrayList<>());

        // Instantiate real service and inject mocks
        chambreService = new ChambreService(chambreRepository,chambreRepository, blocRepository);
    }

    @Test
    void testAddOrUpdate() {
        when(chambreRepository.save(sampleChambre)).thenReturn(sampleChambre);

        Chambre saved = chambreService.addOrUpdate(sampleChambre);

        assertNotNull(saved);
        assertEquals(1L, saved.getIdChambre());
        verify(chambreRepository).save(sampleChambre);
    }

    @Test
    void testFindAll() {
        when(chambreRepository.findAll()).thenReturn(List.of(sampleChambre));

        List<Chambre> result = chambreService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(sampleChambre));

        Chambre found = chambreService.findById(1L);

        assertNotNull(found);
        assertEquals(101, found.getNumeroChambre());
    }

    @Test
    void testDeleteById() {
        doNothing().when(chambreRepository).deleteById(1L);

        chambreService.deleteById(1L);

        verify(chambreRepository).deleteById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(chambreRepository).delete(sampleChambre);

        chambreService.delete(sampleChambre);

        verify(chambreRepository).delete(sampleChambre);
    }

    @Test
    void testGetChambresParNomBloc() {
        when(chambreRepository.findByBlocNomBloc("Bloc A")).thenReturn(List.of(sampleChambre));

        List<Chambre> result = chambreService.getChambresParNomBloc("Bloc A");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testNbChambreParTypeEtBloc() {
        Chambre ch2 = new Chambre(2L, 102, TypeChambre.DOUBLE, sampleBloc, new ArrayList<>());

        when(chambreRepository.findAll()).thenReturn(List.of(sampleChambre, ch2));

        long count = chambreService.nbChambreParTypeEtBloc(TypeChambre.DOUBLE, sampleBloc.getIdBloc());

        assertEquals(2, count);
    }
    @Test
    void testNbChambreParTypeEtBloc_FullCoverage() {
        // Chambre matching both idBloc and type
        Chambre ch1 = new Chambre(1L, 101, TypeChambre.DOUBLE, sampleBloc, new ArrayList<>());

        // Chambre with same idBloc but different type
        Chambre ch2 = new Chambre(2L, 102, TypeChambre.SIMPLE, sampleBloc, new ArrayList<>());

        // Chambre with same type but different bloc id
        Bloc otherBloc = new Bloc();
        otherBloc.setIdBloc(999L);
        Chambre ch3 = new Chambre(3L, 103, TypeChambre.DOUBLE, otherBloc, new ArrayList<>());

        when(chambreRepository.findAll()).thenReturn(List.of(ch1, ch2, ch3));

        long count = chambreService.nbChambreParTypeEtBloc(TypeChambre.DOUBLE, sampleBloc.getIdBloc());

        // Only ch1 matches both conditions, so count should be 1
        assertEquals(1, count);
    }


    @Test
    void testGetChambresNonReserveParNomFoyerEtTypeChambre() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer A");
        sampleBloc.setFoyer(foyer);

        Chambre ch = new Chambre(1L, 101, TypeChambre.SIMPLE, sampleBloc, new ArrayList<>());

        when(chambreRepository.findAll()).thenReturn(List.of(ch));

        List<Chambre> result = chambreService.getChambresNonReserveParNomFoyerEtTypeChambre("Foyer A", TypeChambre.SIMPLE);

        assertEquals(1, result.size());
    }
    @Test
    void testGetChambresNonReserveParNomFoyerEtTypeChambre_FullCoverage() {
        // Mock current date
        LocalDate fakeNow = LocalDate.of(2024, 3, 1);
        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fakeNow);

            // Setup foyer and bloc
            Foyer foyer = new Foyer();
            foyer.setNomFoyer("Foyer A");
            Bloc bloc = new Bloc();
            bloc.setFoyer(foyer);

            // Create test data - ALL reservations must have non-null dates
            Reservation validRes = new Reservation();
            validRes.setAnneeUniversitaire(LocalDate.of(2024, 1, 15));
            validRes.setEstValide(true);

            // Test chambres
            Chambre chambre1 = new Chambre();
            chambre1.setTypeC(TypeChambre.SIMPLE);
            chambre1.setBloc(bloc);
            chambre1.setReservations(List.of(validRes)); // 1 reservation (capacity = 1)

            Chambre chambre2 = new Chambre();
            chambre2.setTypeC(TypeChambre.DOUBLE);
            chambre2.setBloc(bloc);
            chambre2.setReservations(new ArrayList<>()); // Empty list

            when(chambreRepository.findAll()).thenReturn(List.of(chambre1, chambre2));

            // Test
            List<Chambre> result = chambreService.getChambresNonReserveParNomFoyerEtTypeChambre(
                    "Foyer A", TypeChambre.DOUBLE);

            assertEquals(1, result.size());
            assertEquals(chambre2, result.get(0));
        }
    }
    @Test
    void testIsMatchingFoyerAndType_ShouldReturnTrue() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer A");
        Bloc bloc = new Bloc();
        bloc.setFoyer(foyer);
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setBloc(bloc);

        assertTrue(chambreService.isMatchingFoyerAndType(chambre, "Foyer A", TypeChambre.SIMPLE));
    }

    @Test
    void testIsMatchingFoyerAndType_ShouldReturnFalse_WhenWrongFoyer() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer B");
        Bloc bloc = new Bloc();
        bloc.setFoyer(foyer);
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setBloc(bloc);

        assertFalse(chambreService.isMatchingFoyerAndType(chambre, "Foyer A", TypeChambre.SIMPLE));
    }

    @Test
    void testIsMatchingFoyerAndType_ShouldReturnFalse_WhenWrongType() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer A");
        Bloc bloc = new Bloc();
        bloc.setFoyer(foyer);
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.TRIPLE);
        chambre.setBloc(bloc);

        assertFalse(chambreService.isMatchingFoyerAndType(chambre, "Foyer A", TypeChambre.DOUBLE));
    }

    @Test
    void testIsChambreAvailable_SimpleChambre_NoReservations_ShouldReturnTrue() {
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setReservations(new ArrayList<>());

        boolean result = chambreService.isChambreAvailable(chambre, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertTrue(result);
    }

    @Test
    void testIsChambreAvailable_DoubleChambre_OneReservationInRange_ShouldReturnTrue() {
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.setReservations(List.of(new Reservation())); // 1 in range
        boolean result = chambreService.isChambreAvailable(chambre, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertTrue(result);
    }

    @Test
    void testIsChambreAvailable_TripleChambre_ThreeReservationsInRange_ShouldReturnFalse() {
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.TRIPLE);
        ArrayList<Reservation> reservations = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for(int i = 0; i < 3; i++) {
            Reservation r = new Reservation();
            if(i==0) {
                r.setAnneeUniversitaire(now);

            } else if (i==1) {
                r.setAnneeUniversitaire(now.minusDays(1));

            }else {
                r.setAnneeUniversitaire(now.plusDays(1));
            }
            reservations.add(r);
        }
        chambre.setReservations(reservations);

        boolean result = chambreService.isChambreAvailable(chambre, LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));
        assertFalse(result);
    }


    @Test
    void testListeChambresParBloc() {
        // Mock some blocs and chambres
        Chambre chambre1 = new Chambre();
        chambre1.setNumeroChambre(101L);
        chambre1.setTypeC(TypeChambre.SIMPLE);

        Bloc bloc1 = new Bloc();
        bloc1.setNomBloc("Bloc A");
        bloc1.setCapaciteBloc(10);
        bloc1.setChambres(List.of(chambre1));

        Bloc bloc2 = new Bloc();
        bloc2.setNomBloc("Bloc B");
        bloc2.setCapaciteBloc(5);
        bloc2.setChambres(Collections.emptyList());

        when(blocRepository.findAll()).thenReturn(List.of(bloc1, bloc2));

        // Call method
        chambreService.listeChambresParBloc();

        // Optionally verify blocRepository called once
        verify(blocRepository, times(1)).findAll();
    }
    @Test
    void testPourcentageChambreParTypeChambre() {
        // Mock counts
        when(chambreRepository.count()).thenReturn(10L);
        when(chambreRepository.countChambreByTypeC(TypeChambre.SIMPLE)).thenReturn(4L);
        when(chambreRepository.countChambreByTypeC(TypeChambre.DOUBLE)).thenReturn(3L);
        when(chambreRepository.countChambreByTypeC(TypeChambre.TRIPLE)).thenReturn(3L);

        chambreService.pourcentageChambreParTypeChambre();

        verify(chambreRepository, times(1)).count();
        verify(chambreRepository, times(1)).countChambreByTypeC(TypeChambre.SIMPLE);
        verify(chambreRepository, times(1)).countChambreByTypeC(TypeChambre.DOUBLE);
        verify(chambreRepository, times(1)).countChambreByTypeC(TypeChambre.TRIPLE);
    }


}
