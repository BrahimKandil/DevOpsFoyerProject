package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Entities.Etudiant;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Optional, can remove if not used
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
        chambre = chambreRepository.save(
                Chambre.builder()
                        .numeroChambre(System.currentTimeMillis()) // unique number per test
                        .typeC(TypeChambre.SIMPLE)
                        .build());

        etudiant = etudiantRepository.save(
                Etudiant.builder()
                        .nomEt("TestNom")
                        .prenomEt("TestPrenom")
                        .cin(System.currentTimeMillis()) // unique CIN per test
                        .build());
    }

    @Test
    @Order(1)
    void testAddOrUpdateReservation() {
        Reservation res = Reservation.builder()
                .idReservation("R1")
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build();

        Reservation saved = reservationService.addOrUpdate(res);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdReservation()).isEqualTo("R1");
    }

    @Test
    @Order(2)
    void testFindAll() {
        List<Reservation> allReservations = reservationService.findAll();
        assertThat(allReservations).isNotNull();
        assertThat(allReservations.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(3)
    void testFindById() {
        Reservation res = Reservation.builder()
                .idReservation("R2")
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build();
        reservationRepository.save(res);

        Reservation found = reservationService.findById("R2");
        assertThat(found).isNotNull();
        assertThat(found.getIdReservation()).isEqualTo("R2");
    }

    @Test
    @Order(4)
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());
        assertThat(res).isNotNull();
        assertThat(res.getEtudiants()).extracting("cin").contains(etudiant.getCin());

        Chambre chambreFromDb = chambreRepository.findById(chambre.getIdChambre()).get();
        assertThat(chambreFromDb.getReservations()).contains(res);
    }

    @Test
    @Order(5)
    void testGetReservationParAnneeUniversitaire() {
        LocalDate start = LocalDate.of(2020, 9, 1);
        LocalDate end = LocalDate.of(2025, 6, 30);
        long count = reservationService.getReservationParAnneeUniversitaire(start, end);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    @Order(6)
    void testAnnulerReservation() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());

        String msg = reservationService.annulerReservation(etudiant.getCin());
        assertThat(msg).contains("annulée");

        Optional<Reservation> deletedRes = reservationRepository.findById(res.getIdReservation());
        assertThat(deletedRes).isEmpty();
    }

    @Test
    @Order(7)
    void testAffectAndDeaffectReservationAChambre() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());

        reservationService.affectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());
        Chambre updatedChambre = chambreRepository.findById(chambre.getIdChambre()).get();
        assertThat(updatedChambre.getReservations()).contains(res);

        reservationService.deaffectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());
        updatedChambre = chambreRepository.findById(chambre.getIdChambre()).get();
        assertThat(updatedChambre.getReservations()).doesNotContain(res);
    }
}
