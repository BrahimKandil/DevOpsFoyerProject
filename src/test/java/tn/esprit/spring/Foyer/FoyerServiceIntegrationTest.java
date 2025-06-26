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
        // Create and save Universite first (mandatory for foreign key)
        Universite universite = Universite.builder()
                .nomUniversite("Test Uni")
                .build();
        universite = universiteRepository.save(universite);
        assertThat(universite.getIdUniversite()).isNotNull();

        // Create blocs without IDs, no save yet
        Bloc bloc1 = Bloc.builder().nomBloc("Bloc A").capaciteBloc(50).build();
        Bloc bloc2 = Bloc.builder().nomBloc("Bloc B").capaciteBloc(60).build();

        // Create foyer and assign blocs
        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer With Blocs")
                .capaciteFoyer(110L)
                .build();
        foyer.setBlocs(List.of(bloc1, bloc2));

        // Call your service method that saves foyer + blocs + links foyer to universite
        Foyer savedFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, universite.getIdUniversite());
        assertThat(savedFoyer).isNotNull();
        assertThat(savedFoyer.getIdFoyer()).isNotNull();

        // Reload universite and verify foyer association
        Universite updatedUni = universiteRepository.findById(universite.getIdUniversite()).orElseThrow();
        assertThat(updatedUni.getFoyer()).isNotNull();
        assertThat(updatedUni.getFoyer().getNomFoyer()).isEqualTo("Foyer With Blocs");

        // Verify blocs saved & linked to foyer
        List<Bloc> blocs = blocRepository.findAll();
        assertThat(blocs).hasSize(2);
        assertThat(blocs).allMatch(b -> b.getFoyer() != null && b.getFoyer().equals(savedFoyer));
    }

    @Test
    void testAffecterAndDesaffecterFoyerAUniversite() {
        // Save foyer & universite separately before linking
        Foyer foyer = Foyer.builder()
                .nomFoyer("Test Foyer")
                .capaciteFoyer(100L)
                .build();
        foyer = foyerRepository.save(foyer);
        assertThat(foyer.getIdFoyer()).isNotNull();

        Universite universite = Universite.builder()
                .nomUniversite("Test Uni")
                .build();
        universite = universiteRepository.save(universite);
        assertThat(universite.getIdUniversite()).isNotNull();

        // Link foyer to universite
        Universite affected = foyerService.affecterFoyerAUniversite(foyer.getIdFoyer(), universite.getNomUniversite());
        assertThat(affected).isNotNull();
        assertThat(affected.getFoyer()).isNotNull();
        assertThat(affected.getFoyer()).isEqualTo(foyer);

        // Remove foyer from universite
        Universite desaffected = foyerService.desaffecterFoyerAUniversite(universite.getIdUniversite());
        assertThat(desaffected).isNotNull();
        assertThat(desaffected.getFoyer()).isNull();
    }

    @Test
    void testAjoutFoyerEtBlocs() {
        // Create blocs
        Bloc bloc1 = Bloc.builder().nomBloc("Bloc 1").capaciteBloc(25).build();
        Bloc bloc2 = Bloc.builder().nomBloc("Bloc 2").capaciteBloc(30).build();

        // Create foyer with blocs assigned
        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer Test")
                .capaciteFoyer(55L)
                .build();
        foyer.setBlocs(List.of(bloc1, bloc2));

        // Save foyer and blocs via your service method
        Foyer saved = foyerService.ajoutFoyerEtBlocs(foyer);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdFoyer()).isNotNull();

        // Check blocs are saved and linked to foyer
        List<Bloc> savedBlocs = blocRepository.findAll();
        assertThat(savedBlocs).hasSize(2);
        assertThat(savedBlocs).allMatch(b -> b.getFoyer() != null && b.getFoyer().equals(saved));
    }
}
