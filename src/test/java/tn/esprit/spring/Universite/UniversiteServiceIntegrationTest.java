package tn.esprit.spring.Universite;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Universite.UniversiteService;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UniversiteServiceIntegrationTest {

    @Autowired
    private UniversiteService universiteService;

    @Autowired
    private UniversiteRepository universiteRepository;

    private Universite universiteTemplate;

    @BeforeAll
    void init() {
        // Template entity; won't be saved directly
        universiteTemplate = Universite.builder()
                .nomUniversite("Université de Sousse")
                .adresse("Avenue Habib Bourguiba")
                .build();
    }

    @Test
    void testAddOrUpdateAndFindById() {
        Universite universite = cloneUniversite(universiteTemplate);

        Universite saved = universiteService.addOrUpdate(universite);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdUniversite()).isNotNull();

        Universite found = universiteService.findById(saved.getIdUniversite());
        assertThat(found).isNotNull();
        assertThat(found.getNomUniversite()).isEqualTo(universite.getNomUniversite());
    }

    @Test
    void testFindAll() {
        universiteService.addOrUpdate(cloneUniversite(universiteTemplate));

        List<Universite> list = universiteService.findAll();
        assertThat(list).isNotEmpty();
    }

    @Test
    void testDeleteAndDeleteById() {
        Universite u1 = cloneUniversite(universiteTemplate);
        Universite saved = universiteService.addOrUpdate(u1);

        universiteService.deleteById(saved.getIdUniversite());
        assertThat(universiteRepository.findById(saved.getIdUniversite())).isEmpty();

        Universite newUniversite = Universite.builder()
                .nomUniversite("Université de Carthage")
                .adresse("Carthage Street")
                .build();
        Universite saved2 = universiteService.addOrUpdate(newUniversite);
        universiteService.delete(saved2);
        assertThat(universiteRepository.findById(saved2.getIdUniversite())).isEmpty();
    }

    @Test
    void testAjouterUniversiteEtSonFoyer() {
        Universite u = Universite.builder()
                .nomUniversite("Université de Monastir")
                .adresse("Monastir Road")
                .build();

        Universite saved = universiteService.ajouterUniversiteEtSonFoyer(u);
        assertThat(saved).isNotNull();
        assertThat(saved.getIdUniversite()).isNotNull();
        assertThat(saved.getNomUniversite()).isEqualTo("Université de Monastir");
    }

    private Universite cloneUniversite(Universite source) {
        return Universite.builder()
                .nomUniversite(source.getNomUniversite())
                .adresse(source.getAdresse())
                .build();
    }
}
