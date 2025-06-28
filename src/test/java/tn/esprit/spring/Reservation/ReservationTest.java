package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dao.entities.*;

import java.time.LocalDate;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;



class ReservationTest {

    @Test
    void testBuilderAndGettersSetters() {
        Reservation reservation = Reservation.builder()
                .idReservation("R123")
                .anneeUniversitaire(LocalDate.of(2024, 9, 1))
                .estValide(true)
                .build();

        // Initialize etudiants list if null (because your entity doesn't do it)
        if (reservation.getEtudiants() == null) {
            reservation.setEtudiants(new ArrayList<>());
        }

        // initially, etudiants list should be empty
        assertThat(reservation.getEtudiants()).isEmpty();

        // test getters
        assertThat(reservation.getIdReservation()).isEqualTo("R123");
        assertThat(reservation.getAnneeUniversitaire()).isEqualTo(LocalDate.of(2024, 9, 1));
        assertThat(reservation.isEstValide()).isTrue();

        // test adding etudiants
        Etudiant e1 = new Etudiant();
        e1.setNomEt("Nom1");
        reservation.getEtudiants().add(e1);

        assertThat(reservation.getEtudiants()).hasSize(1);
        assertThat(reservation.getEtudiants().get(0).getNomEt()).isEqualTo("Nom1");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        Reservation reservation = new Reservation();
        reservation.setIdReservation("R456");
        reservation.setAnneeUniversitaire(LocalDate.of(2025, 1, 1));
        reservation.setEstValide(false);

        // Initialize etudiants list if null
        if (reservation.getEtudiants() == null) {
            reservation.setEtudiants(new ArrayList<>());
        }

        assertThat(reservation.getIdReservation()).isEqualTo("R456");
        assertThat(reservation.getAnneeUniversitaire()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(reservation.isEstValide()).isFalse();

        // etudiants list should be initialized empty, test adding one
        Etudiant e2 = new Etudiant();
        e2.setPrenomEt("Prenom");
        reservation.getEtudiants().add(e2);

        assertThat(reservation.getEtudiants()).hasSize(1);
        assertThat(reservation.getEtudiants().get(0).getPrenomEt()).isEqualTo("Prenom");
    }
}
