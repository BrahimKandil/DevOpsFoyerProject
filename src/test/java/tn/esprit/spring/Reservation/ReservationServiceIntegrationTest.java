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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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
    private Reservation testReservation;

    @BeforeEach
    void setupData() {
        // Clear existing data
        reservationRepository.deleteAll();
        chambreRepository.deleteAll();
        etudiantRepository.deleteAll();

        // Setup test chambre
        chambre = chambreRepository.save(Chambre.builder()
                .numeroChambre(100L + (long)(Math.random() * 900L)) // Random number between 100-999
                .typeC(TypeChambre.SIMPLE)
                .build());

        // Setup test etudiant
        etudiant = etudiantRepository.save(Etudiant.builder()
                .nomEt("TestNom")
                .prenomEt("TestPrenom")
                .cin(10000000L + (long)(Math.random() * 90000000L)) // Random 8-digit CIN
                .build());

        // Setup test reservation
        testReservation = reservationRepository.save(Reservation.builder()
                .idReservation("TEST-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        assertThat(chambre.getIdChambre()).isNotNull();
        assertThat(etudiant.getCin()).isNotNull();
        assertThat(testReservation.getIdReservation()).isNotNull();
    }

    @Test
    @Order(1)
    void testAddOrUpdateReservation() {
        Reservation res = Reservation.builder()
                .idReservation("R1-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build();

        Reservation saved = reservationService.addOrUpdate(res);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdReservation()).isEqualTo(res.getIdReservation());
    }

    @Test
    @Order(2)
    void testFindAll() {
        List<Reservation> allReservations = reservationService.findAll();
        assertThat(allReservations).isNotNull();
        assertThat(allReservations).isNotEmpty();
        assertThat(allReservations).anyMatch(r -> r.getIdReservation().equals(testReservation.getIdReservation()));
    }

    @Test
    @Order(3)
    void testFindById() {
        Reservation found = reservationService.findById(testReservation.getIdReservation());
        assertThat(found).isNotNull();
        assertThat(found.getIdReservation()).isEqualTo(testReservation.getIdReservation());
    }

    @Test
    @Order(4)
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(
                chambre.getNumeroChambre(),
                etudiant.getCin()
        );

        assertThat(res).isNotNull();
        assertThat(res.getEtudiants()).isNotEmpty();
        assertThat(res.getEtudiants()).extracting(Etudiant::getCin).contains(etudiant.getCin());

        Chambre chambreFromDb = chambreRepository.findById(chambre.getIdChambre()).orElse(null);
        assertThat(chambreFromDb).isNotNull();
        assertThat(chambreFromDb.getReservations()).contains(res);

        Etudiant etudiantFromDb = etudiantRepository.findById(etudiant.getCin()).orElse(null);
        assertThat(etudiantFromDb).isNotNull();
        assertThat(etudiantFromDb.getReservations()).contains(res);
    }

    @Test
    @Order(5)
    void testGetReservationParAnneeUniversitaire() {
        // Create a reservation with specific date
        LocalDate testDate = LocalDate.of(2023, 1, 15);
        reservationRepository.save(Reservation.builder()
                .idReservation("R4-" + System.currentTimeMillis())
                .anneeUniversitaire(testDate)
                .estValide(true)
                .build());

        long count = reservationService.getReservationParAnneeUniversitaire(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31)
        );
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(6)
    void testAnnulerReservation() {
        // First create a valid reservation
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(
                chambre.getNumeroChambre(),
                etudiant.getCin()
        );
        assertThat(res).isNotNull();
        assertThat(res.isEstValide()).isTrue();

        // Now cancel it
        String msg = reservationService.annulerReservation(etudiant.getCin());
        assertThat(msg).isNotNull();
        assertThat(msg).containsIgnoringCase("annulée");

        // Verify it's no longer valid
        Optional<Reservation> cancelledRes = reservationRepository.findById(res.getIdReservation());
        assertThat(cancelledRes).isPresent();
        assertThat(cancelledRes.get().isEstValide()).isFalse();
    }

    @Test
    @Order(7)
    void testAffectAndDeaffectReservationAChambre() {
        // Create a reservation without chambre assignment
        Reservation res = reservationRepository.save(Reservation.builder()
                .idReservation("R7-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        // Affect to chambre
        reservationService.affectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());

        Chambre updatedChambre = chambreRepository.findById(chambre.getIdChambre()).orElse(null);
        assertThat(updatedChambre).isNotNull();
        assertThat(updatedChambre.getReservations()).contains(res);

        // Deaffect from chambre
        reservationService.deaffectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());

        updatedChambre = chambreRepository.findById(chambre.getIdChambre()).orElse(null);
        assertThat(updatedChambre).isNotNull();
        assertThat(updatedChambre.getReservations()).doesNotContain(res);
    }
}