package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.Dao.Entities.Reservation;
import tn.esprit.spring.Dao.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class IReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService; // Your implementation class

    @Mock
    private ReservationRepository reservationRepository;

    // Add other necessary mocks like ChambreRepository, EtudiantRepository, etc.

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");
        when(reservationRepository.save(r)).thenReturn(r);

        Reservation result = reservationService.addOrUpdate(r);

        assertThat(result).isEqualTo(r);
        verify(reservationRepository).save(r);
    }

    @Test
    void testFindAll() {
        Reservation r1 = new Reservation();
        r1.setIdReservation("R1");
        Reservation r2 = new Reservation();
        r2.setIdReservation("R2");
        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Reservation> result = reservationService.findAll();

        assertThat(result).hasSize(2);
        verify(reservationRepository).findAll();
    }

    @Test
    void testFindById() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");
        when(reservationRepository.findById("R1")).thenReturn(java.util.Optional.of(r));

        Reservation result = reservationService.findById("R1");

        assertThat(result).isEqualTo(r);
        verify(reservationRepository).findById("R1");
    }

    @Test
    void testDeleteById() {
        doNothing().when(reservationRepository).deleteById("R1");

        reservationService.deleteById("R1");

        verify(reservationRepository).deleteById("R1");
    }

    @Test
    void testDelete() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");
        doNothing().when(reservationRepository).delete(r);

        reservationService.delete(r);

        verify(reservationRepository).delete(r);
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        // TODO: Mock Chambre and Etudiant repositories and write test for this logic
        // For example, simulate creating reservation, linking chambre and etudiant, then saving
    }

    @Test
    void testGetReservationParAnneeUniversitaire() {
        LocalDate start = LocalDate.of(2023, 9, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);
        when(reservationRepository.countByAnneeUniversitaireBetween(start, end)).thenReturn(5);

        long count = reservationService.getReservationParAnneeUniversitaire(start, end);

        assertThat(count).isEqualTo(5L);
        verify(reservationRepository).countByAnneeUniversitaireBetween(start, end);
    }

    @Test
    void testAnnulerReservation() {
        // TODO: Write logic for annulerReservation
        // Mock find reservation by student cin, mark reservation invalid, save
    }

    @Test
    void testAnnulerReservations() {
        // TODO: Write logic for annulerReservations
        // Mock find all reservations, mark estValide false, saveAll
    }

    @Test
    void testAffectReservationAChambre() {
        // TODO: Mock chambre and reservation find, assign chambre to reservation, save
    }

    @Test
    void testDeaffectReservationAChambre() {
        // TODO: Mock chambre and reservation find, remove chambre from reservation, save
    }
}
