package tn.esprit.spring.Etudiant;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Etudiant.EtudiantService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EtudiantServiceIntegrationTest {

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        // Clear data before each test
        reservationRepository.deleteAll();
        etudiantRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAddOrUpdateAndFindById() {
        // Given
        Etudiant etudiant = Etudiant.builder()
                .nomEt("Integration")
                .prenomEt("Test")
                .cin(123456789L)
                .ecole("Test University")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .reservations(new ArrayList<>())
                .build();

        // When
        Etudiant saved = etudiantService.addOrUpdate(etudiant);

        // Then
        assertThat(saved)
                .isNotNull()
                .extracting(
                        Etudiant::getIdEtudiant,
                        Etudiant::getNomEt
                )
                .doesNotContainNull()
                .containsExactly(saved.getIdEtudiant(), "Integration");

        // Verify find
        Etudiant found = etudiantService.findById(saved.getIdEtudiant());
        assertThat(found)
                .isNotNull()
                .extracting(Etudiant::getNomEt)
                .isEqualTo("Integration");
    }

    @Test
    @Order(2)
    void testFindAll() {
        // Given
        etudiantService.addOrUpdate(Etudiant.builder()
                .nomEt("Alice")
                .prenomEt("Smith")
                .cin(11111111L)
                .ecole("Uni1")
                .dateNaissance(LocalDate.of(1995, 5, 10))
                .build());

        etudiantService.addOrUpdate(Etudiant.builder()
                .nomEt("Bob")
                .prenomEt("Jones")
                .cin(22222222L)
                .ecole("Uni2")
                .dateNaissance(LocalDate.of(1996, 6, 15))
                .build());

        // When
        List<Etudiant> all = etudiantService.findAll();

        // Then
        assertThat(all)
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting(Etudiant::getNomEt)
                .contains("Alice", "Bob");
    }

    @Test
    @Order(3)
    void testDeleteAndDeleteById() {
        // Given
        Etudiant saved = etudiantService.addOrUpdate(Etudiant.builder()
                .nomEt("ToDelete")
                .prenomEt("User")
                .cin(33333333L)
                .ecole("Delete School")
                .dateNaissance(LocalDate.of(1990, 7, 20))
                .build());

        Long id = saved.getIdEtudiant();

        // When - delete by ID
        etudiantService.deleteById(id);

        // Then
        assertThat(etudiantRepository.findById(id)).isEmpty();

        // Given - new entity
        Etudiant saved2 = etudiantService.addOrUpdate(Etudiant.builder()
                .nomEt("ToDelete2")
                .prenomEt("User2")
                .cin(44444444L)
                .ecole("Delete School 2")
                .dateNaissance(LocalDate.of(1991, 8, 21))
                .build());

        // When - delete by entity
        etudiantService.delete(saved2);

        // Then
        assertThat(etudiantRepository.findById(saved2.getIdEtudiant())).isEmpty();
    }

    @Test
    @Order(4)
    void testSelectJPQL() {
        // Given
        etudiantService.addOrUpdate(Etudiant.builder()
                .nomEt("JPQLName")
                .prenomEt("User")
                .cin(55555555L)
                .ecole("JPQL School")
                .dateNaissance(LocalDate.of(1992, 9, 22))
                .build());

        // When
        List<Etudiant> list = etudiantService.selectJPQL("JPQLName");

        // Then
        assertThat(list)
                .isNotNull()
                .isNotEmpty()
                .extracting(Etudiant::getNomEt)
                .containsExactly("JPQLName");
    }

    @Test
    @Order(5)
    void testAffecterAndDesaffecterReservation() {
        // Given
        Reservation res = Reservation.builder()
                .idReservation("RES-" + System.currentTimeMillis())
                .anneeUniversitaire(LocalDate.now())
                .estValide(true)
                .build();
        res = reservationRepository.save(res);
        assertThat(res).isNotNull();

        Etudiant etudiant = etudiantService.addOrUpdate(Etudiant.builder()
                .nomEt("ResUser")
                .prenomEt("Test")
                .cin(66666666L)
                .ecole("Res School")
                .dateNaissance(LocalDate.of(1993, 10, 23))
                .reservations(new ArrayList<>())
                .build());
        assertThat(etudiant).isNotNull();

        // When - affect
        etudiantService.affecterReservationAEtudiant(res.getIdReservation(),
                etudiant.getNomEt(), etudiant.getPrenomEt());

        // Then
        Etudiant updated = etudiantService.findById(etudiant.getIdEtudiant());
        assertThat(updated.getReservations())
                .isNotEmpty()
                .extracting(Reservation::getIdReservation)
                .contains(res.getIdReservation());

        // When - desaffect
        etudiantService.desaffecterReservationAEtudiant(res.getIdReservation(),
                etudiant.getNomEt(), etudiant.getPrenomEt());

        // Then
        updated = etudiantService.findById(etudiant.getIdEtudiant());
        assertThat(updated.getReservations())
                .doesNotContain(res);
    }
}