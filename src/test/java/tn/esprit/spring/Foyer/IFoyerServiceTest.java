package tn.esprit.spring.Foyer;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.services.foyer.IFoyerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

 class IFoyerServiceTest {

    @Mock
    private IFoyerService foyerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Foyer foyer = new Foyer();
        when(foyerService.addOrUpdate(foyer)).thenReturn(foyer);

        Foyer result = foyerService.addOrUpdate(foyer);

        assertThat(result).isNotNull();
        verify(foyerService).addOrUpdate(foyer);
    }

    @Test
    void testFindAll() {
        Foyer f1 = new Foyer();
        Foyer f2 = new Foyer();
        when(foyerService.findAll()).thenReturn(List.of(f1, f2));

        List<Foyer> list = foyerService.findAll();

        assertThat(list).hasSize(2);
        verify(foyerService).findAll();
    }

    @Test
    void testFindById() {
        Foyer f = new Foyer();
        when(foyerService.findById(1L)).thenReturn(f);

        Foyer result = foyerService.findById(1L);

        assertThat(result).isNotNull();
        verify(foyerService).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(foyerService).deleteById(1L);

        foyerService.deleteById(1L);

        verify(foyerService).deleteById(1L);
    }

    @Test
    void testDelete() {
        Foyer f = new Foyer();
        doNothing().when(foyerService).delete(f);

        foyerService.delete(f);

        verify(foyerService).delete(f);
    }

    @Test
    void testAffecterFoyerAUniversiteByIdName() {
        Universite u = new Universite();
        when(foyerService.affecterFoyerAUniversite(1L, "MyUni")).thenReturn(u);

        Universite result = foyerService.affecterFoyerAUniversite(1L, "MyUni");

        assertThat(result).isNotNull();
        verify(foyerService).affecterFoyerAUniversite(1L, "MyUni");
    }

    @Test
    void testDesaffecterFoyerAUniversite() {
        Universite u = new Universite();
        when(foyerService.desaffecterFoyerAUniversite(1L)).thenReturn(u);

        Universite result = foyerService.desaffecterFoyerAUniversite(1L);

        assertThat(result).isNotNull();
        verify(foyerService).desaffecterFoyerAUniversite(1L);
    }

    @Test
    void testAjouterFoyerEtAffecterAUniversite() {
        Foyer f = new Foyer();

        when(foyerService.ajouterFoyerEtAffecterAUniversite(f, 1L)).thenReturn(f);

        Foyer result = foyerService.ajouterFoyerEtAffecterAUniversite(f, 1L);

        assertThat(result).isNotNull();
        verify(foyerService).ajouterFoyerEtAffecterAUniversite(f, 1L);
    }

    @Test
    void testAjoutFoyerEtBlocs() {
        Foyer f = new Foyer();
        when(foyerService.ajoutFoyerEtBlocs(f)).thenReturn(f);

        Foyer result = foyerService.ajoutFoyerEtBlocs(f);

        assertThat(result).isNotNull();
        verify(foyerService).ajoutFoyerEtBlocs(f);
    }

    @Test
    void testAffecterFoyerAUniversiteByIds() {
        Universite u = new Universite();
        when(foyerService.affecterFoyerAUniversite(1L, 2L)).thenReturn(u);

        Universite result = foyerService.affecterFoyerAUniversite(1L, 2L);

        assertThat(result).isNotNull();
        verify(foyerService).affecterFoyerAUniversite(1L, 2L);
    }
}
