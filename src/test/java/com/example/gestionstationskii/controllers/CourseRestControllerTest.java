package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.entities.Support;
import com.example.gestionstationskii.entities.TypeCourse;
import com.example.gestionstationskii.services.ICourseServices;
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
class CourseRestControllerTest {
    @Mock private ICourseServices courseServices;
    @InjectMocks private CourseRestController controller;
    private MockMvc mvc;
    private ObjectMapper mapper;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test void addCourse() throws Exception {
        Course course = new Course();
        course.setLevel(2);
        course.setPrice(80F);
        when(courseServices.addCourse(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        mvc.perform(post("/course/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(2));
        verify(courseServices).addCourse(any(Course.class));
    }

    @Test void getAllCourses() throws Exception {
        when(courseServices.retrieveAllCourses()).thenReturn(Collections.singletonList(new Course()));
        mvc.perform(get("/course/all")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void updateCourse() throws Exception {
        Course course = new Course();
        course.setLevel(4);
        when(courseServices.updateCourse(any(Course.class))).thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/course/update").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.level").value(4));
    }

    @Test void getCourseById() throws Exception {
        Course course = new Course();
        course.setLevel(5);
        when(courseServices.retrieveCourse(9L)).thenReturn(course);
        mvc.perform(get("/course/get/9")).andExpect(status().isOk()).andExpect(jsonPath("$.level").value(5));
    }
}
