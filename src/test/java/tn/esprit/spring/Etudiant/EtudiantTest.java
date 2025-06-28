package tn.esprit.spring.Etudiant;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EtudiantTest {

    @Test
    void testNoArgsConstructor() {
        Etudiant etudiant = new Etudiant();
        assertThat(etudiant).isNotNull();
    }

    @Test
     void testAllArgsConstructor() {
        LocalDate birthDate = LocalDate.of(2000, 1, 15);
        Reservation res1 = new Reservation();
        Reservation res2 = new Reservation();

        Etudiant etudiant = new Etudiant(
                1L,
                "Ben Ali",
                "Ahmed",
                12345678L,
                "ENIT",
                birthDate,
                List.of(res1, res2)
        );

        assertThat(etudiant.getIdEtudiant()).isEqualTo(1L);
        assertThat(etudiant.getNomEt()).isEqualTo("Ben Ali");
        assertThat(etudiant.getPrenomEt()).isEqualTo("Ahmed");
        assertThat(etudiant.getCin()).isEqualTo(12345678L);
        assertThat(etudiant.getEcole()).isEqualTo("ENIT");
        assertThat(etudiant.getDateNaissance()).isEqualTo(birthDate);
        assertThat(etudiant.getReservations()).containsExactly(res1, res2);
    }

    @Test
     void testBuilder() {
        LocalDate birthDate = LocalDate.of(1999, 5, 20);

        Etudiant etudiant = Etudiant.builder()
                .idEtudiant(2L)
                .nomEt("Kacem")
                .prenomEt("Imen")
                .cin(87654321L)
                .ecole("INSAT")
                .dateNaissance(birthDate)
                .reservations(List.of())
                .build();

        assertThat(etudiant.getIdEtudiant()).isEqualTo(2L);
        assertThat(etudiant.getNomEt()).isEqualTo("Kacem");
        assertThat(etudiant.getPrenomEt()).isEqualTo("Imen");
        assertThat(etudiant.getCin()).isEqualTo(87654321L);
        assertThat(etudiant.getEcole()).isEqualTo("INSAT");
        assertThat(etudiant.getDateNaissance()).isEqualTo(birthDate);
        assertThat(etudiant.getReservations()).isEmpty();
    }

    @Test
     void testSettersAndGetters() {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(3L);
        etudiant.setNomEt("Trabelsi");
        etudiant.setPrenomEt("Yassine");
        etudiant.setCin(44556677L);
        etudiant.setEcole("ISI");
        LocalDate date = LocalDate.of(2001, 10, 10);
        etudiant.setDateNaissance(date);

        assertThat(etudiant.getIdEtudiant()).isEqualTo(3L);
        assertThat(etudiant.getNomEt()).isEqualTo("Trabelsi");
        assertThat(etudiant.getPrenomEt()).isEqualTo("Yassine");
        assertThat(etudiant.getCin()).isEqualTo(44556677L);
        assertThat(etudiant.getEcole()).isEqualTo("ISI");
        assertThat(etudiant.getDateNaissance()).isEqualTo(date);
    }

    @Test
     void testDefaultReservationsInitialization() {
        Etudiant etudiant = new Etudiant();
        assertThat(etudiant.getReservations()).isNotNull();
        assertThat(etudiant.getReservations()).isEmpty();
    }
}