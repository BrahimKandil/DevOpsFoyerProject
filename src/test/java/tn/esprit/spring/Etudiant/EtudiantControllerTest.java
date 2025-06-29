package tn.esprit.spring.Etudiant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.restcontrollers.EtudiantRestController;
import tn.esprit.spring.services.etudiant.EtudiantService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EtudiantControllerTest {

    private MockMvc mockMvc;
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(etudiantRestController).build();
        sampleEtudiant = Etudiant.builder()
                .idEtudiant(1L)
                .nomEt("Doe")
                .prenomEt("John")
                .cin(12345678L)
                .ecole("ENIT")
                .dateNaissance(LocalDate.of(1995, 5, 15))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Mock
    private EtudiantService service;

    @InjectMocks
    private ObjectMapper objectMapper;

    private Etudiant sampleEtudiant;
    @InjectMocks
    private EtudiantRestController etudiantRestController;

    @Test
    void testFindAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleEtudiant));

        mockMvc.perform(get("/etudiant/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEtudiant").value(1L))
                .andExpect(jsonPath("$[0].nomEt").value("Doe"));
    }

    @Test
    void testAddOrUpdate() throws Exception {
        when(service.addOrUpdate(any(Etudiant.class))).thenReturn(sampleEtudiant);

        mockMvc.perform(post("/etudiant/addOrUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEtudiant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEtudiant").value(1L))
                .andExpect(jsonPath("$.nomEt").value("Doe"));
    }

    @Test
    void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(sampleEtudiant);

        mockMvc.perform(get("/etudiant/findById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenomEt").value("John"));
    }

    @Test
    void testDelete(){
        // For delete, just mock doNothing on service.delete
        Etudiant sampleEtudiant1 = new Etudiant();
        sampleEtudiant1.setIdEtudiant(1L);
        sampleEtudiant1.setNomEt("Doe");
        sampleEtudiant1.setPrenomEt("John");
        sampleEtudiant1.setCin(12345678L);
        sampleEtudiant1.setEcole("ENIT");
        sampleEtudiant1.setDateNaissance(LocalDate.of(1995, 5, 15));
        sampleEtudiant1.setReservations(null);
        doNothing().when(service).delete(any(Etudiant.class));

        try {
            String json = objectMapper.writeValueAsString(sampleEtudiant1);
            System.out.println("Serialized JSON: " + json);

        mockMvc.perform(delete("/etudiant/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(service).delete(any(Etudiant.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(service).deleteById(1L);

        mockMvc.perform(delete("/etudiant/deleteById")
                        .param("id", "1"))
                .andExpect(status().isOk());

        verify(service).deleteById(1L);
    }

    @Test
    void testSelectJPQL() throws Exception {
        when(service.selectJPQL("Doe")).thenReturn(List.of(sampleEtudiant));

        mockMvc.perform(get("/etudiant/selectJPQL")
                        .param("nom", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ecole").value("ENIT"));
    }
}
