package tn.esprit.spring.schedular;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tn.esprit.spring.services.chambre.IChambreService;
import tn.esprit.spring.services.reservation.IReservationService;

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
}

