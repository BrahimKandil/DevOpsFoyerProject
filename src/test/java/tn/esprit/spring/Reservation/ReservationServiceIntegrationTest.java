package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.*;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.IReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationServiceIntegrationTest {

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Chambre chambre;
    private Etudiant etudiant;

    @BeforeEach
    void setupData() {
        // Clear existing data
        reservationRepository.deleteAll();
        chambreRepository.deleteAll();
        etudiantRepository.deleteAll();

        // Setup test chambre
        chambre = chambreRepository.save(Chambre.builder()
                .numeroChambre(100L + (long)(Math.random() * 900L))
                .typeC(TypeChambre.SIMPLE)
                .reservations(new ArrayList<>())
                .build());

        // Setup test etudiant
        etudiant = etudiantRepository.save(Etudiant.builder()
                .nomEt("TestNom")
                .prenomEt("TestPrenom")
                .cin(10000000L + (long)(Math.random() * 90000000L))
                .reservations(new ArrayList<>())
                .build());

        assertThat(chambre.getIdChambre()).isNotNull();
        assertThat(etudiant.getCin()).isNotNull();
    }

    @Test
    @Order(1)
    void testAddOrUpdateReservation() {
        // Given
        Reservation res = Reservation.builder()
                .idReservation("R1-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .etudiants(new ArrayList<>())
                .build();

        // When
        Reservation saved = reservationService.addOrUpdate(res);

        // Then
        assertThat(saved)
                .isNotNull()
                .extracting(
                        Reservation::getIdReservation,
                        Reservation::isEstValide
                )
                .containsExactly(res.getIdReservation(), true);

        // Verify in repository
        assertTrue(reservationRepository.existsById(saved.getIdReservation()));
    }

    @Test
    @Order(2)
    void testFindAll() {
        // Given
        reservationRepository.save(Reservation.builder()
                .idReservation("R2-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        // When
        List<Reservation> allReservations = reservationService.findAll();

        // Then
        assertThat(allReservations)
                .isNotNull()
                .isNotEmpty()
                .allMatch(r -> r.getIdReservation() != null);
    }

    @Test
    @Order(3)
    void testFindById() {
        // Given
        Reservation saved = reservationRepository.save(Reservation.builder()
                .idReservation("R3-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        // When
        Reservation found = reservationService.findById(saved.getIdReservation());

        // Then
        assertThat(found)
                .isNotNull()
                .extracting(Reservation::getIdReservation)
                .isEqualTo(saved.getIdReservation());
    }

    @Test
    @Order(4)
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        // When
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(
                chambre.getNumeroChambre(),
                etudiant.getCin()
        );

        // Then
        assertThat(res)
                .isNotNull()
                .extracting(
                        r -> r.getEtudiants().isEmpty(),
                        r -> r.getAnneeUniversitaire() == null
                )
                .containsExactly(false, false);

        // Verify chambre association
        Chambre chambreFromDb = chambreRepository.findById(chambre.getIdChambre())
                .orElseThrow(() -> new AssertionError("Chambre not found"));
        assertThat(chambreFromDb.getReservations())
                .contains(res);

        // Verify etudiant association
        Etudiant etudiantFromDb = etudiantRepository.findById(etudiant.getCin())
                .orElseThrow(() -> new AssertionError("Etudiant not found"));
        assertThat(etudiantFromDb.getReservations())
                .contains(res);
    }

    @Test
    @Order(5)
    void testGetReservationParAnneeUniversitaire() {
        // Given
        LocalDate testDate = LocalDate.of(2023, 1, 15);
        reservationRepository.save(Reservation.builder()
                .idReservation("R4-" + System.currentTimeMillis())
                .anneeUniversitaire(testDate)
                .estValide(true)
                .build());

        // When
        long count = reservationService.getReservationParAnneeUniversitaire(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31)
        );

        // Then
        assertThat(count)
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(6)
    void testAnnulerReservation() {
        // Given
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(
                chambre.getNumeroChambre(),
                etudiant.getCin()
        );
        assertThat(res.isEstValide()).isTrue();

        // When
        String msg = reservationService.annulerReservation(etudiant.getCin());

        // Then
        assertThat(msg)
                .isNotNull()
                .containsIgnoringCase("annulée");

        // Verify status changed
        Reservation cancelledRes = reservationRepository.findById(res.getIdReservation())
                .orElseThrow(() -> new AssertionError("Reservation not found"));
        assertThat(cancelledRes.isEstValide()).isFalse();
    }

    @Test
    @Order(7)
    void testAffectAndDeaffectReservationAChambre() {
        // Given
        Reservation res = reservationRepository.save(Reservation.builder()
                .idReservation("R7-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        // When - Affect
        reservationService.affectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());

        // Then - Affect
        Chambre updatedChambre = chambreRepository.findById(chambre.getIdChambre())
                .orElseThrow(() -> new AssertionError("Chambre not found"));
        assertThat(updatedChambre.getReservations())
                .contains(res);

        // When - Deaffect
        reservationService.deaffectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());

        // Then - Deaffect
        updatedChambre = chambreRepository.findById(chambre.getIdChambre())
                .orElseThrow(() -> new AssertionError("Chambre not found"));
        assertThat(updatedChambre.getReservations())
                .doesNotContain(res);
    }
}