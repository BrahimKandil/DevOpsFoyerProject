package tn.esprit.spring.Etudiant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Etudiant.IEtudiantService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Transactional
public class EtudiantServiceIntegrationTest {

    @Autowired
    private IEtudiantService etudiantService;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void testAddOrUpdateAndFindById() {
        Etudiant etudiant = Etudiant.builder()
                .nomEt("Integration")
                .prenomEt("Test")
                .cin(123456789L)
                .ecole("Test University")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();

        Etudiant saved = etudiantService.addOrUpdate(etudiant);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdEtudiant()).isNotNull();

        Etudiant found = etudiantService.findById(saved.getIdEtudiant());
        assertThat(found.getNomEt()).isEqualTo("Integration");
    }

    @Test
    public void testFindAll() {
        Etudiant e1 = Etudiant.builder()
                .nomEt("Alice")
                .prenomEt("Smith")
                .cin(11111111L)
                .ecole("Uni1")
                .dateNaissance(LocalDate.of(1995, 5, 10))
                .build();
        Etudiant e2 = Etudiant.builder()
                .nomEt("Bob")
                .prenomEt("Jones")
                .cin(22222222L)
                .ecole("Uni2")
                .dateNaissance(LocalDate.of(1996, 6, 15))
                .build();

        etudiantService.addOrUpdate(e1);
        etudiantService.addOrUpdate(e2);

        List<Etudiant> all = etudiantService.findAll();
        assertThat(all).isNotNull();
        assertThat(all.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testDeleteAndDeleteById() {
        Etudiant etudiant = Etudiant.builder()
                .nomEt("ToDelete")
                .prenomEt("User")
                .cin(33333333L)
                .ecole("Delete School")
                .dateNaissance(LocalDate.of(1990, 7, 20))
                .build();

        Etudiant saved = etudiantService.addOrUpdate(etudiant);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdEtudiant()).isNotNull();

        etudiantService.deleteById(saved.getIdEtudiant());
        assertThat(etudiantRepository.findById(saved.getIdEtudiant())).isEmpty();

        Etudiant et2 = Etudiant.builder()
                .nomEt("ToDelete2")
                .prenomEt("User2")
                .cin(44444444L)
                .ecole("Delete School 2")
                .dateNaissance(LocalDate.of(1991, 8, 21))
                .build();

        Etudiant saved2 = etudiantService.addOrUpdate(et2);
        assertThat(saved2).isNotNull();
        assertThat(saved2.getIdEtudiant()).isNotNull();

        etudiantService.delete(saved2);
        assertThat(etudiantRepository.findById(saved2.getIdEtudiant())).isEmpty();
    }

    @Test
    public void testSelectJPQL() {
        Etudiant etudiant = Etudiant.builder()
                .nomEt("JPQLName")
                .prenomEt("User")
                .cin(55555555L)
                .ecole("JPQL School")
                .dateNaissance(LocalDate.of(1992, 9, 22))
                .build();

        etudiantService.addOrUpdate(etudiant);

        List<Etudiant> list = etudiantService.selectJPQL("JPQLName");
        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getNomEt()).isEqualTo("JPQLName");
    }

    @Test
    public void testAffecterAndDesaffecterReservation() {
        Reservation res = new Reservation();
        res.setIdReservation(String.valueOf(1L)); // Manually assign ID to fix your error
        res = reservationRepository.save(res);
        assertThat(res).isNotNull();
        assertThat(res.getIdReservation()).isNotNull();

        Etudiant etudiant = Etudiant.builder()
                .nomEt("ResUser")
                .prenomEt("Test")
                .cin(66666666L)
                .ecole("Res School")
                .dateNaissance(LocalDate.of(1993, 10, 23))
                .build();

        etudiant = etudiantService.addOrUpdate(etudiant);
        assertThat(etudiant).isNotNull();
        assertThat(etudiant.getIdEtudiant()).isNotNull();

        // Affect reservation
        etudiantService.affecterReservationAEtudiant(res.getIdReservation(), etudiant.getNomEt(), etudiant.getPrenomEt());

        Etudiant updated = etudiantService.findById(etudiant.getIdEtudiant());
        assertThat(updated.getReservations()).contains(res);

        // Desaffect reservation
        etudiantService.desaffecterReservationAEtudiant(res.getIdReservation(), etudiant.getNomEt(), etudiant.getPrenomEt());

        updated = etudiantService.findById(etudiant.getIdEtudiant());
        assertThat(updated.getReservations()).doesNotContain(res);
    }
}


