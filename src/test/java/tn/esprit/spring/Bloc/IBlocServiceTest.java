package tn.esprit.spring.Bloc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.services.bloc.IBlocService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IBlocServiceTest {

    private IBlocService blocService;

    private Bloc testBloc;

    @BeforeEach
    void setup() {
        blocService = Mockito.mock(IBlocService.class);

        testBloc = new Bloc();
        testBloc.setIdBloc(1L);
        testBloc.setNomBloc("TestBloc");
    }

    @Test
    void testAddOrUpdate() {
        when(blocService.addOrUpdate(any(Bloc.class))).thenReturn(testBloc);

        Bloc result = blocService.addOrUpdate(new Bloc());
        assertNotNull(result);
        assertEquals("TestBloc", result.getNomBloc());
    }

    @Test
    void testFindAll() {
        when(blocService.findAll()).thenReturn(List.of(testBloc));

        List<Bloc> blocs = blocService.findAll();
        assertEquals(1, blocs.size());
        assertEquals("TestBloc", blocs.get(0).getNomBloc());
    }

    @Test
    void testFindById() {
        when(blocService.findById(1L)).thenReturn(testBloc);

        Bloc bloc = blocService.findById(1L);
        assertNotNull(bloc);
        assertEquals(1L, bloc.getIdBloc());
    }

    @Test
    void testDeleteById() {
        doNothing().when(blocService).deleteById(1L);

        blocService.deleteById(1L);
        verify(blocService).deleteById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(blocService).delete(testBloc);

        blocService.delete(testBloc);
        verify(blocService).delete(testBloc);
    }

    @Test
    void testAffecterChambresABloc() {
        when(blocService.affecterChambresABloc(anyList(), anyString())).thenReturn(testBloc);

        Bloc bloc = blocService.affecterChambresABloc(List.of(1L), "TestBloc");
        assertNotNull(bloc);
        assertEquals("TestBloc", bloc.getNomBloc());
    }

    @Test
    void testAffecterBlocAFoyer() {
        when(blocService.affecterBlocAFoyer(anyString(), anyString())).thenReturn(testBloc);

        Bloc bloc = blocService.affecterBlocAFoyer("TestBloc", "Foyer1");
        assertNotNull(bloc);
        assertEquals("TestBloc", bloc.getNomBloc());
    }

    @Test
    void testAjouterBlocEtSesChambres() {
        when(blocService.ajouterBlocEtSesChambres(any(Bloc.class))).thenReturn(testBloc);

        Bloc bloc = blocService.ajouterBlocEtSesChambres(new Bloc());
        assertNotNull(bloc);
        assertEquals("TestBloc", bloc.getNomBloc());
    }

    @Test
    void testAjouterBlocEtAffecterAFoyer() {
        when(blocService.ajouterBlocEtAffecterAFoyer(any(Bloc.class), anyString())).thenReturn(testBloc);

        Bloc bloc = blocService.ajouterBlocEtAffecterAFoyer(new Bloc(), "Foyer1");
        assertNotNull(bloc);
        assertEquals("TestBloc", bloc.getNomBloc());
    }
}
