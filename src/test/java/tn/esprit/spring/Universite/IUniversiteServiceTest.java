package tn.esprit.spring.Universite;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.Dao.Entities.Universite;
import tn.esprit.spring.Dao.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Universite.UniversiteService;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class IUniversiteServiceTest {
    private MockMvc mockMvc;

    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteService universiteService;  // your implementation class

    private Universite universite;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(universiteService).build();

        universite = Universite.builder()
                .idUniversite(1L)
                .nomUniversite("Université de Tunis")
                .adresse("Rue de la Paix")
                .build();
    }

    @Test
    void testAddOrUpdate() {
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite saved = universiteService.addOrUpdate(universite);

        assertThat(saved).isNotNull();
        assertThat(saved.getNomUniversite()).isEqualTo("Université de Tunis");
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testFindAll() {
        List<Universite> list = Collections.singletonList(universite);
        when(universiteRepository.findAll()).thenReturn(list);

        List<Universite> result = universiteService.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

        Universite found = universiteService.findById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getIdUniversite()).isEqualTo(1L);
        verify(universiteRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(universiteRepository).deleteById(1L);

        universiteService.deleteById(1L);

        verify(universiteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(universiteRepository).delete(universite);

        universiteService.delete(universite);

        verify(universiteRepository, times(1)).delete(universite);
    }

    @Test
    void testAjouterUniversiteEtSonFoyer() {
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite saved = universiteService.ajouterUniversiteEtSonFoyer(universite);

        assertThat(saved).isNotNull();
        verify(universiteRepository, times(1)).save(universite);
    }
}
