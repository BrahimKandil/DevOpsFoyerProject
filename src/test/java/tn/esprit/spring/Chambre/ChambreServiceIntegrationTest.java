package tn.esprit.spring.Chambre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.Services.Chambre.IChambreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChambreServiceIntegrationTest {

    @Autowired
    private IChambreService chambreService;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private BlocRepository blocRepository;

    @Test
    public void testAddOrUpdateChambre() {
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc Test")
                .capaciteBloc(100L)
                .build();
        bloc = blocRepository.save(bloc);

        Chambre chambre = Chambre.builder()
                .numeroChambre(1L)
                .typeC(TypeChambre.SIMPLE)
                .bloc(bloc)
                .build();

        Chambre savedChambre = chambreService.addOrUpdate(chambre);
        assertThat(savedChambre.getIdChambre()).isNotNull();
        assertThat(savedChambre.getNumeroChambre()).isEqualTo(1L);
    }

    @Test
    public void testFindAllChambres() {
        Bloc bloc = blocRepository.save(
                Bloc.builder().nomBloc("Bloc B").capaciteBloc(50L).build()
        );

        Chambre ch1 = chambreService.addOrUpdate(
                Chambre.builder().numeroChambre(101L).typeC(TypeChambre.SIMPLE).bloc(bloc).build()
        );
        Chambre ch2 = chambreService.addOrUpdate(
                Chambre.builder().numeroChambre(102L).typeC(TypeChambre.DOUBLE).bloc(bloc).build()
        );

        List<Chambre> chambres = chambreService.findAll();
        assertThat(chambres.size()).isGreaterThanOrEqualTo(2);
        assertThat(chambres).extracting(Chambre::getNumeroChambre).contains(101L, 102L);
    }

    @Test
    public void testFindById() {
        Bloc bloc = blocRepository.save(
                Bloc.builder().nomBloc("Bloc C").capaciteBloc(75L).build()
        );

        Chambre chambre = chambreService.addOrUpdate(
                Chambre.builder().numeroChambre(303L).typeC(TypeChambre.TRIPLE).bloc(bloc).build()
        );

        Chambre found = chambreService.findById(chambre.getIdChambre());
        assertThat(found).isNotNull();
        assertThat(found.getNumeroChambre()).isEqualTo(303L);
    }

    @Test
    public void testDeleteById() {
        Bloc bloc = blocRepository.save(
                Bloc.builder().nomBloc("Bloc D").capaciteBloc(60L).build()
        );

        Chambre chambre = chambreService.addOrUpdate(
                Chambre.builder().numeroChambre(404L).typeC(TypeChambre.SIMPLE).bloc(bloc).build()
        );

        chambreService.deleteById(chambre.getIdChambre());

        boolean exists = chambreRepository.findById(chambre.getIdChambre()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    public void testGetChambresParNomBloc() {
        Bloc bloc = blocRepository.save(
                Bloc.builder().nomBloc("Bloc Special").capaciteBloc(80L).build()
        );

        chambreService.addOrUpdate(
                Chambre.builder().numeroChambre(505L).typeC(TypeChambre.DOUBLE).bloc(bloc).build()
        );

        List<Chambre> chambres = chambreService.getChambresParNomBloc("Bloc Special");
        assertThat(chambres).isNotEmpty();
        assertThat(chambres.get(0).getBloc().getNomBloc()).isEqualTo("Bloc Special");
    }
}
