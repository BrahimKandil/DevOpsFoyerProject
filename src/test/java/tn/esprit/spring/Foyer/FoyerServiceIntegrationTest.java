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
import tn.esprit.spring.Services.Foyer.IFoyerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class FoyerServiceIntegrationTest {

    @Autowired
    private IFoyerService foyerService;

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
        assertThat(saved.getIdFoyer()).isNotNull();

        Foyer found = foyerService.findById(saved.getIdFoyer());
        assertThat(found.getNomFoyer()).isEqualTo("Integration Foyer");
    }

    @Test
    void testAjouterFoyerEtAffecterAUniversite() {
        Universite universite = new Universite();
        universite.setNomUniversite("Test Uni");
        universite = universiteRepository.save(universite);

        Bloc bloc1 = new Bloc();
        bloc1.setNomBloc("Bloc A");
        bloc1.setCapaciteBloc(50);

        Bloc bloc2 = new Bloc();
        bloc2.setNomBloc("Bloc B");
        bloc2.setCapaciteBloc(60);

        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer With Blocs")
                .capaciteFoyer(110)
                .build();

        foyer.setBlocs(List.of(bloc1, bloc2));

        Foyer savedFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, universite.getIdUniversite());

        // Verify foyer saved and linked to universite
        assertThat(savedFoyer.getIdFoyer()).isNotNull();

        Universite updatedUni = universiteRepository.findById(universite.getIdUniversite()).orElseThrow();
        assertThat(updatedUni.getFoyer()).isNotNull();
        assertThat(updatedUni.getFoyer().getNomFoyer()).isEqualTo("Foyer With Blocs");

        // Verify blocs are saved and linked to foyer
        List<Bloc> blocs = blocRepository.findAll();
        assertThat(blocs).hasSize(2);
        assertThat(blocs.get(0).getFoyer()).isEqualTo(savedFoyer);
        assertThat(blocs.get(1).getFoyer()).isEqualTo(savedFoyer);
    }

    @Test
    void testAffecterAndDesaffecterFoyerAUniversite() {
        Foyer foyer = Foyer.builder()
                .nomFoyer("Test Foyer")
                .capaciteFoyer(100L)
                .build();
        foyer = foyerRepository.save(foyer);

        Universite universite = new Universite();
        universite.setNomUniversite("Test Uni");
        universite = universiteRepository.save(universite);

        Universite affected = foyerService.affecterFoyerAUniversite(foyer.getIdFoyer(), universite.getNomUniversite());
        assertThat(affected.getFoyer()).isEqualTo(foyer);

        Universite desaffected = foyerService.desaffecterFoyerAUniversite(universite.getIdUniversite());
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
                .capaciteFoyer(55)
                .build();

        foyer.setBlocs(List.of(bloc1, bloc2));

        Foyer saved = foyerService.ajoutFoyerEtBlocs(foyer);

        assertThat(saved.getIdFoyer()).isNotNull();

        List<Bloc> savedBlocs = blocRepository.findAll();
        assertThat(savedBlocs).hasSize(2);
        assertThat(savedBlocs.get(0).getFoyer()).isEqualTo(saved);
        assertThat(savedBlocs.get(1).getFoyer()).isEqualTo(saved);
    }
}
