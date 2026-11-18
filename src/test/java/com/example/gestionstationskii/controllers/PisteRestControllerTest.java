package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.Piste;
import com.example.gestionstationskii.services.IPisteServices;
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
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PisteRestControllerTest {
    @Mock private IPisteServices pisteServices;
    @InjectMocks private PisteRestController controller;
    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test void addPiste() throws Exception {
        Piste piste = new Piste();
        piste.setNamePiste("Blue");
        when(pisteServices.addPiste(any(Piste.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(post("/piste/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(piste)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.namePiste").value("Blue"));
    }

    @Test void getAllPistes() throws Exception {
        when(pisteServices.retrieveAllPistes()).thenReturn(Collections.singletonList(new Piste()));
        mvc.perform(get("/piste/all")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void getPisteById() throws Exception {
        when(pisteServices.retrievePiste(2L)).thenReturn(new Piste());
        mvc.perform(get("/piste/get/2")).andExpect(status().isOk());
    }

    @Test void deletePiste() throws Exception {
        doNothing().when(pisteServices).removePiste(2L);
        mvc.perform(delete("/piste/delete/2")).andExpect(status().isOk()).andExpect(content().string(""));
        verify(pisteServices).removePiste(2L);
    }
}
