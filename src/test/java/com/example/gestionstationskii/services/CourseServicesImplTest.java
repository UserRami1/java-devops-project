package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.repositories.ICourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServicesImplTest {
    @Mock private ICourseRepository courseRepository;
    @InjectMocks private CourseServicesImpl courseServices;

    @Test void addCourse() {
        Course course = new Course();
        course.setLevel(2);
        course.setPrice(100F);
        when(courseRepository.save(course)).thenReturn(course);
        Course result = courseServices.addCourse(course);
        assertSame(course, result);
        assertEquals(2, result.getLevel());
        verify(courseRepository).save(course);
    }

    @Test void retrieveAllCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(new Course(), new Course()));
        assertEquals(2, courseServices.retrieveAllCourses().size());
        verify(courseRepository).findAll();
    }

    @Test void retrieveAllCoursesWhenEmpty() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(courseServices.retrieveAllCourses().isEmpty());
    }

    @Test void updateCourse() {
        Course course = new Course();
        course.setLevel(3);
        when(courseRepository.save(course)).thenReturn(course);
        assertSame(course, courseServices.updateCourse(course));
        verify(courseRepository).save(course);
    }

    @Test void retrieveCourseFound() {
        Course course = new Course();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        assertSame(course, courseServices.retrieveCourse(1L));
    }

    @Test void retrieveCourseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(courseServices.retrieveCourse(99L));
    }
}
