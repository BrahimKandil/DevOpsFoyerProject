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


import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.restcontrollers.FoyerRestController;
import tn.esprit.spring.services.foyer.FoyerService;

@ExtendWith(MockitoExtension.class)
class FoyerControllerTest {

    @Mock
    private FoyerService service;

    private FoyerRestController controller;

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
    void testAddOrUpdate()  {

            when(service.addOrUpdate(any(Foyer.class))).thenReturn(sampleFoyer);

            Foyer response = controller.addOrUpdate(sampleFoyer);

            assertThat(response).isNotNull();
            assertThat(response.getIdFoyer()).isEqualTo(1L);
            assertThat(response.getNomFoyer()).isEqualTo("Main Foyer");
    }

    @Test
    void testFindAll() {

            when(service.findAll()).thenReturn(List.of(sampleFoyer));

            List<Foyer> response = controller.findAll();

            assertThat(response).isNotEmpty();
            assertThat(response.get(0).getIdFoyer()).isEqualTo(1L);
            assertThat(response.get(0).getNomFoyer()).isEqualTo("Main Foyer");

    }

    @Test
    void testFindById() {

            when(service.findById(1L)).thenReturn(sampleFoyer);

            Foyer response = controller.findById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getCapaciteFoyer()).isEqualTo(100L);

    }

    @Test
    void testDelete() {

            controller.delete(sampleFoyer);
            assertThat(controller.findById(1L)).isNull(); // Assuming findById returns null if not found
    }

    @Test
    void testDeleteById()  {
            controller.deleteById(1L);
        assertThat(controller.findById(1L)).isNull(); // Assuming findById returns null if not found

        // no exception means success

    }

    @Test
    void testAffecterFoyerAUniversite_ByIdAndName(){
            when(service.affecterFoyerAUniversite(1L, "Test University")).thenReturn(sampleUniversite);

            Universite response = controller.affecterFoyerAUniversite(1L, "Test University");

            assertThat(response).isNotNull();
            assertThat(response.getNomUniversite()).isEqualTo("Test University");

    }

    @Test
    void testDesaffecterFoyerAUniversite() {
            when(service.desaffecterFoyerAUniversite(1L)).thenReturn(sampleUniversite);

            Universite response = controller.desaffecterFoyerAUniversite(1L);

            assertThat(response).isNotNull();
            assertThat(response.getIdUniversite()).isEqualTo(1L);
    }


    @Test
    void testAjouterFoyerEtAffecterAUniversite() {
        when(service.ajouterFoyerEtAffecterAUniversite(any(Foyer.class), eq(1L))).thenReturn(sampleFoyer);

        Foyer response = controller.ajouterFoyerEtAffecterAUniversite(sampleFoyer, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getIdFoyer()).isEqualTo(1L);
    }

    @Test
    void testAffecterFoyerAUniversite_ByPathVariables() {


        when(service.affecterFoyerAUniversite(1L, 1L)).thenReturn(sampleUniversite);

        Universite response = controller.affecterFoyerAUniversite(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getIdUniversite()).isEqualTo(1L);
    }
}