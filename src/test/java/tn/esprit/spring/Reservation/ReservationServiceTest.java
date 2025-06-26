package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.*;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
        // MockitoExtension handles setup
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_ChambreHasCapacity() {
        // Given: a chambre with capacity for DOUBLE (2 students)
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(101L);
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.setReservations(new ArrayList<>());

        Bloc bloc = new Bloc();
        bloc.setNomBloc("BlocA");
        chambre.setBloc(bloc);

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(123456L);
        etudiant.setReservations(new ArrayList<>());

        // Mock repository responses
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(123456L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(1); // Only one existing reservation

        // Simulate saving reservation
        when(repo.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);
            // Ensure collections are initialized
            if (res.getEtudiants() == null) {
                res.setEtudiants(new ArrayList<>());
            }
            res.getEtudiants().add(etudiant);

            // Also initialize the other side of the relationship
            if (etudiant.getReservations() == null) {
                etudiant.setReservations(new ArrayList<>());
            }
            etudiant.getReservations().add(res);

            return res;
        });

        when(chambreRepository.save(any(Chambre.class))).thenAnswer(invocation -> {
            Chambre c = invocation.getArgument(0);
            if (c.getReservations() == null) {
                c.setReservations(new ArrayList<>());
            }
            return c;
        });

        // When
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(101L, 123456L);

        // Then
        assertThat(res).isNotNull();
        assertThat(res.isEstValide()).isTrue();
        assertThat(res.getEtudiants()).contains(etudiant);
        assertThat(chambre.getReservations()).contains(res);

        // Verify interactions
        verify(chambreRepository).findByNumeroChambre(101L);
        verify(etudiantRepository).findByCin(123456L);
        verify(repo).save(any(Reservation.class));
        verify(chambreRepository).save(chambre);
    }
}