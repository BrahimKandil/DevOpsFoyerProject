package tn.esprit.spring.Etudiant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.RestControllers.EtudiantRestController;
import tn.esprit.spring.Services.Etudiant.IEtudiantService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EtudiantRestController.class)
public class EtudiantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEtudiantService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Etudiant sampleEtudiant;

    @BeforeEach
    void setup() {
        sampleEtudiant = Etudiant.builder()
                .idEtudiant(1L)
                .nomEt("Doe")
                .prenomEt("John")
                .cin(12345678L)
                .ecole("ENIT")
                .dateNaissance(LocalDate.of(1995, 5, 15))
                .build();
    }

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
    void testDelete() throws Exception {
        // For delete, just mock doNothing on service.delete
        doNothing().when(service).delete(any(Etudiant.class));

        mockMvc.perform(delete("/etudiant/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEtudiant)))
                .andExpect(status().isOk());

        verify(service).delete(any(Etudiant.class));
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
