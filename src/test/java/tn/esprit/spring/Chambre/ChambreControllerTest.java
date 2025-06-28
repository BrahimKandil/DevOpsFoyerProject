package tn.esprit.spring.Chambre;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.restcontrollers.ChambreRestController;
import tn.esprit.spring.services.chambre.ChambreService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChambreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChambreService chambreService;

    @InjectMocks
    private ChambreRestController chambreRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Chambre sampleChambre;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(chambreRestController).build();
        sampleChambre = new Chambre(1L, 101, TypeChambre.DOUBLE, null, null);
    }

    @Test
    void testFindAll() throws Exception {
        when(chambreService.findAll()).thenReturn(List.of(sampleChambre));

        mockMvc.perform(get("/chambre/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1))
                .andExpect(jsonPath("$[0].typeC").value("DOUBLE"));
    }

    @Test
    void testFindById() throws Exception {
        when(chambreService.findById(1L)).thenReturn(sampleChambre);

        mockMvc.perform(get("/chambre/findById").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idChambre").value(1))
                .andExpect(jsonPath("$.typeC").value("DOUBLE"));
    }

    @Test
    void testAddOrUpdate() throws Exception {
        when(chambreService.addOrUpdate(any(Chambre.class))).thenReturn(sampleChambre);

        mockMvc.perform(post("/chambre/addOrUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleChambre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idChambre").value(1));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(chambreService).delete(any(Chambre.class));

        mockMvc.perform(delete("/chambre/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleChambre)))
                .andExpect(status().isOk());

        verify(chambreService, times(1)).delete(any(Chambre.class));
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(chambreService).deleteById(1L);

        mockMvc.perform(delete("/chambre/deleteById").param("id", "1"))
                .andExpect(status().isOk());

        verify(chambreService).deleteById(1L);
    }

    @Test
    void testGetChambresParNomBloc() throws Exception {
        when(chambreService.getChambresParNomBloc("Bloc A")).thenReturn(List.of(sampleChambre));

        mockMvc.perform(get("/chambre/getChambresParNomBloc").param("nomBloc", "Bloc A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1));
    }

    @Test
    void testNbChambreParTypeEtBloc() throws Exception {
        when(chambreService.nbChambreParTypeEtBloc(TypeChambre.DOUBLE, 1L)).thenReturn(5L);

        mockMvc.perform(get("/chambre/nbChambreParTypeEtBloc")
                        .param("type", "DOUBLE")
                        .param("idBloc", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testGetChambresNonReserveParNomFoyerEtTypeChambre() throws Exception {
        when(chambreService.getChambresNonReserveParNomFoyerEtTypeChambre("Foyer X", TypeChambre.DOUBLE))
                .thenReturn(List.of(sampleChambre));

        mockMvc.perform(get("/chambre/getChambresNonReserveParNomFoyerEtTypeChambre")
                        .param("nomFoyer", "Foyer X")
                        .param("type", "DOUBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1));
    }
}
