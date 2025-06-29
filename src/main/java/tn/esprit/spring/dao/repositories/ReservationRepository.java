package tn.esprit.spring.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.dao.entities.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    int countByAnneeUniversitaireBetween(LocalDate dateInf, LocalDate dateSup);
    Reservation findByEtudiantsCinAndEstValide(long cin,boolean isValid);
    List<Reservation> findByEstValideAndAnneeUniversitaireBetween(boolean estValide, LocalDate dateDebut, LocalDate dateFin);
    @Query(value = "SELECT c FROM Chambre c  WHERE c.idChambre = :idchambre")
    List<Reservation> findByChambreIdChambre(long idchambre);

}
