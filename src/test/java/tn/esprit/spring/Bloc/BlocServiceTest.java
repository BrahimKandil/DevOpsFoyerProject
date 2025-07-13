package tn.esprit.spring.Bloc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;
import tn.esprit.spring.services.bloc.BlocService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private BlocService blocService;

    private Bloc bloc;
    private Chambre chambre;

    @BeforeEach
    void setup() {
        chambre = new Chambre();
        chambre.setNumeroChambre(101L);

        bloc = new Bloc();
        bloc.setIdBloc(1L);
        bloc.setNomBloc("Bloc A");
        bloc.setChambres(List.of(chambre));
    }

    @Test
    void testAddOrUpdate() {
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        Bloc result = blocService.addOrUpdate(bloc);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository).save(bloc);
        verify(chambreRepository).save(any(Chambre.class));
    }

    @Test
    void testFindAll() {
        when(blocRepository.findAll()).thenReturn(List.of(bloc));
        List<Bloc> blocs = blocService.findAll();
        assertEquals(1, blocs.size());
    }

    @Test
    void testFindById() {
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));
        Bloc result = blocService.findById(1L);
        assertEquals("Bloc A", result.getNomBloc());
    }

    @Test
    void testDeleteById_WhenBlocExists() {
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));
        blocService.deleteById(1L);
        verify(chambreRepository).deleteAll(bloc.getChambres());
        verify(blocRepository).delete(bloc);
    }

    @Test
    void testDeleteById_WhenBlocDoesNotExist() {
        when(blocRepository.findById(1L)).thenReturn(Optional.empty());

        blocService.deleteById(1L);

        verify(chambreRepository, never()).deleteAll(any());
        verify(blocRepository, never()).delete(any());
    }
    @Test
    void testAddOrUpdate2() {
        // Prepare a Bloc with chambres
        Bloc blocx = new Bloc();
        List<Chambre> chambres = new ArrayList<>();

        Chambre c1 = new Chambre();
        Chambre c2 = new Chambre();

        chambres.add(c1);
        chambres.add(c2);

        blocx.setChambres(chambres);

        // Mock chambreRepository.save to return the passed chambre (optional)
        when(chambreRepository.save(any(Chambre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        Bloc result = blocService.addOrUpdate2(blocx);

        // Verify each chambre's bloc is set correctly
        for (Chambre c : chambres) {
            assertEquals(blocx, c.getBloc());
        }

        // Verify save was called for each chambre
        verify(chambreRepository, times(chambres.size())).save(any(Chambre.class));

        // Assert the method returns the bloc itself
        assertEquals(blocx, result);
    }


    @Test
    void testDelete() {
        blocService.delete(bloc);
        verify(chambreRepository).deleteAll(bloc.getChambres());
        verify(blocRepository).delete(bloc);
    }

    @Test
    void testAffecterChambresABloc() {
        Bloc b = new Bloc();
        b.setNomBloc("Bloc A");
        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(b);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);

        Bloc result = blocService.affecterChambresABloc(List.of(101L), "Bloc A");
        assertNotNull(result);
        verify(chambreRepository).save(any(Chambre.class));
    }

    @Test
    void testAffecterBlocAFoyer() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer 1");
        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(bloc);
        when(foyerRepository.findByNomFoyer("Foyer 1")).thenReturn(foyer);
        when(blocRepository.save(bloc)).thenReturn(bloc);

        Bloc result = blocService.affecterBlocAFoyer("Bloc A", "Foyer 1");
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository).save(bloc);
    }

    @Test
    void testAjouterBlocEtSesChambres() {
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);
        Bloc result = blocService.ajouterBlocEtSesChambres(bloc);
        assertEquals("Bloc A", result.getNomBloc());
    }

    @Test
    void testAjouterBlocEtAffecterAFoyer() {
        Foyer f = new Foyer();
        f.setNomFoyer("Foyer 1");
        when(foyerRepository.findByNomFoyer("Foyer 1")).thenReturn(f);
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.ajouterBlocEtAffecterAFoyer(bloc, "Foyer 1");
        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
    }
}
