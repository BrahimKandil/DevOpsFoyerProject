package tn.esprit.spring.services.etudiant;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EtudiantService implements IEtudiantService {
    EtudiantRepository repo;
    ReservationRepository reservationRepository;

    @Override
    public Etudiant addOrUpdate(Etudiant e) {
        return repo.save(e);
    }

    @Override
    public List<Etudiant> findAll() {
        return repo.findAll();
    }

    @Override
    public Etudiant findById(long id) {
        Optional<Etudiant> optionalEtudiant = repo.findById(id);
        return optionalEtudiant.orElse(null);
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Etudiant e) {
        repo.delete(e);
    }

    @Override
    public List<Etudiant> selectJPQL(String nom) {
        return repo.selectJPQL(nom);
    }

    @Override
    public void affecterReservationAEtudiant(String idR, String nomE, String prenomE) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(idR);
        Reservation res= optionalReservation.orElse(null);
        Etudiant et= repo.getByNomEtAndPrenomEt(nomE,prenomE);
        et.getReservations().add(res);
        repo.save(et);
    }
    @Override
    public void desaffecterReservationAEtudiant(String idR, String nomE, String prenomE) {

        Optional<Reservation> optionalReservation = reservationRepository.findById(idR);
        if(optionalReservation.isPresent()){
        Reservation res= optionalReservation.get();
        Etudiant et= repo.getByNomEtAndPrenomEt(nomE,prenomE);
        et.getReservations().remove(res);
        repo.save(et);
    }}
}
