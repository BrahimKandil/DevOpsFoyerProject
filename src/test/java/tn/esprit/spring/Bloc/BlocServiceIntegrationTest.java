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

        assertNotNull(result);
        assertNotNull(result.getIdBloc());
        assertEquals("Bloc Alpha", result.getNomBloc());
    }

    @Test
    void testFindAll() {
        blocRepository.save(Bloc.builder().nomBloc("Bloc1").capaciteBloc(50).build());
        blocRepository.save(Bloc.builder().nomBloc("Bloc2").capaciteBloc(75).build());

        List<Bloc> blocs = blocService.findAll();

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

        assertNotNull(saved);
        assertNotNull(saved.getIdBloc());
        assertEquals(1, saved.getChambres().size());
        assertEquals(saved, saved.getChambres().get(0).getBloc());
    }

    @Test
    void testAffecterBlocAFoyer() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer Central");
        foyer = foyerRepository.save(foyer);

        Bloc bloc = Bloc.builder().nomBloc("Bloc C").capaciteBloc(80).build();
        bloc = blocRepository.save(bloc);

        Bloc updated = blocService.affecterBlocAFoyer("Bloc C", "Foyer Central");

        assertNotNull(updated);
        assertNotNull(updated.getFoyer());
        assertEquals("Foyer Central", updated.getFoyer().getNomFoyer());
    }
}
