package tn.esprit.spring.Chambre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.services.chambre.ChambreService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IChambreServiceTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private ChambreService chambreService;

    private Chambre sampleChambre;

    @BeforeEach
    void setup() {
        sampleChambre = new Chambre(1L, 101, TypeChambre.SIMPLE, null, new ArrayList<>());
    }

    @Test
    void testAddOrUpdate() {
        when(chambreRepository.save(any(Chambre.class))).thenReturn(sampleChambre);
        Chambre result = chambreService.addOrUpdate(sampleChambre);
        assertEquals(1L, result.getIdChambre());
    }

    @Test
    void testFindAll() {
        when(chambreRepository.findAll()).thenReturn(List.of(sampleChambre));
        List<Chambre> result = chambreService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(sampleChambre));
        Chambre result = chambreService.findById(1L);
        assertEquals(TypeChambre.SIMPLE, result.getTypeC());
    }

    @Test
    void testDeleteById() {
        doNothing().when(chambreRepository).deleteById(1L);
        chambreService.deleteById(1L);
        verify(chambreRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(chambreRepository).delete(sampleChambre);
        chambreService.delete(sampleChambre);
        verify(chambreRepository, times(1)).delete(sampleChambre);
    }

    @Test
    void testGetChambresParNomBloc() {
        when(chambreRepository.findByBlocNomBloc("Bloc A")).thenReturn(List.of(sampleChambre));
        List<Chambre> result = chambreService.getChambresParNomBloc("Bloc A");
        assertEquals(1, result.size());
    }

    @Test
    void testNbChambreParTypeEtBloc() {
        sampleChambre.setTypeC(TypeChambre.DOUBLE);
        Bloc bloc = new Bloc();
        bloc.setIdBloc(2L);
        sampleChambre.setBloc(bloc);

        when(chambreRepository.findAll()).thenReturn(List.of(sampleChambre));
        long count = chambreService.nbChambreParTypeEtBloc(TypeChambre.DOUBLE, 2L);
        assertEquals(1, count);
    }

    @Test
    void testGetChambresParNomBlocJava() {
        Bloc bloc = new Bloc();
        bloc.setNomBloc("Bloc A");
        bloc.setChambres(List.of(sampleChambre));

        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(bloc);
        List<Chambre> result = chambreService.getChambresParNomBlocJava("Bloc A");
        assertEquals(1, result.size());
    }

    @Test
    void testGetChambresParNomBlocKeyWord() {
        when(chambreRepository.findByBlocNomBloc("Bloc A")).thenReturn(List.of(sampleChambre));
        List<Chambre> result = chambreService.getChambresParNomBlocKeyWord("Bloc A");
        assertEquals(1, result.size());
    }

    @Test
    void testGetChambresParNomBlocJPQL() {
        when(chambreRepository.getChambresParNomBlocJPQL("Bloc A")).thenReturn(List.of(sampleChambre));
        List<Chambre> result = chambreService.getChambresParNomBlocJPQL("Bloc A");
        assertEquals(1, result.size());
    }

    @Test
    void testGetChambresParNomBlocSQL() {
        when(chambreRepository.getChambresParNomBlocSQL("Bloc A")).thenReturn(List.of(sampleChambre));
        List<Chambre> result = chambreService.getChambresParNomBlocSQL("Bloc A");
        assertEquals(1, result.size());
    }

    // Methods like getChambresNonReserveParNomFoyerEtTypeChambre(),
    // listeChambresParBloc(), pourcentageChambreParTypeChambre(),
    // nbPlacesDisponibleParChambreAnneeEnCours() can be tested separately
    // with more complex mocking of reservation and log output.
}
