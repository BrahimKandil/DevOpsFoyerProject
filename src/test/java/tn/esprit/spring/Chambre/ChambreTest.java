package tn.esprit.spring.Chambre;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChambreTest {

    @Test
    void testNoArgsConstructor() {
        Chambre chambre = new Chambre();
        assertThat(chambre).isNotNull();
    }

    @Test
     void testAllArgsConstructor() {
        Bloc bloc = Bloc.builder().idBloc(1L).nomBloc("Bloc A").build();
        Chambre chambre = new Chambre(1L, 101L, TypeChambre.SIMPLE, bloc, List.of());

        assertThat(chambre.getIdChambre()).isEqualTo(1L);
        assertThat(chambre.getNumeroChambre()).isEqualTo(101L);
        assertThat(chambre.getTypeC()).isEqualTo(TypeChambre.SIMPLE);
        assertThat(chambre.getBloc()).isEqualTo(bloc);
        assertThat(chambre.getReservations()).isEmpty();
    }

    @Test
     void testBuilderPattern() {
        Bloc bloc = Bloc.builder().idBloc(2L).nomBloc("Bloc B").build();

        Chambre chambre = Chambre.builder()
                .idChambre(2L)
                .numeroChambre(202L)
                .typeC(TypeChambre.DOUBLE)
                .bloc(bloc)
                .reservations(List.of())
                .build();

        assertThat(chambre.getIdChambre()).isEqualTo(2L);
        assertThat(chambre.getNumeroChambre()).isEqualTo(202L);
        assertThat(chambre.getTypeC()).isEqualTo(TypeChambre.DOUBLE);
        assertThat(chambre.getBloc()).isEqualTo(bloc);
    }

    @Test
     void testSettersAndGetters() {
        Chambre chambre = new Chambre();
        chambre.setIdChambre(3L);
        chambre.setNumeroChambre(303L);
        chambre.setTypeC(TypeChambre.TRIPLE);

        Bloc bloc = Bloc.builder().idBloc(3L).nomBloc("Bloc C").build();
        chambre.setBloc(bloc);

        assertThat(chambre.getIdChambre()).isEqualTo(3L);
        assertThat(chambre.getNumeroChambre()).isEqualTo(303L);
        assertThat(chambre.getTypeC()).isEqualTo(TypeChambre.TRIPLE);
        assertThat(chambre.getBloc()).isEqualTo(bloc);
    }

    @Test
     void testReservationsDefaultInitialization() {
        Chambre chambre = new Chambre();
        assertThat(chambre.getReservations()).isNotNull();
        assertThat(chambre.getReservations()).isEmpty();
    }
}
