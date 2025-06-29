package tn.esprit.spring.Bloc;

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
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.restcontrollers.BlocRestController;
import tn.esprit.spring.services.bloc.BlocService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class BlocControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BlocService blocService;

    @InjectMocks
    private BlocRestController blocRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Bloc sampleBloc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(blocRestController).build();
        sampleBloc = new Bloc(1L, "Bloc G", 10, null, null);
    }

    @Test
    void testFindAll() throws Exception {
        when(blocService.findAll()).thenReturn(List.of(sampleBloc));

        mockMvc.perform(get("/bloc/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idBloc").value(1))
                .andExpect(jsonPath("$[0].nomBloc").value("Bloc G"));
    }

    @Test
    void testFindById() throws Exception {
        when(blocService.findById(1L)).thenReturn(sampleBloc);

        mockMvc.perform(get("/bloc/findById").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBloc").value(1))
                .andExpect(jsonPath("$.nomBloc").value("Bloc G"));
    }

    @Test
    void testAddOrUpdate() throws Exception {
        when(blocService.addOrUpdate(any(Bloc.class))).thenReturn(sampleBloc);

        mockMvc.perform(post("/bloc/addOrUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBloc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBloc").value(1));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(blocService).delete(any(Bloc.class));

        mockMvc.perform(delete("/bloc/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBloc)))
                .andExpect(status().isOk());

        verify(blocService, times(1)).delete(any(Bloc.class));
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(blocService).deleteById(1L);

        mockMvc.perform(delete("/bloc/deleteById").param("id", "1"))
                .andExpect(status().isOk());

        verify(blocService).deleteById(1L);
    }

    @Test
    void testAffecterChambresABloc() {
        // Arrange
        List<Long> chambres = List.of(101L, 102L);
        String nomBloc = "Bloc G";
        Bloc expectedBloc = new Bloc();
        expectedBloc.setNomBloc(nomBloc);

        // Mock the service call with direct parameters
        when(blocService.affecterChambresABloc(chambres, nomBloc))
                .thenReturn(expectedBloc);

        // Act
        Bloc result = blocService.affecterChambresABloc(chambres, nomBloc);

        // Assert
        assertNotNull(result);
        assertEquals(nomBloc, result.getNomBloc());
        verify(blocService).affecterChambresABloc(chambres, nomBloc);
    }
    @Test
    void testAffecterBlocAFoyer() throws Exception {
        when(blocService.affecterBlocAFoyer("Bloc G", "Foyer 1")).thenReturn(sampleBloc);

        mockMvc.perform(put("/bloc/affecterBlocAFoyer")
                        .param("nomBloc", "Bloc G")
                        .param("nomFoyer", "Foyer 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomBloc").value("Bloc G"));
    }

    @Test
    void testAffecterBlocAFoyer2() throws Exception {
        when(blocService.affecterBlocAFoyer("Bloc G", "Foyer 1")).thenReturn(sampleBloc);

        mockMvc.perform(put("/bloc/affecterBlocAFoyer2/Foyer 1/Bloc G"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomBloc").value("Bloc G"));
    }

    @Test
    void testAjouterBlocEtSesChambres() throws Exception {
        when(blocService.ajouterBlocEtSesChambres(any(Bloc.class))).thenReturn(sampleBloc);

        mockMvc.perform(post("/bloc/ajouterBlocEtSesChambres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBloc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomBloc").value("Bloc G"));
    }

    @Test
    void testAjouterBlocEtAffecterAFoyer() throws Exception {
        when(blocService.ajouterBlocEtAffecterAFoyer(any(Bloc.class), eq("Foyer 1"))).thenReturn(sampleBloc);

        mockMvc.perform(post("/bloc/ajouterBlocEtAffecterAFoyer/Foyer 1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBloc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomBloc").value("Bloc G"));
    }
}
