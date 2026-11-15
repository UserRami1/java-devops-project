package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServicesImplTest {
    @Mock private IRegistrationRepository registrationRepository;
    @Mock private ISkierRepository skierRepository;
    @Mock private ICourseRepository courseRepository;
    @InjectMocks private RegistrationServicesImpl registrationServices;

    @Test void addRegistrationAndAssignToSkier() {
        Registration registration = new Registration();
        Skier skier = new Skier();
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(registrationRepository.save(registration)).thenReturn(registration);

        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        assertSame(skier, result.getSkier());
        verify(registrationRepository).save(registration);
    }

    @Test void assignRegistrationToCourse() {
        Registration registration = new Registration();
        Course course = new Course();
        when(registrationRepository.findById(2L)).thenReturn(Optional.of(registration));
        when(courseRepository.findById(3L)).thenReturn(Optional.of(course));
        when(registrationRepository.save(registration)).thenReturn(registration);

        Registration result = registrationServices.assignRegistrationToCourse(2L, 3L);

        assertSame(course, result.getCourse());
        verify(registrationRepository).save(registration);
    }

    @Test void addAndAssignIndividualCourse() {
        Registration registration = new Registration();
        registration.setNumWeek(4);
        Skier skier = new Skier();
        skier.setNumSkier(10L);
        skier.setDateOfBirth(LocalDate.now().minusYears(20));
        Course course = new Course();
        course.setNumCourse(20L);
        course.setTypeCourse(TypeCourse.INDIVIDUAL);

        when(skierRepository.findById(10L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(4, 10L, 20L)).thenReturn(0L);
        when(registrationRepository.save(registration)).thenReturn(registration);

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 10L, 20L);

        assertSame(skier, result.getSkier());
        assertSame(course, result.getCourse());
        verify(registrationRepository).save(registration);
    }

    @Test void addAndAssignChildToChildrenCourse() {
        Registration registration = new Registration();
        registration.setNumWeek(2);
        Skier child = new Skier();
        child.setNumSkier(1L);
        child.setDateOfBirth(LocalDate.now().minusYears(12));
        Course course = new Course();
        course.setNumCourse(2L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(child));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(2, 1L, 2L)).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(course, 2)).thenReturn(1L);
        when(registrationRepository.save(registration)).thenReturn(registration);

        assertNotNull(registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 2L));
    }

    @Test void rejectsDuplicateRegistration() {
        Registration registration = new Registration();
        registration.setNumWeek(3);
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        Course course = new Course();
        course.setNumCourse(2L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(3, 1L, 2L)).thenReturn(1L);

        assertNull(registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 2L));
        verify(registrationRepository, never()).save(any());
    }

    @Test void returnsNullWhenSkierOrCourseDoesNotExist() {
        Registration registration = new Registration();
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());
        when(courseRepository.findById(2L)).thenReturn(Optional.of(new Course()));

        assertNull(registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 2L));
        verify(registrationRepository, never()).save(any());
    }

    @Test void rejectsChildForAdultCourse() {
        Registration registration = new Registration();
        registration.setNumWeek(1);
        Skier child = new Skier();
        child.setNumSkier(1L);
        child.setDateOfBirth(LocalDate.now().minusYears(10));
        Course course = new Course();
        course.setNumCourse(2L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(child));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(1, 1L, 2L)).thenReturn(0L);

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 2L);

        assertSame(registration, result);
        verify(registrationRepository, never()).save(any());
    }

    @Test void rejectsRegistrationWhenCollectiveCourseIsFull() {
        Registration registration = new Registration();
        registration.setNumWeek(1);
        Skier adult = new Skier();
        adult.setNumSkier(1L);
        adult.setDateOfBirth(LocalDate.now().minusYears(25));
        Course course = new Course();
        course.setNumCourse(2L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(adult));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(1, 1L, 2L)).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(course, 1)).thenReturn(6L);

        assertNull(registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 2L));
        verify(registrationRepository, never()).save(any());
    }

    @Test void numWeeksCourseOfInstructorBySupport() {
        List<Integer> weeks = Arrays.asList(1, 3, 5);
        when(registrationRepository.numWeeksCourseOfInstructorBySupport(4L, Support.SKI)).thenReturn(weeks);
        assertEquals(weeks, registrationServices.numWeeksCourseOfInstructorBySupport(4L, Support.SKI));
    }
}
