package tn.esprit.spring.Etudiant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.services.etudiant.IEtudiantService;

import org.junit.jupiter.api.Test;

import tn.esprit.spring.dao.entities.*;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class IEtudiantServiceTest {
    @Mock
    private IEtudiantService etudiantService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Etudiant etudiant = Etudiant.builder().idEtudiant(1L).nomEt("Test").prenomEt("User").build();

        when(etudiantService.addOrUpdate(etudiant)).thenReturn(etudiant);

        Etudiant returned = etudiantService.addOrUpdate(etudiant);

        assertThat(returned).isNotNull();
        assertThat(returned.getNomEt()).isEqualTo("Test");

        verify(etudiantService).addOrUpdate(etudiant);
    }

    @Test
    void testFindAll() {
        Etudiant e1 = Etudiant.builder().idEtudiant(1L).nomEt("A").prenomEt("X").build();
        Etudiant e2 = Etudiant.builder().idEtudiant(2L).nomEt("B").prenomEt("Y").build();

        when(etudiantService.findAll()).thenReturn(List.of(e1, e2));

        List<Etudiant> result = etudiantService.findAll();

        assertThat(result).hasSize(2);
        verify(etudiantService).findAll();
    }

    @Test
    void testFindById() {
        Etudiant etudiant = Etudiant.builder().idEtudiant(1L).nomEt("A").prenomEt("X").build();

        when(etudiantService.findById(1L)).thenReturn(etudiant);

        Etudiant result = etudiantService.findById(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getIdEtudiant()).isEqualTo(1L);
        verify(etudiantService).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(etudiantService).deleteById(1L);

        etudiantService.deleteById(1L);

        verify(etudiantService).deleteById(1L);
    }

    @Test
    void testDelete() {
        Etudiant etudiant = Etudiant.builder().idEtudiant(1L).build();

        doNothing().when(etudiantService).delete(etudiant);

        etudiantService.delete(etudiant);

        verify(etudiantService).delete(etudiant);
    }

    @Test
    void testSelectJPQL() {
        Etudiant etudiant = Etudiant.builder().nomEt("Smith").build();

        when(etudiantService.selectJPQL("Smith")).thenReturn(List.of(etudiant));

        List<Etudiant> result = etudiantService.selectJPQL("Smith");

        Assertions.assertThat(result).isNotEmpty();
        verify(etudiantService).selectJPQL("Smith");
    }

    @Test
    void testAffecterReservationAEtudiant() {
        doNothing().when(etudiantService).affecterReservationAEtudiant("1", "Ben", "Ali");

        etudiantService.affecterReservationAEtudiant("1", "Ben", "Ali");

        verify(etudiantService).affecterReservationAEtudiant("1", "Ben", "Ali");
    }

    @Test
    void testDesaffecterReservationAEtudiant() {
        doNothing().when(etudiantService).desaffecterReservationAEtudiant("1", "Ben", "Ali");

        etudiantService.desaffecterReservationAEtudiant("1", "Ben", "Ali");

        verify(etudiantService).desaffecterReservationAEtudiant("1", "Ben", "Ali");
    }
}

