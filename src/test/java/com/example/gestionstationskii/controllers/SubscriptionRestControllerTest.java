package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.Subscription;
import com.example.gestionstationskii.entities.TypeSubscription;
import com.example.gestionstationskii.services.ISubscriptionServices;
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
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionRestControllerTest {
    @Mock private ISubscriptionServices subscriptionServices;
    @InjectMocks private SubscriptionRestController controller;
    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test void addSubscription() throws Exception {
        Subscription sub = new Subscription();
        sub.setPrice(150F);
        when(subscriptionServices.addSubscription(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(post("/subscription/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sub)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.price").value(150.0));
    }

    @Test void getSubscriptionById() throws Exception {
        when(subscriptionServices.retrieveSubscriptionById(1L)).thenReturn(new Subscription());
        mvc.perform(get("/subscription/get/1")).andExpect(status().isOk());
    }

    @Test void getSubscriptionsByType() throws Exception {
        Set<Subscription> subs = new HashSet<>();
        subs.add(new Subscription());
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY)).thenReturn(subs);
        mvc.perform(get("/subscription/all/MONTHLY"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void updateSubscription() throws Exception {
        Subscription sub = new Subscription();
        sub.setPrice(200F);
        when(subscriptionServices.updateSubscription(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/subscription/update").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sub)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.price").value(200.0));
    }

    @Test void getSubscriptionsByDates() throws Exception {
        when(subscriptionServices.retrieveSubscriptionsByDates(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)))
                .thenReturn(Collections.singletonList(new Subscription()));
        mvc.perform(get("/subscription/all/2026-01-01/2026-12-31"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
}
