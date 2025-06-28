package tn.esprit.spring.Services.Chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    private final ChambreRepository chambreRepository;
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        Optional<Chambre> optionalChambre = repo.findById(id);
        return optionalChambre.orElse(null);
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        long compteur = 0;
        List<Chambre> list = chambreRepository.findAll();
        for (Chambre chambre : list) {
            if (chambre.getBloc().getIdBloc() == idBloc
                    && chambre.getTypeC().equals(type)) {
                compteur++;
            }
        }
        return compteur;
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {

        int numReservation;
        int year = LocalDate.now().getYear() % 100;

        LocalDate dateDebutAU = (LocalDate.now().getMonthValue() <= 7) ? LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15) : LocalDate.of(Integer.parseInt("20" + year), 9, 15);
        LocalDate dateFinAU = (LocalDate.now().getMonthValue() <= 7) ? LocalDate.of(Integer.parseInt("20" + year), 6, 30) : LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        List<Chambre> listChambreDispo = new ArrayList<>();
        for (Chambre c : repo.findAll()) {
            if (c.getTypeC().equals(type) && c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer)) { // Les chambres du foyer X et qui ont le type Y
                numReservation = 0;
                for (Reservation reservation : c.getReservations()) {
                    if (reservation.getAnneeUniversitaire().isBefore(dateFinAU) && reservation.getAnneeUniversitaire().isAfter(dateDebutAU)) {
                        numReservation++;
                    }
                }
                if (c.getTypeC().equals(TypeChambre.SIMPLE) && numReservation == 0) {
                    listChambreDispo.add(c);
                } else if (c.getTypeC().equals(TypeChambre.DOUBLE) && numReservation < 2) {
                    listChambreDispo.add(c);
                } else if (c.getTypeC().equals(TypeChambre.TRIPLE) && numReservation < 3) {
                    listChambreDispo.add(c);
                }
            }
        }
        return listChambreDispo;
    }

    @Scheduled(fixedRate = 300000)
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() +
                    " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() +
                            " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (double) (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        double pDouble = (double) (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = (double) (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;
        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);

    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        for (Chambre c : repo.findAll()) {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(c.getIdChambre()
                    , true, dateDebutAU, dateFinAU);
            int capacity;
            switch (c.getTypeC()) {
                case SIMPLE -> capacity = 1;
                case DOUBLE -> capacity = 2;
                case TRIPLE -> capacity = 3;
                default -> capacity = 0;
            }

            if (nbReservation < capacity) {
                log.info("Le nombre de place disponible pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (capacity - nbReservation));
            } else {
                log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complete");
            }
        }
    }

    @Override
    public List<Chambre> getChambresParNomBlocJava(String nomBloc) {
        Bloc b = blocRepository.findByNomBloc(nomBloc);
        return b.getChambres();
    }

    @Override
    public List<Chambre> getChambresParNomBlocKeyWord(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocJPQL(String nomBloc) {
        return repo.getChambresParNomBlocJPQL(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocSQL(String nomBloc) {
        return repo.getChambresParNomBlocSQL(nomBloc);
    }
}
