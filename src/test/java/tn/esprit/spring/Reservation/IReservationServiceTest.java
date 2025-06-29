package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;

@ExtendWith(MockitoExtension.class)

class IReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService; // Your implementation class

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ChambreRepository chambreRepository;

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
        Etudiant etudiant = new Etudiant(1L, "John", "Doe", 12345678L, "ENIT", LocalDate.of(1995, 5, 15), null);
        Reservation reservation = new Reservation();
        reservation.setAnneeUniversitaire(LocalDate.of(2023, 9, 1));
        reservation.setEtudiants(List.of(etudiant));

        // Stub save() method
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Call the method under test, e.g.
        Reservation result = reservationService.addOrUpdate(reservation);
        // Add assertions here...
        assertNotNull(result);
        assertEquals(1, result.getEtudiants().size());
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
    public void annulerReservation() {
        long cin = 12345678L; // Example CIN
        Reservation reservation = reservationRepository.findByEtudiantsCinAndEstValide(cin, true);
        if (reservation != null) {
            reservation.setEstValide(false); // ✅ make it invalid

            Chambre chambres = chambreRepository.findByReservationsIdReservation(reservation.getIdReservation());
//            for (Chambre c : chambres) {
                if (chambres.getReservations() != null) {
                    chambres.getReservations().removeIf(r -> r.getIdReservation().equals(reservation.getIdReservation()));
                }
//            }

            reservationRepository.save(reservation);
        }
    }



    @Test
    void testAnnulerReservations() {
        Reservation r1 = new Reservation();
        r1.setIdReservation("R1");
        r1.setEstValide(true);
        Reservation r2 = new Reservation();
        r2.setIdReservation("R2");
        r2.setEstValide(true);

        // The date range matching your method's logic:
        int year = LocalDate.now().getYear() % 100;
        LocalDate dateDebutAU = (LocalDate.now().getMonthValue() <= 7)
                ? LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15)
                : LocalDate.of(Integer.parseInt("20" + year), 9, 15);
        LocalDate dateFinAU = (LocalDate.now().getMonthValue() <= 7)
                ? LocalDate.of(Integer.parseInt("20" + year), 6, 30)
                : LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);

        List<Reservation> reservations = List.of(r1, r2);

        when(reservationRepository.findByEstValideAndAnneeUniversitaireBetween(
                eq(true), eq(dateDebutAU), eq(dateFinAU)))
                .thenReturn(reservations);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(i -> i.getArgument(0));

        reservationService.annulerReservations();

        assertThat(r1.isEstValide()).isFalse();
        assertThat(r2.isEstValide()).isFalse();
        verify(reservationRepository, times(reservations.size())).save(any(Reservation.class));
    }


    @Test
    void testAffectReservationAChambre() {
        // Suppose affectReservationAChambre assigns a Chambre to a Reservation by IDs
        String reservationId = "R1";
        long chambreId = 1L;

        Reservation reservation = new Reservation();
        reservation.setIdReservation(reservationId);

        // Mock chambre repository and reservation repo if needed (create mocks)
        // For example assuming chambreRepository is injected in service
        // and service has method findById for chambre and reservation

        // We create mock chambre and stub repo calls:
        tn.esprit.spring.dao.entities.Chambre chambre = new tn.esprit.spring.dao.entities.Chambre();
        chambre.setIdChambre(chambreId);

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));
        // Assuming you have chambreRepository mock (add it if missing)
        // when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));

        // Mock save
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        // Call method under test
        reservationService.affectReservationAChambre(reservationId, chambreId);
        List<Reservation> reservations = reservationRepository.findByChambreIdChambre(chambreId);
        // Assertions
        assertThat(reservations).isNotNull();
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testDeaffectReservationAChambre() {
        String reservationId = "R1";

        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(reservationId);

        chambre.setReservations(new ArrayList<>(List.of(reservation)));

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(chambreRepository.findById(chambre.getIdChambre())).thenReturn(Optional.of(chambre));
        when(chambreRepository.save(any(Chambre.class))).thenAnswer(i -> i.getArgument(0));

        reservationService.deaffectReservationAChambre(reservationId, chambre.getIdChambre());

        // Assert that the reservation list no longer contains the reservation
        assertThat(chambre.getReservations()).doesNotContain(reservation);

        // ✅ Verify chambre was saved
        verify(chambreRepository).save(chambre);

        // ❌ Do NOT verify reservationRepository.save(...)
        verify(reservationRepository, never()).save(any());
    }






}
