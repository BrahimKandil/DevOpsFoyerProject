package tn.esprit.spring.Reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.spring.dao.entities.*;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void testAjouterReservation_ChambreSimple() {
        Chambre chambre = new Chambre();
        chambre.setIdChambre(2L);
        chambre.setNumeroChambre(102L);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setReservations(new ArrayList<>());

        Bloc bloc = new Bloc();
        bloc.setNomBloc("BlocB");
        chambre.setBloc(bloc);

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(111111L);
        etudiant.setReservations(new ArrayList<>());

        when(chambreRepository.findByNumeroChambre(102L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(111111L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                eq(2L), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(0); // Ensure space available

        ArrayList<Etudiant> etudiants = new ArrayList<>();
        etudiants.add(etudiant);
        when(repo.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);
            res.setEtudiants(etudiants); // Trigger `addAll`
            return res;
        });

        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(102L, 111111L);

        assertThat(res).isNotNull();
        assertThat(res.getEtudiants()).contains(etudiant);
        assertThat(res.isEstValide()).isTrue();
    }

    @Test
    void testAjouterReservation_ChambreFullShouldReturnNull() {
        Chambre chambre = new Chambre();
        chambre.setIdChambre(3L);
        chambre.setNumeroChambre(103L);
        chambre.setTypeC(TypeChambre.TRIPLE);
        chambre.setReservations(new ArrayList<>());

        Bloc bloc = new Bloc();
        bloc.setNomBloc("BlocC");
        chambre.setBloc(bloc);

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(999999L);
        etudiant.setReservations(new ArrayList<>());

        when(chambreRepository.findByNumeroChambre(103L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(999999L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                eq(3L), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(3); // Chambre is full

        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(103L, 999999L);

        assertThat(res).isNull(); // 💥 Should cover the `return null`
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
    @Test
    void testAnnulerReservation_shouldRemoveReservationSuccessfully() {
        // Arrange
        long cin = 123456L;
        String reservationId = "2024/2025-BlocA-101-123456";

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(cin);
        ArrayList<Etudiant> etudiants = new ArrayList<>();
        etudiants.add(etudiant);


        Reservation reservation = Reservation.builder()
                .idReservation(reservationId)
                .estValide(true)
                .etudiants(etudiants)
                .build();
        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        Chambre chambre = new Chambre();
        chambre.setReservations(reservations);

        // Mock behavior
        when(repo.findByEtudiantsCinAndEstValide(cin, true)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation(reservationId)).thenReturn(chambre);

        // Act
        String result = reservationService.annulerReservation(cin);

        // Assert
        assertThat(result).isEqualTo("La réservation " + reservationId + " est annulée avec succés");
        assertThat(chambre.getReservations()).doesNotContain(reservation);

        verify(repo).findByEtudiantsCinAndEstValide(cin, true);
        verify(chambreRepository).findByReservationsIdReservation(reservationId);
        verify(chambreRepository).save(chambre);
        verify(repo).delete(reservation);
    }
    @Test
    void testAffectReservationAChambre_shouldAffectSuccessfully() {
        // Arrange
        String reservationId = "2024/2025-BlocA-101-123456";
        long chambreId = 1L;

        Reservation reservation = Reservation.builder()
                .idReservation(reservationId)
                .build();

        Chambre chambre = new Chambre();
        chambre.setReservations(new ArrayList<>());

        // Mock repository behavior
        when(repo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));

        // Act
        reservationService.affectReservationAChambre(reservationId, chambreId);

        // Assert
        assertThat(chambre.getReservations()).contains(reservation);
        verify(repo).findById(reservationId);
        verify(chambreRepository).findById(chambreId);
        verify(chambreRepository).save(chambre);
    }

    @Test
    void testAffectReservationAChambre_shouldDoNothingIfNotFound() {
        // Arrange
        String reservationId = "res-1";
        long chambreId = 2L;

        when(repo.findById(reservationId)).thenReturn(Optional.empty());
        when(chambreRepository.findById(chambreId)).thenReturn(Optional.empty());

        // Act
        reservationService.affectReservationAChambre(reservationId, chambreId);

        // Assert
        verify(chambreRepository, never()).save(any());
    }
    @Test
    void testAnnulerReservation() {
        Etudiant etudiant = new Etudiant();
        etudiant.setCin(123456L);

        Reservation reservation = new Reservation();
        reservation.setIdReservation("2024/2025-B-101-123456");
        reservation.setEstValide(true);
        reservation.setEtudiants(List.of(etudiant));

        Chambre chambre = new Chambre();
        chambre.setReservations(new ArrayList<>(List.of(reservation)));

        when(repo.findByEtudiantsCinAndEstValide(123456L, true)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation(reservation.getIdReservation())).thenReturn(chambre);

        String result = reservationService.annulerReservation(123456L);

        assertThat(result).contains("est annulée avec succés");
        verify(repo).delete(reservation);
        verify(chambreRepository).save(chambre);
    }

    @Test
    void testAffectReservationAChambre() {
        Reservation reservation = new Reservation();
        Chambre chambre = new Chambre();
        chambre.setReservations(new ArrayList<>());

        when(repo.findById("res1")).thenReturn(Optional.of(reservation));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        reservationService.affectReservationAChambre("res1", 1L);

        assertThat(chambre.getReservations()).contains(reservation);
        verify(chambreRepository).save(chambre);
    }
    @Test
    void testDeaffectReservationAChambre() {
        Reservation reservation = new Reservation();
        Chambre chambre = new Chambre();
        chambre.setReservations(new ArrayList<>(List.of(reservation)));

        when(repo.findById("res1")).thenReturn(Optional.of(reservation));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        reservationService.deaffectReservationAChambre("res1", 1L);

        assertThat(chambre.getReservations()).doesNotContain(reservation);
        verify(chambreRepository).save(chambre);
    }
    @Test
    void testAnnulerReservations() {
        // Create the fixed LocalDate to return from now()
        LocalDate fakeNow = LocalDate.of(2024, 10, 1);

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            // Only mock LocalDate.now() to return fakeNow
            mockedLocalDate.when(LocalDate::now).thenReturn(fakeNow);

            // Setup reservation with the same date
            Reservation r1 = new Reservation();
            r1.setIdReservation("res1");
            r1.setEstValide(true);
            r1.setAnneeUniversitaire(fakeNow);

            when(repo.findByEstValideAndAnneeUniversitaireBetween(eq(true), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of(r1));

            reservationService.annulerReservations();

            assertThat(r1.isEstValide()).isFalse();
            verify(repo).save(r1);
        }
    }

}
