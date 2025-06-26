package tn.esprit.spring.Foyer;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.*;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Foyer.FoyerService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

public class FoyerServiceTest {

    @InjectMocks
    private FoyerService foyerService;

    @Mock
    private FoyerRepository foyerRepository;

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private BlocRepository blocRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Foyer foyer = new Foyer();
        when(foyerRepository.save(foyer)).thenReturn(foyer);

        Foyer saved = foyerService.addOrUpdate(foyer);

        assertThat(saved).isNotNull();
        verify(foyerRepository).save(foyer);
    }

    @Test
    void testFindAll() {
        Foyer f1 = new Foyer();
        Foyer f2 = new Foyer();
        when(foyerRepository.findAll()).thenReturn(List.of(f1, f2));

        List<Foyer> foyers = foyerService.findAll();

        assertThat(foyers).hasSize(2);
        verify(foyerRepository).findAll();
    }

    @Test
    void testFindById() {
        Foyer foyer = new Foyer();
        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));

        Foyer found = foyerService.findById(1L);

        assertThat(found).isNotNull();
        verify(foyerRepository).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(foyerRepository).deleteById(1L);

        foyerService.deleteById(1L);

        verify(foyerRepository).deleteById(1L);
    }

    @Test
    void testDelete() {
        Foyer foyer = new Foyer();
        doNothing().when(foyerRepository).delete(foyer);

        foyerService.delete(foyer);

        verify(foyerRepository).delete(foyer);
    }

    @Test
    void testAffecterFoyerAUniversite_ByIdAndNom() {
        Foyer foyer = new Foyer();
        Universite universite = new Universite();
        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));
        when(universiteRepository.findByNomUniversite("Uni")).thenReturn(universite);
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite result = foyerService.affecterFoyerAUniversite(1L, "Uni");

        assertThat(result).isEqualTo(universite);
        assertThat(universite.getFoyer()).isEqualTo(foyer);
        verify(foyerRepository).findById(1L);
        verify(universiteRepository).findByNomUniversite("Uni");
        verify(universiteRepository).save(universite);
    }

    @Test
    void testAjouterFoyerEtAffecterAUniversite() {
        Bloc bloc1 = new Bloc();
        Bloc bloc2 = new Bloc();
        Foyer foyer = new Foyer();
        foyer.setBlocs(List.of(bloc1, bloc2));
        Universite universite = new Universite();

        when(foyerRepository.save(foyer)).thenReturn(foyer);
        when(universiteRepository.findById(2L)).thenReturn(Optional.of(universite));
        when(blocRepository.save(any(Bloc.class))).thenReturn(new Bloc());
        when(universiteRepository.save(universite)).thenReturn(universite);

        Foyer result = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, 2L);

        assertThat(result).isEqualTo(foyer);
        verify(foyerRepository).save(foyer);
        verify(universiteRepository).findById(2L);
        verify(blocRepository, times(2)).save(any(Bloc.class));
        verify(universiteRepository).save(universite);
        assertThat(universite.getFoyer()).isEqualTo(foyer);
    }

    @Test
    void testAjoutFoyerEtBlocs() {
        Bloc bloc1 = new Bloc();
        Bloc bloc2 = new Bloc();
        Foyer foyer = new Foyer();
        foyer.setBlocs(List.of(bloc1, bloc2));

        when(foyerRepository.save(foyer)).thenReturn(foyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(new Bloc());

        Foyer result = foyerService.ajoutFoyerEtBlocs(foyer);

        assertThat(result).isEqualTo(foyer);
        verify(foyerRepository).save(foyer);
        verify(blocRepository, times(2)).save(any(Bloc.class));
    }

    @Test
    void testAffecterFoyerAUniversite_ByIds() {
        Universite universite = new Universite();
        Foyer foyer = new Foyer();

        when(universiteRepository.findById(2L)).thenReturn(Optional.of(universite));
        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite result = foyerService.affecterFoyerAUniversite(1L, 2L); // Make sure this uses correct overload

        assertThat(result).isEqualTo(universite);
        assertThat(universite.getFoyer()).isEqualTo(foyer);
    }
//    void testAffecterFoyerAUniversite_ByIds() {
//        Universite universite = new Universite();
//        Foyer foyer = new Foyer();
//
//        when(universiteRepository.findById(2L)).thenReturn(Optional.of(universite));
//        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));
//        when(universiteRepository.save(universite)).thenReturn(universite);
//
//        Universite result = foyerService.affecterFoyerAUniversite(1L, 2L);
//
//        assertThat(result).isEqualTo(universite);
//        assertThat(universite.getFoyer()).isEqualTo(foyer);
//
//        verify(universiteRepository).findById(2L);
//        verify(foyerRepository).findById(1L);
//        verify(universiteRepository).save(universite);
//    }

    @Test
    void testDesaffecterFoyerAUniversite() {
        Universite universite = new Universite();
        when(universiteRepository.findById(2L)).thenReturn(Optional.of(universite));
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite result = foyerService.desaffecterFoyerAUniversite(2L);

        assertThat(result).isEqualTo(universite);
        assertThat(universite.getFoyer()).isNull();
        verify(universiteRepository).findById(2L);
        verify(universiteRepository).save(universite);
    }
}
