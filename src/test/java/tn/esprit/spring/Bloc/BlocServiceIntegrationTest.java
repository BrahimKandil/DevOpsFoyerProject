package tn.esprit.spring.Bloc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

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
        blocService.addOrUpdate(Bloc.builder().nomBloc("Bloc1").capaciteBloc(50).build());
        blocService.addOrUpdate(Bloc.builder().nomBloc("Bloc2").capaciteBloc(75).build());

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

        Chambre chambre = Chambre.builder()
                .numeroChambre(101L)
                .bloc(bloc)
                .build();

        List<Chambre> chambres = new ArrayList<>();
        chambres.add(chambre);
        bloc.setChambres(chambres);

        Bloc saved = blocService.addOrUpdate(bloc);

        assertNotNull(saved, "Saved bloc should not be null");
        assertNotNull(saved.getIdBloc(), "Saved bloc ID should not be null");
        assertThat(saved.getChambres()).isNotNull();
        assertEquals(1, saved.getChambres().size());
        assertEquals(saved.getNomBloc(), saved.getChambres().get(0).getBloc().getNomBloc());
    }

    @Test
    void testAffecterBlocAFoyer() {
        Foyer foyer = Foyer.builder()
                .nomFoyer("Foyer Central")
                .capaciteFoyer(200L)
                .build();
        foyer = foyerRepository.save(foyer);
        assertNotNull(foyer.getIdFoyer());

        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc C")
                .capaciteBloc(80)
                .build();
        bloc = blocRepository.save(bloc);
        assertNotNull(bloc.getIdBloc());

        Bloc updated = blocService.affecterBlocAFoyer("Bloc C", "Foyer Central");

        assertNotNull(updated, "Updated bloc should not be null");
        assertNotNull(updated.getFoyer(), "Bloc's foyer should not be null");
        assertEquals("Foyer Central", updated.getFoyer().getNomFoyer());
    }
}
