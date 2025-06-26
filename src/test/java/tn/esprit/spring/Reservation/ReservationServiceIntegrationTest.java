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

    @BeforeEach
    void setupData() {
        chambre = chambreRepository.save(Chambre.builder()
                .numeroChambre(System.currentTimeMillis())
                .typeC(TypeChambre.SIMPLE)
                .build());

        etudiant = etudiantRepository.save(Etudiant.builder()
                .nomEt("TestNom")
                .prenomEt("TestPrenom")
                .cin(System.currentTimeMillis())
                .build());

        assertThat(chambre.getIdChambre()).isNotNull();
        assertThat(etudiant.getCin()).isNotNull();
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
        Reservation res = reservationRepository.save(Reservation.builder()
                .idReservation("R2")
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        List<Reservation> allReservations = reservationService.findAll();
        assertThat(allReservations).isNotNull();
        assertThat(allReservations).extracting(Reservation::getIdReservation).contains("R2");
    }

    @Test
    @Order(3)
    void testFindById() {
        Reservation res = reservationRepository.save(Reservation.builder()
                .idReservation("R3")
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build());

        Reservation found = reservationService.findById("R3");
        assertThat(found).isNotNull();
        assertThat(found.getIdReservation()).isEqualTo("R3");
    }

    @Test
    @Order(4)
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());
        assertThat(res).isNotNull();
        assertThat(res.getEtudiants()).isNotEmpty();
        assertThat(res.getEtudiants()).extracting(Etudiant::getCin).contains(etudiant.getCin());

        Chambre chambreFromDb = chambreRepository.findById(chambre.getIdChambre()).orElse(null);
        assertThat(chambreFromDb).isNotNull();
        assertThat(chambreFromDb.getReservations()).contains(res);
    }

    @Test
    @Order(5)
    void testGetReservationParAnneeUniversitaire() {
        Reservation res = reservationRepository.save(Reservation.builder()
                .idReservation("R4")
                .anneeUniversitaire(LocalDate.of(2022, 9, 1))
                .estValide(true)
                .build());

        long count = reservationService.getReservationParAnneeUniversitaire(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2025, 12, 31)
        );
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(6)
    void testAnnulerReservation() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(
                chambre.getNumeroChambre(),
                etudiant.getCin()
        );
        assertThat(res).isNotNull();

        String msg = reservationService.annulerReservation(etudiant.getCin());
        assertThat(msg).containsIgnoringCase("annulée");

        Optional<Reservation> deletedRes = reservationRepository.findById(res.getIdReservation());
        assertThat(deletedRes).isEmpty(); // Must be deleted
    }

    @Test
    @Order(7)
    void testAffectAndDeaffectReservationAChambre() {
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(
                chambre.getNumeroChambre(),
                etudiant.getCin()
        );
        assertThat(res).isNotNull();

        reservationService.affectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());

        Chambre updatedChambre = chambreRepository.findById(chambre.getIdChambre()).orElse(null);
        assertThat(updatedChambre).isNotNull();
        assertThat(updatedChambre.getReservations()).contains(res);

        reservationService.deaffectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());

        updatedChambre = chambreRepository.findById(chambre.getIdChambre()).orElse(null);
        assertThat(updatedChambre).isNotNull();
        assertThat(updatedChambre.getReservations()).doesNotContain(res);
    }
}
