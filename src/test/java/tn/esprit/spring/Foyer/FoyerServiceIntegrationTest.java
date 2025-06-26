package tn.esprit.spring.Foyer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Foyer.FoyerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class FoyerServiceIntegrationTest {

    @Autowired
    private FoyerService foyerService;

    @Autowired
    private FoyerRepository foyerRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private BlocRepository blocRepository;

    @Test
    void testAddOrUpdateAndFind() {
        Foyer foyer = Foyer.builder()
                .nomFoyer("Integration Foyer")
                .capaciteFoyer(200L)
                .build();

        Foyer saved = foyerService.addOrUpdate(foyer);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdFoyer()).isNotNull();

        Foyer found = foyerService.findById(saved.getIdFoyer());
        assertThat(found).isNotNull();
        assertThat(found.getNomFoyer()).isEqualTo("Integration Foyer");
    }

    @Test
    void testAjouterFoyerEtAffecterAUniversite() {
        // Create and save Universite
        Universite universite = new Universite();
        universite.setNomUniversite("Test Uni");
        universite = universiteRepository.save(universite);
        assertThat(universite.getIdUniversite()).isNotNull();

        // Create blocs
        Bloc bloc1 = new Bloc();
        bloc1.setNomBloc("Bloc A");
        bloc1.setCapaciteBloc(50);

        Bloc bloc2 = new Bloc();
        bloc2.setNomBloc("Bloc B");
        bloc2.setCapaciteBloc(60);

        // Create foyer with blocs
        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer With Blocs")
                .capaciteFoyer(110L)
                .build();
        foyer.setBlocs(List.of(bloc1, bloc2));

        // Save foyer and associate with universite via service
        Foyer savedFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, universite.getIdUniversite());
        assertThat(savedFoyer).isNotNull();
        assertThat(savedFoyer.getIdFoyer()).isNotNull();

        // Reload Universite to check association
        Universite updatedUni = universiteRepository.findById(universite.getIdUniversite()).orElseThrow();
        assertThat(updatedUni.getFoyer()).isNotNull();
        assertThat(updatedUni.getFoyer().getNomFoyer()).isEqualTo("Foyer With Blocs");

        // Check blocs are saved and linked
        List<Bloc> blocs = blocRepository.findAll();
        assertThat(blocs).hasSize(2);
        assertThat(blocs.get(0).getFoyer()).isEqualTo(savedFoyer);
        assertThat(blocs.get(1).getFoyer()).isEqualTo(savedFoyer);
    }

    @Test
    void testAffecterAndDesaffecterFoyerAUniversite() {
        // Save foyer and universite separately
        Foyer foyer = Foyer.builder()
                .nomFoyer("Test Foyer")
                .capaciteFoyer(100L)
                .build();
        foyer = foyerRepository.save(foyer);
        assertThat(foyer.getIdFoyer()).isNotNull();

        Universite universite = new Universite();
        universite.setNomUniversite("Test Uni");
        universite = universiteRepository.save(universite);
        assertThat(universite.getIdUniversite()).isNotNull();

        // Affect foyer to universite
        Universite affected = foyerService.affecterFoyerAUniversite(foyer.getIdFoyer(), universite.getNomUniversite());
        assertThat(affected).isNotNull();
        assertThat(affected.getFoyer()).isEqualTo(foyer);

        // Desaffect foyer
        Universite desaffected = foyerService.desaffecterFoyerAUniversite(universite.getIdUniversite());
        assertThat(desaffected).isNotNull();
        assertThat(desaffected.getFoyer()).isNull();
    }

    @Test
    void testAjoutFoyerEtBlocs() {
        Bloc bloc1 = new Bloc();
        bloc1.setNomBloc("Bloc 1");
        bloc1.setCapaciteBloc(25);

        Bloc bloc2 = new Bloc();
        bloc2.setNomBloc("Bloc 2");
        bloc2.setCapaciteBloc(30);

        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer Test")
                .capaciteFoyer(55L)
                .build();
        foyer.setBlocs(List.of(bloc1, bloc2));

        Foyer saved = foyerService.ajoutFoyerEtBlocs(foyer);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdFoyer()).isNotNull();

        List<Bloc> savedBlocs = blocRepository.findAll();
        assertThat(savedBlocs).hasSize(2);
        assertThat(savedBlocs.get(0).getFoyer()).isEqualTo(saved);
        assertThat(savedBlocs.get(1).getFoyer()).isEqualTo(saved);
    }
}
