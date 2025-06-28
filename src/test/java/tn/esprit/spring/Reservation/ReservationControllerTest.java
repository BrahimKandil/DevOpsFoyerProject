package tn.esprit.spring.Reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.restcontrollers.ReservationRestController;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationService service;

    private ObjectMapper objectMapper;

    private Reservation reservation;

    @Mock
    private ReservationRestController controller;

    @BeforeEach
    void setup() {
        controller = new ReservationRestController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✅ Fix for LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional ISO formatting

        reservation = Reservation.builder()
                .idReservation("R123")
                .estValide(true)
                .anneeUniversitaire(LocalDate.now())
                .build();
    }

    @Test
    void testAddOrUpdate() throws Exception {
        when(service.addOrUpdate(any(Reservation.class))).thenReturn(reservation);

        mockMvc.perform(post("/reservation/addOrUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReservation").value("R123"))
                .andExpect(jsonPath("$.estValide").value(true));

        verify(service).addOrUpdate(any(Reservation.class));
    }

    @Test
    void testFindAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservation/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReservation").value("R123"));

        verify(service).findAll();
    }

    @Test
    void testFindById() throws Exception {
        when(service.findById("R123")).thenReturn(reservation);

        mockMvc.perform(get("/reservation/findById")
                        .param("id", "R123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReservation").value("R123"));

        verify(service).findById("R123");
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(service).deleteById("R123");

        mockMvc.perform(delete("/reservation/deleteById/R123"))
                .andExpect(status().isOk());

        verify(service).deleteById("R123");
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(any(Reservation.class));

        mockMvc.perform(delete("/reservation/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk());

        verify(service).delete(any(Reservation.class));
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() throws Exception {
        when(service.ajouterReservationEtAssignerAChambreEtAEtudiant(101L, 123456L))
                .thenReturn(reservation);

        mockMvc.perform(post("/reservation/ajouterReservationEtAssignerAChambreEtAEtudiant")
                        .param("numChambre", "101")
                        .param("cin", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReservation").value("R123"));

        verify(service).ajouterReservationEtAssignerAChambreEtAEtudiant(101L, 123456L);
    }

    @Test
    void testGetReservationParAnneeUniversitaire() throws Exception {
        LocalDate start = LocalDate.of(2023, 9, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);

        when(service.getReservationParAnneeUniversitaire(start, end)).thenReturn(5L);

        mockMvc.perform(get("/reservation/getReservationParAnneeUniversitaire")
                        .param("debutAnnee", start.toString())
                        .param("finAnnee", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(service).getReservationParAnneeUniversitaire(start, end);
    }

    @Test
    void testAnnulerReservation() throws Exception {
        when(service.annulerReservation(123456L)).thenReturn("Réservation annulée");

        mockMvc.perform(delete("/reservation/annulerReservation")
                        .param("cinEtudiant", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("Réservation annulée"));

        verify(service).annulerReservation(123456L);
    }
}
