package tn.esprit.spring.services.universite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.dao.repositories.UniversiteRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UniversiteService implements IUniversiteService {
    public final UniversiteRepository repo;
    public UniversiteService(UniversiteRepository repo) {
        this.repo = repo;
    }

    @Override
    public Universite addOrUpdate(Universite u) {
        return repo.save(u);
    }

    @Override
    public List<Universite> findAll() {
        return repo.findAll();
    }

    @Override
    public Universite findById(long id) {
        Optional<Universite> optionalUniversite = repo.findById(id);
        return optionalUniversite.orElse(null);
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Universite u) {
        repo.delete(u);
    }

    @Override
    public Universite ajouterUniversiteEtSonFoyer(Universite u) {
        return repo.save(u);
    }
}
