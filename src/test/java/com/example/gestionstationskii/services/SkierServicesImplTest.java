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
class SkierServicesImplTest {
    @Mock private ISkierRepository skierRepository;
    @Mock private IPisteRepository pisteRepository;
    @Mock private ICourseRepository courseRepository;
    @Mock private IRegistrationRepository registrationRepository;
    @Mock private ISubscriptionRepository subscriptionRepository;
    @InjectMocks private SkierServicesImpl skierServices;

    @Test void retrieveAllSkiers() {
        when(skierRepository.findAll()).thenReturn(Arrays.asList(new Skier(), new Skier()));
        assertEquals(2, skierServices.retrieveAllSkiers().size());
    }

    @Test void addSkierAnnualSubscriptionSetsEndDate() {
        Skier skier = new Skier();
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.of(2026, 1, 1));
        sub.setTypeSub(TypeSubscription.ANNUAL);
        skier.setSubscription(sub);
        when(skierRepository.save(skier)).thenReturn(skier);

        Skier result = skierServices.addSkier(skier);

        assertEquals(LocalDate.of(2027, 1, 1), result.getSubscription().getEndDate());
        verify(skierRepository).save(skier);
    }

    @Test void addSkierMonthlySubscriptionSetsEndDate() {
        Skier skier = new Skier();
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.of(2026, 1, 15));
        sub.setTypeSub(TypeSubscription.MONTHLY);
        skier.setSubscription(sub);
        when(skierRepository.save(skier)).thenReturn(skier);

        skierServices.addSkier(skier);

        assertEquals(LocalDate.of(2026, 2, 15), sub.getEndDate());
    }

    @Test void addSkierSemesterSubscriptionSetsEndDate() {
        Skier skier = new Skier();
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.of(2026, 1, 15));
        sub.setTypeSub(TypeSubscription.SEMESTRIEL);
        skier.setSubscription(sub);
        when(skierRepository.save(skier)).thenReturn(skier);

        skierServices.addSkier(skier);

        assertEquals(LocalDate.of(2026, 7, 15), sub.getEndDate());
    }

    @Test void retrieveSkierFound() {
        Skier skier = new Skier();
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        assertSame(skier, skierServices.retrieveSkier(1L));
    }

    @Test void retrieveSkierNotFound() {
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(skierServices.retrieveSkier(1L));
    }

    @Test void removeSkier() {
        skierServices.removeSkier(1L);
        verify(skierRepository).deleteById(1L);
    }

    @Test void assignSkierToSubscription() {
        Skier skier = new Skier();
        Subscription sub = new Subscription();
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(2L)).thenReturn(Optional.of(sub));
        when(skierRepository.save(skier)).thenReturn(skier);

        assertSame(sub, skierServices.assignSkierToSubscription(1L, 2L).getSubscription());
    }

    @Test void assignSkierToPisteInitializesPisteSet() {
        Skier skier = new Skier();
        Piste piste = new Piste();
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(2L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(skier)).thenReturn(skier);

        Skier result = skierServices.assignSkierToPiste(1L, 2L);

        assertNotNull(result.getPistes());
        assertTrue(result.getPistes().contains(piste));
    }

    @Test void assignSkierToPisteAddsToExistingSet() {
        Skier skier = new Skier();
        Set<Piste> pistes = new HashSet<>();
        skier.setPistes(pistes);
        Piste piste = new Piste();
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(2L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(skier)).thenReturn(skier);

        skierServices.assignSkierToPiste(1L, 2L);

        assertTrue(pistes.contains(piste));
    }

    @Test void retrieveSkiersBySubscriptionType() {
        List<Skier> expected = Collections.singletonList(new Skier());
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.MONTHLY)).thenReturn(expected);
        assertEquals(expected, skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.MONTHLY));
    }

    @Test void addSkierAndAssignToCourse() {
        Skier skier = new Skier();
        Registration registration = new Registration();
        Set<Registration> registrations = new HashSet<>();
        registrations.add(registration);
        skier.setRegistrations(registrations);
        Course course = new Course();
        when(skierRepository.save(skier)).thenReturn(skier);
        when(courseRepository.getById(8L)).thenReturn(course);

        Skier result = skierServices.addSkierAndAssignToCourse(skier, 8L);

        assertSame(skier, result);
        assertSame(skier, registration.getSkier());
        assertSame(course, registration.getCourse());
        verify(registrationRepository).save(registration);
    }
}
