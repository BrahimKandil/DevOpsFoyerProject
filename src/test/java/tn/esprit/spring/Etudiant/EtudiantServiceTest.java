package tn.esprit.spring.Etudiant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.etudiant.EtudiantService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

class EtudiantServiceTest {

    @InjectMocks
    private EtudiantService etudiantService;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ReservationRepository reservationRepository;


    @Test
    void testAddOrUpdate() {
        Etudiant etudiant = Etudiant.builder().idEtudiant(1L).nomEt("Test").prenomEt("User").build();

        when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        Etudiant saved = etudiantService.addOrUpdate(etudiant);

        assertThat(saved).isNotNull();
        assertThat(saved.getNomEt()).isEqualTo("Test");
        verify(etudiantRepository).save(etudiant);
    }

    @Test
    void testFindAll() {
        Etudiant e1 = Etudiant.builder().idEtudiant(1L).nomEt("A").prenomEt("X").build();
        Etudiant e2 = Etudiant.builder().idEtudiant(2L).nomEt("B").prenomEt("Y").build();

        when(etudiantRepository.findAll()).thenReturn(List.of(e1, e2));

        List<Etudiant> list = etudiantService.findAll();

        assertThat(list).hasSize(2);
        verify(etudiantRepository).findAll();
    }

    @Test
    void testFindById() {
        Etudiant etudiant = Etudiant.builder().idEtudiant(1L).nomEt("A").prenomEt("X").build();

        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        Etudiant result = etudiantService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getIdEtudiant()).isEqualTo(1L);
        verify(etudiantRepository).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(etudiantRepository).deleteById(1L);

        etudiantService.deleteById(1L);

        verify(etudiantRepository).deleteById(1L);
    }

    @Test
    void testDelete() {
        Etudiant etudiant = Etudiant.builder().idEtudiant(1L).build();

        doNothing().when(etudiantRepository).delete(etudiant);

        etudiantService.delete(etudiant);

        verify(etudiantRepository).delete(etudiant);
    }

    @Test
    void testSelectJPQL() {
        Etudiant e = Etudiant.builder().nomEt("Smith").build();

        when(etudiantRepository.selectJPQL("Smith")).thenReturn(List.of(e));

        List<Etudiant> result = etudiantService.selectJPQL("Smith");

        assertThat(result).isNotEmpty();
        verify(etudiantRepository).selectJPQL("Smith");
    }

    @Test
    void testAffecterReservationAEtudiant() {
        String reservationId = "res1";
        String nom = "Ben";
        String prenom = "Ali";

        Reservation res = new Reservation();
        Etudiant et = Etudiant.builder().nomEt(nom).prenomEt(prenom).reservations(new ArrayList<>()).build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(res));
        when(etudiantRepository.getByNomEtAndPrenomEt(nom, prenom)).thenReturn(et);
        when(etudiantRepository.save(et)).thenReturn(et);

        etudiantService.affecterReservationAEtudiant(reservationId, nom, prenom);

        assertThat(et.getReservations()).contains(res);
        verify(reservationRepository).findById(reservationId);
        verify(etudiantRepository).getByNomEtAndPrenomEt(nom, prenom);
        verify(etudiantRepository).save(et);
    }

    @Test
    void testDesaffecterReservationAEtudiant() {
        String reservationId = "res1";
        String nom = "Ben";
        String prenom = "Ali";

        Reservation res = new Reservation();
        Etudiant et = Etudiant.builder()
                .nomEt(nom)
                .prenomEt(prenom)
                .reservations(new ArrayList<>())  // <--- Fix here
                .build();
        et.getReservations().add(res);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(res));
        when(etudiantRepository.getByNomEtAndPrenomEt(nom, prenom)).thenReturn(et);
        when(etudiantRepository.save(et)).thenReturn(et);

        etudiantService.desaffecterReservationAEtudiant(reservationId, nom, prenom);

        assertThat(et.getReservations()).doesNotContain(res);
        verify(reservationRepository).findById(reservationId);
        verify(etudiantRepository).getByNomEtAndPrenomEt(nom, prenom);
        verify(etudiantRepository).save(et);
    }
    @Test
    void testDesaffecterReservationAEtudiant_ReservationNotFound() {
        String reservationId = "nonexistent";
        String nom = "Ben";
        String prenom = "Ali";

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Execute
        etudiantService.desaffecterReservationAEtudiant(reservationId, nom, prenom);

        // Verify that nothing else happens
        verify(reservationRepository).findById(reservationId);
        verifyNoMoreInteractions(etudiantRepository); // No student should be fetched or saved
    }


}
