package tn.esprit.spring.services.reservation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService implements IReservationService {
    ReservationRepository repo;
    ChambreRepository chambreRepository;
    EtudiantRepository etudiantRepository;

    @Override
    public Reservation addOrUpdate(Reservation r) {
        return repo.save(r);
    }

    @Override
    public List<Reservation> findAll() {
        return repo.findAll();
    }

    @Override
    public Reservation findById(String id) {
        Optional<Reservation> optionalReservation = repo.findById(id);
        return optionalReservation.orElse(null);
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Reservation r) {
        repo.delete(r);
    }

    public LocalDate getDateDebutAU() {
        LocalDate dateDebutAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
        }
        return dateDebutAU;
    }

    public LocalDate getDateFinAU() {
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        return dateFinAU;
    }

    @Override
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant
            (Long numChambre, long cin) {
        // Récupération de la chambre et de l'étudiant
        Chambre chambre = chambreRepository.findByNumeroChambre(numChambre);
        Etudiant etudiant = etudiantRepository.findByCin(cin);

        // Compter le nombre de réservations existantes
        int nombreReservations = chambreRepository.
                countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween
                        (chambre.getIdChambre(), getDateDebutAU(), getDateFinAU());

        // Vérification de la capacité de la chambre
        boolean ajout = false;
        int capaciteMaximale = switch (chambre.getTypeC()) {
            case SIMPLE -> 1;
            case DOUBLE -> 2;
            case TRIPLE -> 3;
        };

        if (nombreReservations < capaciteMaximale) {
            ajout = true;
        } else {
            log.info("Chambre " + chambre.getTypeC() + " remplie !");
        }

        if (ajout) {
            // Création de la réservation
            String idReservation = getDateDebutAU().getYear() + "/" + getDateFinAU().getYear() + "-" + chambre.getBloc().getNomBloc() + "-"
                    + chambre.getNumeroChambre() + "-" + etudiant.getCin();

            Reservation reservation = Reservation.builder()
                    .estValide(true)
                    .anneeUniversitaire(LocalDate.now())
                    .idReservation(idReservation)
                    .build();

            // Affectation de l'étudiant à la réservation
            ArrayList<Etudiant> etudiants = new ArrayList<>();
            etudiants.add(etudiant);
            if(reservation.getEtudiants() == null) {
                reservation.setEtudiants(etudiants);
            }else {
                reservation.getEtudiants().addAll(etudiants);
            }

            // Sauvegarde de la réservation
            reservation = repo.save(reservation);

            // Affectation de la réservation à la chambre
            chambre.getReservations().add(reservation);
            chambreRepository.save(chambre);

            return reservation;
        }

        // Retourner null ou lever une exception plutôt que de retourner une nouvelle réservation vide
        return null; // Ou vous pouvez lever une exception pour indiquer que l'ajout a échoué
    }


    @Override
    public long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee) {
        return repo.countByAnneeUniversitaireBetween(debutAnnee, finAnnee);
    }

    @Override
    public String annulerReservation(long cinEtudiant) {
        Reservation r = repo.findByEtudiantsCinAndEstValide(cinEtudiant,
                true);
        Chambre c = chambreRepository.findByReservationsIdReservation
                (r.getIdReservation());
        c.getReservations().remove(r);
        chambreRepository.save(c);
        repo.delete(r);
        return "La réservation " + r.getIdReservation()
                + " est annulée avec succés";
    }

    @Override
    public void affectReservationAChambre(String idRes, long idChambre) {
        Optional<Reservation> optionalReservation = repo.findById(idRes);
        Optional<Chambre> optionalChambre = chambreRepository.findById(idChambre);
        if (optionalReservation.isPresent() && optionalChambre.isPresent()) {
            Reservation r = optionalReservation.get();
            Chambre c = optionalChambre.get();
             c.getReservations().add(r);
            chambreRepository.save(c);
        }
    }

    @Override
    public void deaffectReservationAChambre(String idRes, long idChambre) {
        Optional<Reservation> optionalReservation = repo.findById(idRes);
        Optional<Chambre> optionalChambre = chambreRepository.findById(idChambre);
        if(optionalReservation.isPresent() && optionalChambre.isPresent()){
            Reservation r = optionalReservation.get();
            Chambre c = optionalChambre.get();
            c.getReservations().remove(r);
            chambreRepository.save(c);

        }
    }
    @Override
    public void annulerReservations() {
        int year = LocalDate.now().getYear() % 100;
        LocalDate dateDebutAU = (LocalDate.now().getMonthValue() <= 7) ? LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15) : LocalDate.of(Integer.parseInt("20" + year), 9, 15);
        LocalDate dateFinAU = (LocalDate.now().getMonthValue() <= 7) ?  LocalDate.of(Integer.parseInt("20" + year), 6, 30) : LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        // Fin "récuperer l'année universitaire actuelle"
        for (Reservation reservation : repo.findByEstValideAndAnneeUniversitaireBetween
                (true, dateDebutAU, dateFinAU)) {
            reservation.setEstValide(false);
            repo.save(reservation);
            log.info("La reservation " + reservation.getIdReservation() + " est annulée automatiquement");
        }
    }

}
