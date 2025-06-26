package tn.esprit.spring.Bloc;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BlocTest {

    @Test
    public void testNoArgsConstructor() {
        Bloc bloc = new Bloc();
        assertThat(bloc).isNotNull();
    }

    @Test
    public void testAllArgsConstructor() {
        Foyer foyer = Foyer.builder().idFoyer(1L).nomFoyer("Foyer A").build();
        Chambre chambre1 = Chambre.builder().idChambre(1L).numeroChambre(101L).build();
        Chambre chambre2 = Chambre.builder().idChambre(2L).numeroChambre(102L).build();
        List<Chambre> chambres = List.of(chambre1, chambre2);

        Bloc bloc = new Bloc(1L, "Bloc A", 50L, foyer, chambres);

        assertThat(bloc.getIdBloc()).isEqualTo(1L);
        assertThat(bloc.getNomBloc()).isEqualTo("Bloc A");
        assertThat(bloc.getCapaciteBloc()).isEqualTo(50L);
        assertThat(bloc.getFoyer()).isEqualTo(foyer);
        assertThat(bloc.getChambres()).containsExactly(chambre1, chambre2);
    }

    @Test
    public void testBuilder() {
        Foyer foyer = Foyer.builder().idFoyer(2L).nomFoyer("Foyer B").build();
        Bloc bloc = Bloc.builder()
                .idBloc(2L)
                .nomBloc("Bloc B")
                .capaciteBloc(100L)
                .foyer(foyer)
                .chambres(List.of())
                .build();

        assertThat(bloc.getIdBloc()).isEqualTo(2L);
        assertThat(bloc.getNomBloc()).isEqualTo("Bloc B");
        assertThat(bloc.getCapaciteBloc()).isEqualTo(100L);
        assertThat(bloc.getFoyer()).isEqualTo(foyer);
        assertThat(bloc.getChambres()).isEmpty();
    }

    @Test
    public void testSettersAndGetters() {
        Bloc bloc = new Bloc();
        bloc.setIdBloc(3L);
        bloc.setNomBloc("Bloc C");
        bloc.setCapaciteBloc(75L);

        Foyer foyer = Foyer.builder().idFoyer(3L).nomFoyer("Foyer C").build();
        bloc.setFoyer(foyer);

        assertThat(bloc.getIdBloc()).isEqualTo(3L);
        assertThat(bloc.getNomBloc()).isEqualTo("Bloc C");
        assertThat(bloc.getCapaciteBloc()).isEqualTo(75L);
        assertThat(bloc.getFoyer()).isEqualTo(foyer);
    }

    @Test
    public void testChambresInitialization() {
        Bloc bloc = new Bloc();
        assertThat(bloc.getChambres()).isNotNull();
        assertThat(bloc.getChambres()).isEmpty();
    }
}
