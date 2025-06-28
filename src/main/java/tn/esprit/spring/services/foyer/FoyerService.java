package tn.esprit.spring.services.foyer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.*;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;
import tn.esprit.spring.dao.repositories.UniversiteRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FoyerService implements IFoyerService {
    private final FoyerRepository foyerRepository;
    FoyerRepository repo;
    UniversiteRepository universiteRepository;
    BlocRepository blocRepository;

    @Override
    public Foyer addOrUpdate(Foyer f) {
        return repo.save(f);
    }

    @Override
    public List<Foyer> findAll() {
        return repo.findAll();
    }

    @Override
    public Foyer findById(long id) {
        Optional<Foyer> optionalFoyer = repo.findById(id);
        return optionalFoyer.orElse(null);
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Foyer f) {
        repo.delete(f);
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        Foyer f = findById(idFoyer); // Child
        Universite u = universiteRepository.findByNomUniversite(nomUniversite); // Parent
        // On affecte le child au parent
        u.setFoyer(f);
        return universiteRepository.save(u);
    }


    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        List<Bloc> blocs = foyer.getBlocs();
        Foyer f = repo.save(foyer);
        Optional<Universite> optionalUniversite = universiteRepository.findById(idUniversite);

        if(optionalUniversite.isPresent()){
            Universite u= optionalUniversite.get();

        for (Bloc bloc : blocs) {
            bloc.setFoyer(foyer);
            blocRepository.save(bloc);
        }
        u.setFoyer(f);
        return universiteRepository.save(u).getFoyer();
    }else {
            return null; // ou lancer une exception
        }
    }

    @Override
    public Foyer ajoutFoyerEtBlocs(Foyer foyer) {
        //-----------------------------------------
        List<Bloc> blocs = foyer.getBlocs();
        foyer = repo.save(foyer);
        for (Bloc b : blocs) {
            b.setFoyer(foyer);
            blocRepository.save(b);
        }
        return foyer;
    }

    @Override
    public Universite affecterFoyerAUniversite(long idF, long idU) {
        Optional<Universite> optionalUniversite = universiteRepository.findById(idU);
        Optional<Foyer> optionalFoyer = foyerRepository.findById(idF);
        if(optionalUniversite.isPresent() && optionalFoyer.isPresent()){
        Universite u= optionalUniversite.get();
        Foyer f= optionalFoyer.get();
        u.setFoyer(f);
        return universiteRepository.save(u);
    }else {
            return null;
        }
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        Optional<Universite> optionalUniversite = universiteRepository.findById(idUniversite);
        if(optionalUniversite.isEmpty()){
            return null; // ou lancer une exception
        }
        Universite u = optionalUniversite.get(); // Parent
        u.setFoyer(null);
        return universiteRepository.save(u);
    }


}
