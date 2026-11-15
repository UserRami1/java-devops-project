package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.Registration;
import com.example.gestionstationskii.entities.Support;
import com.example.gestionstationskii.services.IRegistrationServices;
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
import java.util.Arrays;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RegistrationRestControllerTest {
    @Mock private IRegistrationServices registrationServices;
    @InjectMocks private RegistrationRestController controller;
    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test void addAndAssignToSkier() throws Exception {
        Registration registration = new Registration();
        registration.setNumWeek(2);
        when(registrationServices.addRegistrationAndAssignToSkier(any(Registration.class), eq(1L)))
                .thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/registration/addAndAssignToSkier/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registration)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.numWeek").value(2));
    }

    @Test void assignRegistrationToCourse() throws Exception {
        when(registrationServices.assignRegistrationToCourse(2L, 3L)).thenReturn(new Registration());
        mvc.perform(put("/registration/assignToCourse/2/3")).andExpect(status().isOk());
    }

    @Test void addAndAssignToSkierAndCourse() throws Exception {
        Registration registration = new Registration();
        when(registrationServices.addRegistrationAndAssignToSkierAndCourse(any(Registration.class), eq(1L), eq(4L)))
                .thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/registration/addAndAssignToSkierAndCourse/1/4")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(registration)))
                .andExpect(status().isOk());
    }

    @Test void numWeeksByInstructorAndSupport() throws Exception {
        when(registrationServices.numWeeksCourseOfInstructorBySupport(5L, Support.SKI))
                .thenReturn(Arrays.asList(1, 2, 4));
        mvc.perform(get("/registration/numWeeks/5/SKI"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
    }
}
