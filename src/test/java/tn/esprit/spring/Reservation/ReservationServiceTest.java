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

    @Test
    void testAddOrUpdate() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");

        when(repo.save(r)).thenReturn(r);

        Reservation result = reservationService.addOrUpdate(r);

        assertThat(result).isEqualTo(r);
        verify(repo).save(r);
    }

    @Test
    void testFindAll() {
        Reservation r1 = new Reservation();
        r1.setIdReservation("R1");
        Reservation r2 = new Reservation();
        r2.setIdReservation("R2");

        when(repo.findAll()).thenReturn(List.of(r1, r2));

        List<Reservation> result = reservationService.findAll();

        assertThat(result).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    void testFindById() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");

        when(repo.findById("R1")).thenReturn(Optional.of(r));

        Reservation result = reservationService.findById("R1");

        assertThat(result).isEqualTo(r);
        verify(repo).findById("R1");
    }

    @Test
    void testDeleteById() {
        doNothing().when(repo).deleteById("R1");

        reservationService.deleteById("R1");

        verify(repo).deleteById("R1");
    }

    @Test
    void testDelete() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");

        doNothing().when(repo).delete(r);

        reservationService.delete(r);

        verify(repo).delete(r);
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_ChambreHasCapacity() {
        // Setup chambre with DOUBLE type and 1 current reservation, capacity is 2
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(101L);
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.setReservations(new ArrayList<>());

        // Mock bloc name for reservation ID string generation
        tn.esprit.spring.DAO.Entities.Bloc bloc = new tn.esprit.spring.DAO.Entities.Bloc();
        bloc.setNomBloc("BlocA");
        chambre.setBloc(bloc);

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(123456L);

        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(123456L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

        // Fix here: when saving, return the same Reservation passed to save()
        when(repo.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);
            // Ensure etudiants list is initialized to avoid NPE during test
            if (res.getEtudiants() == null) {
                res.setEtudiants(new ArrayList<>());
            }
            // Add etudiant to reservation (simulate what service does)
            res.getEtudiants().add(etudiant);
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

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_ChambreFullCapacity() {
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(101L);
        chambre.setTypeC(TypeChambre.SIMPLE);

        tn.esprit.spring.DAO.Entities.Bloc bloc = new tn.esprit.spring.DAO.Entities.Bloc();
        bloc.setNomBloc("BlocA");
        chambre.setBloc(bloc);

        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(123456L)).thenReturn(new Etudiant());
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

        Reservation res = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(101L, 123456L);

        assertThat(res).isNull();
        verify(repo, never()).save(any());
        verify(chambreRepository, never()).save(any());
    }

    @Test
    void testGetReservationParAnneeUniversitaire() {
        LocalDate start = LocalDate.of(2023, 9, 15);
        LocalDate end = LocalDate.of(2024, 6, 30);

        when(repo.countByAnneeUniversitaireBetween(start, end)).thenReturn(10);

        long count = reservationService.getReservationParAnneeUniversitaire(start, end);

        assertThat(count).isEqualTo(10L);
        verify(repo).countByAnneeUniversitaireBetween(start, end);
    }

    @Test
    void testAnnulerReservation() {
        Reservation r = new Reservation();
        r.setIdReservation("R123");
        r.setEstValide(true);
        r.setEtudiants(new ArrayList<>());

        Chambre c = new Chambre();
        c.setReservations(new ArrayList<>(List.of(r)));

        when(repo.findByEtudiantsCinAndEstValide(123456L, true)).thenReturn(r);
        when(chambreRepository.findByReservationsIdReservation("R123")).thenReturn(c);
        // Fix here: save returns the entity, do not use doNothing() on save()
        when(chambreRepository.save(c)).thenReturn(c);
        doNothing().when(repo).delete(r);

        String message = reservationService.annulerReservation(123456L);

        assertThat(message).contains("R123");
        assertThat(c.getReservations()).doesNotContain(r);

        verify(chambreRepository).save(c);
        verify(repo).delete(r);
    }

    @Test
    void testAffectReservationAChambre() {
        Reservation r = new Reservation();
        r.setIdReservation("R123");
        Chambre c = new Chambre();
        c.setReservations(new ArrayList<>());

        when(repo.findById("R123")).thenReturn(Optional.of(r));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(c));
        when(chambreRepository.save(c)).thenReturn(c);

        reservationService.affectReservationAChambre("R123", 1L);

        assertThat(c.getReservations()).contains(r);
        verify(chambreRepository).save(c);
    }

    @Test
    void testDeaffectReservationAChambre() {
        Reservation r = new Reservation();
        r.setIdReservation("R123");
        Chambre c = new Chambre();
        c.setReservations(new ArrayList<>(List.of(r)));

        when(repo.findById("R123")).thenReturn(Optional.of(r));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(c));
        when(chambreRepository.save(c)).thenReturn(c);

        reservationService.deaffectReservationAChambre("R123", 1L);

        assertThat(c.getReservations()).doesNotContain(r);
        verify(chambreRepository).save(c);
    }

    @Test
    void testAnnulerReservations() {
        LocalDate start = reservationService.getDateDebutAU();
        LocalDate end = reservationService.getDateFinAU();

        Reservation r1 = new Reservation();
        r1.setIdReservation("R1");
        r1.setEstValide(true);

        Reservation r2 = new Reservation();
        r2.setIdReservation("R2");
        r2.setEstValide(true);

        when(repo.findByEstValideAndAnneeUniversitaireBetween(true, start, end))
                .thenReturn(List.of(r1, r2));

        when(repo.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.annulerReservations();

        assertThat(r1.isEstValide()).isFalse();
        assertThat(r2.isEstValide()).isFalse();

        verify(repo, times(2)).save(any(Reservation.class));
    }
}