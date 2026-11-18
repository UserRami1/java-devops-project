package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.Instructor;
import com.example.gestionstationskii.services.IInstructorServices;
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
class InstructorRestControllerTest {
    @Mock private IInstructorServices instructorServices;
    @InjectMocks private InstructorRestController controller;
    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test void addInstructor() throws Exception {
        Instructor instructor = new Instructor();
        instructor.setFirstName("Ahmed");
        when(instructorServices.addInstructor(any(Instructor.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(post("/instructor/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(instructor)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Ahmed"));
    }

    @Test void addInstructorAndAssignToCourse() throws Exception {
        Instructor instructor = new Instructor();
        when(instructorServices.addInstructorAndAssignToCourse(any(Instructor.class), eq(3L)))
                .thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/instructor/addAndAssignToCourse/3").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(instructor)))
                .andExpect(status().isOk());
    }

    @Test void getAllInstructors() throws Exception {
        when(instructorServices.retrieveAllInstructors()).thenReturn(Collections.singletonList(new Instructor()));
        mvc.perform(get("/instructor/all")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void updateInstructor() throws Exception {
        Instructor instructor = new Instructor();
        instructor.setLastName("Updated");
        when(instructorServices.updateInstructor(any(Instructor.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/instructor/update").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(instructor)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test void getInstructorById() throws Exception {
        when(instructorServices.retrieveInstructor(4L)).thenReturn(new Instructor());
        mvc.perform(get("/instructor/get/4")).andExpect(status().isOk());
    }
}
