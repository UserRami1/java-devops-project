package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.Piste;
import com.example.gestionstationskii.repositories.IPisteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PisteServicesImplTest {
    @Mock private IPisteRepository pisteRepository;
    @InjectMocks private PisteServicesImpl pisteServices;

    @Test void addPiste() {
        Piste piste = new Piste();
        piste.setNamePiste("Blue Run");
        when(pisteRepository.save(piste)).thenReturn(piste);
        assertSame(piste, pisteServices.addPiste(piste));
        verify(pisteRepository).save(piste);
    }

    @Test void retrieveAllPistes() {
        when(pisteRepository.findAll()).thenReturn(Arrays.asList(new Piste(), new Piste()));
        assertEquals(2, pisteServices.retrieveAllPistes().size());
    }

    @Test void retrieveAllPistesWhenEmpty() {
        when(pisteRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(pisteServices.retrieveAllPistes().isEmpty());
    }

    @Test void retrievePisteFound() {
        Piste piste = new Piste();
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        assertSame(piste, pisteServices.retrievePiste(1L));
    }

    @Test void retrievePisteNotFound() {
        when(pisteRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(pisteServices.retrievePiste(99L));
    }

    @Test void removePiste() {
        doNothing().when(pisteRepository).deleteById(5L);
        pisteServices.removePiste(5L);
        verify(pisteRepository).deleteById(5L);
    }
}
