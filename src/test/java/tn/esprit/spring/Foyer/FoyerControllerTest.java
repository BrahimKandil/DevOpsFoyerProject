package tn.esprit.spring.Foyer;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.Services.Foyer.IFoyerService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FoyerControllerTest {

    @Mock
    private MockMvc mockMvc;

    @MockBean
    private IFoyerService service;

    @Mock
    private ObjectMapper objectMapper;

    Foyer sampleFoyer;
    Universite sampleUniversite;

    @BeforeEach
    void setup() {
        sampleFoyer = Foyer.builder()
                .idFoyer(1L)
                .nomFoyer("Main Foyer")
                .capaciteFoyer(100L)
                .build();

        sampleUniversite = new Universite();
        sampleUniversite.setIdUniversite(1L);
        sampleUniversite.setNomUniversite("Test University");
        sampleUniversite.setFoyer(sampleFoyer);
    }

    @Test
    void testAddOrUpdate() throws Exception {
        when(service.addOrUpdate(any(Foyer.class))).thenReturn(sampleFoyer);

        mockMvc.perform(post("/foyer/addOrUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleFoyer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoyer").value(1))
                .andExpect(jsonPath("$.nomFoyer").value("Main Foyer"));
    }

    @Test
    void testFindAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleFoyer));

        mockMvc.perform(get("/foyer/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFoyer").value(1))
                .andExpect(jsonPath("$[0].nomFoyer").value("Main Foyer"));
    }

    @Test
    void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(sampleFoyer);

        mockMvc.perform(get("/foyer/findById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capaciteFoyer").value(100));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/foyer/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleFoyer)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteById() throws Exception {
        mockMvc.perform(delete("/foyer/deleteById")
                        .param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testAffecterFoyerAUniversite_ByIdAndName() throws Exception {
        when(service.affecterFoyerAUniversite(1L, "Test University")).thenReturn(sampleUniversite);

        mockMvc.perform(put("/foyer/affecterFoyerAUniversite")
                        .param("idFoyer", "1")
                        .param("nomUniversite", "Test University"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomUniversite").value("Test University"));
    }

    @Test
    void testDesaffecterFoyerAUniversite() throws Exception {
        when(service.desaffecterFoyerAUniversite(1L)).thenReturn(sampleUniversite);

        mockMvc.perform(put("/foyer/desaffecterFoyerAUniversite")
                        .param("idUniversite", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testAjouterFoyerEtAffecterAUniversite() throws Exception {
        when(service.ajouterFoyerEtAffecterAUniversite(any(Foyer.class), eq(1L))).thenReturn(sampleFoyer);

        mockMvc.perform(post("/foyer/ajouterFoyerEtAffecterAUniversite")
                        .param("idUniversite", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleFoyer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoyer").value(1));
    }

    @Test
    void testAffecterFoyerAUniversite_ByPathVariables() throws Exception {
        when(service.affecterFoyerAUniversite(1L, 1L)).thenReturn(sampleUniversite);

        mockMvc.perform(put("/foyer/affecterFoyerAUniversite/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUniversite").value(1));
    }
}
