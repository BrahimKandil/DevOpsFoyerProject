package tn.esprit.spring.Universite;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.restcontrollers.UniversiteRestController;
import tn.esprit.spring.services.universite.UniversiteService;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)

class UniversiteControllerTest {

    @Mock
    private UniversiteService universiteService;

    @InjectMocks
    private UniversiteRestController universiteRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Universite universite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(universiteRestController).build();
        objectMapper = new ObjectMapper();

        universite = Universite.builder()
                .idUniversite(1L)
                .nomUniversite("Université de Tunis")
                .adresse("Rue de la Paix")
                .build();
    }

    @Test
    void testAddOrUpdate() throws Exception {
        when(universiteService.addOrUpdate(any(Universite.class))).thenReturn(universite);

        mockMvc.perform(post("/universite/addOrUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(universite)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomUniversite").value("Université de Tunis"));

        verify(universiteService, times(1)).addOrUpdate(any(Universite.class));
    }

    @Test
    void testFindAll() throws Exception {
        List<Universite> list = Collections.singletonList(universite);
        when(universiteService.findAll()).thenReturn(list);

        mockMvc.perform(get("/universite/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomUniversite").value("Université de Tunis"));

        verify(universiteService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        when(universiteService.findById(1L)).thenReturn(universite);

        mockMvc.perform(get("/universite/findById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUniversite").value(1));

        verify(universiteService, times(1)).findById(1L);
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(universiteService).delete(any(Universite.class));

        mockMvc.perform(delete("/universite/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(universite)))
                .andExpect(status().isOk());

        verify(universiteService, times(1)).delete(any(Universite.class));
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(universiteService).deleteById(1L);

        mockMvc.perform(delete("/universite/deleteById")
                        .param("id", "1"))
                .andExpect(status().isOk());

        verify(universiteService, times(1)).deleteById(1L);
    }

    @Test
    void testAjouterUniversiteEtSonFoyer() throws Exception {
        when(universiteService.ajouterUniversiteEtSonFoyer(any(Universite.class))).thenReturn(universite);

        mockMvc.perform(post("/universite/ajouterUniversiteEtSonFoyer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(universite)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomUniversite").value("Université de Tunis"));

        verify(universiteService, times(1)).ajouterUniversiteEtSonFoyer(any(Universite.class));
    }
}
