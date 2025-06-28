package tn.esprit.spring.Chambre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.*;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.Services.Chambre.ChambreService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import tn.esprit.spring.Services.Chambre.IChambreService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ChambreServiceTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private BlocRepository blocRepository;

    // Use real service with injected mocks:
    private IChambreService chambreService;

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
    void testGetChambresNonReserveParNomFoyerEtTypeChambre() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer A");
        sampleBloc.setFoyer(foyer);

        Chambre ch = new Chambre(1L, 101, TypeChambre.SIMPLE, sampleBloc, new ArrayList<>());

        when(chambreRepository.findAll()).thenReturn(List.of(ch));

        List<Chambre> result = chambreService.getChambresNonReserveParNomFoyerEtTypeChambre("Foyer A", TypeChambre.SIMPLE);

        assertEquals(1, result.size());
    }
}
