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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationServiceIntegrationTest {

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private static Chambre chambre;
    private static Etudiant etudiant;

    @BeforeAll
    static void setupData(@Autowired ChambreRepository chambreRepo,
                          @Autowired EtudiantRepository etudiantRepo) {
        // Create and save a Chambre
        Chambre newChambre = Chambre.builder()
                .numeroChambre(101L)
                .typeC(TypeChambre.SIMPLE)
                .build();
        chambre = chambreRepo.save(newChambre);

        // Create and save an Etudiant
        Etudiant newEtudiant = Etudiant.builder()
                .nomEt("TestNom")
                .prenomEt("TestPrenom")
                .cin(12345678L)
                .build();
        etudiant = etudiantRepo.save(newEtudiant);
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
        assertThat(allReservations.size()).isGreaterThanOrEqualTo(0);
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
        assertThat(chambreRepository.findById(chambre.getIdChambre()).get().getReservations()).contains(res);
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
        // Prepare reservation for etudiant
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());

        String msg = reservationService.annulerReservation(etudiant.getCin());
        assertThat(msg).contains("annulée");

        // Reservation should no longer exist
        Optional<Reservation> deletedRes = reservationRepository.findById(res.getIdReservation());
        assertThat(deletedRes).isEmpty();
    }

    @Test
    @Order(7)
    void testAffectAndDeaffectReservationAChambre() {
        // Prepare reservation
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());

        // Test affect
        reservationService.affectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());
        Chambre updatedChambre = chambreRepository.findById(chambre.getIdChambre()).get();
        assertThat(updatedChambre.getReservations()).contains(res);

        // Test deaffect
        reservationService.deaffectReservationAChambre(res.getIdReservation(), chambre.getIdChambre());
        updatedChambre = chambreRepository.findById(chambre.getIdChambre()).get();
        assertThat(updatedChambre.getReservations()).doesNotContain(res);
    }
}
