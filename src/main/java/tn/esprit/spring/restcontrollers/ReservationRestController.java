package tn.esprit.spring.restcontrollers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("reservation")
@AllArgsConstructor
public class ReservationRestController {
    ReservationService service;
    @PostMapping("addOrUpdate")
    Reservation addOrUpdate(@RequestBody Reservation r) {
        return service.addOrUpdate(r);
    }

    @GetMapping("findAll")
    List<Reservation> findAll() {
        return service.findAll();
    }

    // ......... ?id=1
    @GetMapping("findById")
    Reservation findById(@RequestParam String id) {
        return service.findById(id);
    }

    // ......../1
    @DeleteMapping("deleteById/{id}")
    void deleteById(@PathVariable String id) {
        service.deleteById(id);
    }




    @DeleteMapping("delete")
    void delete(@RequestBody Reservation r) {
        service.delete(r);
    }



    @PostMapping("ajouterReservationEtAssignerAChambreEtAEtudiant")
    Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(@RequestParam Long numChambre, @RequestParam long cin) {
        return service.ajouterReservationEtAssignerAChambreEtAEtudiant(numChambre, cin);
    }

    @GetMapping("getReservationParAnneeUniversitaire")
    long getReservationParAnneeUniversitaire(@RequestParam LocalDate debutAnnee, @RequestParam LocalDate finAnnee) {
        return service.getReservationParAnneeUniversitaire(debutAnnee, finAnnee);
    }

    @DeleteMapping("annulerReservation")
    String annulerReservation(@RequestParam long cinEtudiant) {
        return service.annulerReservation(cinEtudiant);
    }
}
