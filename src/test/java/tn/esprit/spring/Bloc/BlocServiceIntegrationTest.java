package tn.esprit.spring.Bloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BlocServiceIntegrationTest {

    @Autowired
    private BlocService blocService;

    @Autowired
    private BlocRepository blocRepository;

    @Autowired
    private FoyerRepository foyerRepository;

    @Test
    void testAddOrUpdate() {
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Alpha")
                .capaciteBloc(100)
                .build();

        Bloc result = blocService.addOrUpdate(bloc);

        assertNotNull(result, "Bloc returned by service should not be null");
        assertNotNull(result.getIdBloc(), "Bloc ID should not be null after save");
        assertEquals("Bloc Alpha", result.getNomBloc());
    }

    @Test
    void testFindAll() {
        // Ensure repository has some blocs saved
        blocRepository.save(Bloc.builder().nomBloc("Bloc1").capaciteBloc(50).build());
        blocRepository.save(Bloc.builder().nomBloc("Bloc2").capaciteBloc(75).build());

        List<Bloc> blocs = blocService.findAll();

        assertThat(blocs).isNotNull();
        assertThat(blocs).isNotEmpty();
        assertThat(blocs).extracting(Bloc::getNomBloc).contains("Bloc1", "Bloc2");
    }

    @Test
    void testAddOrUpdateWithChambres() {
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Beta")
                .capaciteBloc(200)
                .build();

        Chambre chambre = new Chambre();
        chambre.setNumeroChambre(101L);
        chambre.setBloc(bloc); // set back-reference

        bloc.setChambres(List.of(chambre));

        Bloc saved = blocService.addOrUpdate(bloc);

        assertNotNull(saved, "Saved bloc should not be null");
        assertNotNull(saved.getIdBloc(), "Saved bloc ID should not be null");
        assertThat(saved.getChambres()).isNotNull();
        assertEquals(1, saved.getChambres().size());
        assertEquals(saved, saved.getChambres().get(0).getBloc());
    }

    @Test
    void testAffecterBlocAFoyer() {
        // Save foyer first, so it exists in the DB
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer Central");
        foyer = foyerRepository.save(foyer);

        // Save bloc first, so it exists in the DB
        Bloc bloc = Bloc.builder().nomBloc("Bloc C").capaciteBloc(80).build();
        bloc = blocRepository.save(bloc);

        // Now affect bloc to foyer by names
        Bloc updated = blocService.affecterBlocAFoyer("Bloc C", "Foyer Central");

        assertNotNull(updated, "Updated bloc should not be null");
        assertNotNull(updated.getFoyer(), "Bloc's foyer should not be null");
        assertEquals("Foyer Central", updated.getFoyer().getNomFoyer());
    }
}
