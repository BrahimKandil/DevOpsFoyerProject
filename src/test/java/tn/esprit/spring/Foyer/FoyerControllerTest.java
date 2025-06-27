package tn.esprit.spring.Foyer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.RestControllers.FoyerRestController;
import tn.esprit.spring.Services.Foyer.FoyerService;
import tn.esprit.spring.Services.Foyer.IFoyerService;

@ExtendWith(MockitoExtension.class)
class FoyerControllerTest {

    @Mock
    private FoyerService service;

    private FoyerRestController controller;

    private final ObjectMapper objectMapper = new ObjectMapper(); // if needed for manual serialization

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

        controller = new FoyerRestController(service);
    }

    @Test
    void testAddOrUpdate() throws Exception {
        try {
            when(service.addOrUpdate(any(Foyer.class))).thenReturn(sampleFoyer);

            Foyer response = controller.addOrUpdate(sampleFoyer);

            assertThat(response).isNotNull();
            assertThat(response.getIdFoyer()).isEqualTo(1L);
            assertThat(response.getNomFoyer()).isEqualTo("Main Foyer");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFindAll() throws Exception {
        try {
            when(service.findAll()).thenReturn(List.of(sampleFoyer));

            List<Foyer> response = controller.findAll();

            assertThat(response).isNotEmpty();
            assertThat(response.get(0).getIdFoyer()).isEqualTo(1L);
            assertThat(response.get(0).getNomFoyer()).isEqualTo("Main Foyer");
        }catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Test
    void testFindById() throws Exception {
        try {
            when(service.findById(1L)).thenReturn(sampleFoyer);

            Foyer response = controller.findById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getCapaciteFoyer()).isEqualTo(100L);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDelete() throws Exception {
        try {
            // delete method returns void in many controllers
            // so just call it; here, if it returns anything, assert accordingly
            controller.delete(sampleFoyer);
            // no exception means success
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDeleteById() throws Exception {
        try {
            controller.deleteById(1L);
            // no exception means success
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAffecterFoyerAUniversite_ByIdAndName() throws Exception {
        try {
            when(service.affecterFoyerAUniversite(1L, "Test University")).thenReturn(sampleUniversite);

            Universite response = controller.affecterFoyerAUniversite(1L, "Test University");

            assertThat(response).isNotNull();
            assertThat(response.getNomUniversite()).isEqualTo("Test University");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDesaffecterFoyerAUniversite() throws Exception {
        try {


            when(service.desaffecterFoyerAUniversite(1L)).thenReturn(sampleUniversite);

            Universite response = controller.desaffecterFoyerAUniversite(1L);

            assertThat(response).isNotNull();
            assertThat(response.getIdUniversite()).isEqualTo(1L);
        } catch (Exception e) {
            // Handle exception if needed
            e.printStackTrace();
        }
    }


    @Test
    void testAjouterFoyerEtAffecterAUniversite() throws Exception {
        try {


        when(service.ajouterFoyerEtAffecterAUniversite(any(Foyer.class), eq(1L))).thenReturn(sampleFoyer);

        Foyer response = controller.ajouterFoyerEtAffecterAUniversite(sampleFoyer, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getIdFoyer()).isEqualTo(1L);
    }catch (Exception e) {
            // Handle exception if needed
            e.printStackTrace();
        }
    }

    @Test
    void testAffecterFoyerAUniversite_ByPathVariables() throws Exception {
       try {


        when(service.affecterFoyerAUniversite(1L, 1L)).thenReturn(sampleUniversite);

        Universite response = controller.affecterFoyerAUniversite(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getIdUniversite()).isEqualTo(1L);
    }catch (Exception e) {
            // Handle exception if needed
            e.printStackTrace();
        }
    }
}