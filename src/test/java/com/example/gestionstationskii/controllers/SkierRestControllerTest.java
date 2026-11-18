package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.services.ISkierServices;
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
class SkierRestControllerTest {
    @Mock private ISkierServices skierServices;
    @InjectMocks private SkierRestController controller;
    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test void addSkier() throws Exception {
        Skier skier = new Skier();
        skier.setFirstName("Sami");
        when(skierServices.addSkier(any(Skier.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(post("/skier/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(skier)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Sami"));
    }

    @Test void addSkierAndAssignToCourse() throws Exception {
        Skier skier = new Skier();
        when(skierServices.addSkierAndAssignToCourse(any(Skier.class), eq(7L))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(post("/skier/addAndAssign/7").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(skier))).andExpect(status().isOk());
    }

    @Test void assignToSubscription() throws Exception {
        when(skierServices.assignSkierToSubscription(1L, 2L)).thenReturn(new Skier());
        mvc.perform(put("/skier/assignToSub/1/2")).andExpect(status().isOk());
    }

    @Test void assignToPiste() throws Exception {
        when(skierServices.assignSkierToPiste(1L, 3L)).thenReturn(new Skier());
        mvc.perform(put("/skier/assignToPiste/1/3")).andExpect(status().isOk());
    }

    @Test void retrieveSkiersBySubscriptionType() throws Exception {
        when(skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.MONTHLY))
                .thenReturn(Collections.singletonList(new Skier()));
        mvc.perform(get("/skier/getSkiersBySubscription").param("typeSubscription", "MONTHLY"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void getSkierById() throws Exception {
        when(skierServices.retrieveSkier(1L)).thenReturn(new Skier());
        mvc.perform(get("/skier/get/1")).andExpect(status().isOk());
    }

    @Test void deleteSkier() throws Exception {
        mvc.perform(delete("/skier/delete/1")).andExpect(status().isOk()).andExpect(content().string(""));
        verify(skierServices).removeSkier(1L);
    }

    @Test void getAllSkiers() throws Exception {
        when(skierServices.retrieveAllSkiers()).thenReturn(Collections.singletonList(new Skier()));
        mvc.perform(get("/skier/all")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
}
