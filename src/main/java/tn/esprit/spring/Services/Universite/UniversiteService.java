package tn.esprit.spring.Services.Universite;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;

import java.util.List;

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
        return repo.findById(id).get();
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
