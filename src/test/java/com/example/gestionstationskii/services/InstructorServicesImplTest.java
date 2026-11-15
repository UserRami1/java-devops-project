package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.entities.Instructor;
import com.example.gestionstationskii.repositories.ICourseRepository;
import com.example.gestionstationskii.repositories.IInstructorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServicesImplTest {
    @Mock private IInstructorRepository instructorRepository;
    @Mock private ICourseRepository courseRepository;
    @InjectMocks private InstructorServicesImpl instructorServices;

    @Test void addInstructor() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("Ahmed");
        when(instructorRepository.save(instructor)).thenReturn(instructor);
        assertSame(instructor, instructorServices.addInstructor(instructor));
        verify(instructorRepository).save(instructor);
    }

    @Test void retrieveAllInstructors() {
        when(instructorRepository.findAll()).thenReturn(Arrays.asList(new Instructor(), new Instructor()));
        assertEquals(2, instructorServices.retrieveAllInstructors().size());
    }

    @Test void retrieveAllInstructorsWhenEmpty() {
        when(instructorRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(instructorServices.retrieveAllInstructors().isEmpty());
    }

    @Test void updateInstructor() {
        Instructor instructor = new Instructor();
        instructor.setLastName("Updated");
        when(instructorRepository.save(instructor)).thenReturn(instructor);
        assertSame(instructor, instructorServices.updateInstructor(instructor));
        verify(instructorRepository).save(instructor);
    }

    @Test void retrieveInstructorFound() {
        Instructor instructor = new Instructor();
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        assertSame(instructor, instructorServices.retrieveInstructor(1L));
    }

    @Test void retrieveInstructorNotFound() {
        when(instructorRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(instructorServices.retrieveInstructor(99L));
    }

    @Test void addInstructorAndAssignToCourse() {
        Instructor instructor = new Instructor();
        Course course = new Course();
        when(courseRepository.findById(7L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenAnswer(inv -> inv.getArgument(0));

        Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, 7L);

        assertNotNull(result.getCourses());
        assertTrue(result.getCourses().contains(course));
        verify(courseRepository).findById(7L);
        verify(instructorRepository).save(instructor);
    }
}
