package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.DAO.Entities.*;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private MockMvc mockMvc;
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository repo;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationService).build();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_ChambreHasCapacity() {
        // Given
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

        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        etudiantRepository.save(etudiant);

        when(etudiantRepository.findByCin(123456L)).thenReturn(etudiant);

        // Simulate chambre capacity (only 1 reservation exists)
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(1);

        when(repo.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);

            // Ensure etudiants list is initialized
            if (res.getEtudiants() == null) {
                res.setEtudiants(new ArrayList<>());
            }

            // Link entities
            res.getEtudiants().add(etudiant);

            if (etudiant.getReservations() == null) {
                etudiant.setReservations(new ArrayList<>());
            }
            etudiant.getReservations().add(res);

            if (chambre.getReservations() == null) {
                chambre.setReservations(new ArrayList<>());
            }
            chambre.getReservations().add(res);

            return res;
        });

        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        // When
        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(101L, 123456L);

        // Then
        assertThat(res).isNotNull();
        assertThat(res.isEstValide()).isTrue();
        assertThat(res.getEtudiants()).contains(etudiant);
        assertThat(chambre.getReservations()).contains(res);

        verify(chambreRepository).findByNumeroChambre(101L);
        verify(etudiantRepository).findByCin(123456L);
        verify(repo).save(any(Reservation.class));
        verify(chambreRepository).save(chambre);
    }

    @Test
    void getDateDebutAU_shouldReturnPreviousYearWhenBeforeAugust() {
        // Mock current date to June 15, 2023
        LocalDate mockDate = LocalDate.of(2023, 6, 15);
        try (var mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            LocalDate result = reservationService.getDateDebutAU();
            assertEquals(LocalDate.of(2022, 9, 15), result);
        }
    }

    @Test
    void getDateDebutAU_shouldReturnCurrentYearWhenAfterJuly() {
        // Mock current date to September 1, 2023
        LocalDate mockDate = LocalDate.of(2023, 9, 1);
        try (var mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            LocalDate result = reservationService.getDateDebutAU();
            assertEquals(LocalDate.of(2023, 9, 15), result);
        }
    }

    @Test
    void getDateFinAU_shouldReturnCurrentYearWhenBeforeAugust() {
        // Mock current date to June 15, 2023
        LocalDate mockDate = LocalDate.of(2023, 6, 15);
        try (var mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            LocalDate result = reservationService.getDateFinAU();
            assertEquals(LocalDate.of(2023, 6, 30), result);
        }
    }

    @Test
    void getDateFinAU_shouldReturnNextYearWhenAfterJuly() {
        // Mock current date to September 1, 2023
        LocalDate mockDate = LocalDate.of(2023, 9, 1);
        try (var mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            LocalDate result = reservationService.getDateFinAU();
            assertEquals(LocalDate.of(2024, 6, 30), result);
        }
    }
}
