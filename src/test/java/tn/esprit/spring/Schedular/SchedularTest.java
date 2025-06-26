package tn.esprit.spring.Schedular;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tn.esprit.spring.Services.Chambre.IChambreService;
import tn.esprit.spring.Services.Reservation.IReservationService;

import static org.mockito.Mockito.*;

class SchedularTest {

    private IChambreService chambreService;
    private IReservationService reservationService;

    private Schedular schedular;

    @BeforeEach
    void setup() {
        chambreService = Mockito.mock(IChambreService.class);
        reservationService = Mockito.mock(IReservationService.class);

        schedular = new Schedular(chambreService, reservationService);
    }

    @Test
    void testService1_callsListeChambresParBloc() {
        schedular.service1();
        verify(chambreService, times(1)).listeChambresParBloc();
    }

// Uncomment and add tests if you enable those scheduled methods:

//    @Test
//    void testService2_callsPourcentageChambreParTypeChambre() {
//        schedular.service2();
//        verify(chambreService, times(1)).pourcentageChambreParTypeChambre();
//    }
//
//    @Test
//    void testService3_callsNbPlacesDisponibleParChambreAnneeEnCours() {
//        schedular.service3();
//        verify(chambreService, times(1)).nbPlacesDisponibleParChambreAnneeEnCours();
//    }
//
//    @Test
//    void testService4_callsAnnulerReservations() {
//        schedular.service4();
//        verify(reservationService, times(1)).annulerReservations();
//    }
}

