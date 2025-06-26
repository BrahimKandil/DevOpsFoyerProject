package tn.esprit.spring.Reservation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository repo;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @BeforeEach
    void setUp() {
        // No need to openMocks here with MockitoExtension
    }

    // Other test methods...

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_ChambreHasCapacity() {
        // Setup chambre with DOUBLE type and 1 current reservation, capacity is 2
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(101L);
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.setReservations(new ArrayList<>());

        tn.esprit.spring.DAO.Entities.Bloc bloc = new tn.esprit.spring.DAO.Entities.Bloc();
        bloc.setNomBloc("BlocA");
        chambre.setBloc(bloc);

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(123456L);

        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(123456L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

        // This is the key fix:
        when(repo.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);
            if (res.getEtudiants() == null) {
                res.setEtudiants(new ArrayList<>());
            }
            res.getEtudiants().add(etudiant);  // simulate adding etudiant
            return res;
        });
        when(chambreRepository.save(any(Chambre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(101L, 123456L);

        assertThat(res).isNotNull();
        assertThat(res.isEstValide()).isTrue();
        assertThat(res.getEtudiants()).contains(etudiant);
        assertThat(chambre.getReservations()).contains(res);

        verify(repo).save(any(Reservation.class));
        verify(chambreRepository).save(chambre);
    }

    // Rest of your tests...
}
