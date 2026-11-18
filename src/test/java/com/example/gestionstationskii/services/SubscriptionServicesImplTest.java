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
class SubscriptionServicesImplTest {
    @Mock private ISubscriptionRepository subscriptionRepository;
    @Mock private ISkierRepository skierRepository;
    @InjectMocks private SubscriptionServicesImpl subscriptionServices;

    @Test void addAnnualSubscriptionSetsEndDate() {
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.of(2026, 1, 1));
        sub.setTypeSub(TypeSubscription.ANNUAL);
        when(subscriptionRepository.save(sub)).thenReturn(sub);

        Subscription result = subscriptionServices.addSubscription(sub);

        assertEquals(LocalDate.of(2027, 1, 1), result.getEndDate());
        verify(subscriptionRepository).save(sub);
    }

    @Test void addSemesterSubscriptionSetsEndDate() {
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.of(2026, 1, 1));
        sub.setTypeSub(TypeSubscription.SEMESTRIEL);
        when(subscriptionRepository.save(sub)).thenReturn(sub);
        subscriptionServices.addSubscription(sub);
        assertEquals(LocalDate.of(2026, 7, 1), sub.getEndDate());
    }

    @Test void addMonthlySubscriptionSetsEndDate() {
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.of(2026, 1, 1));
        sub.setTypeSub(TypeSubscription.MONTHLY);
        when(subscriptionRepository.save(sub)).thenReturn(sub);
        subscriptionServices.addSubscription(sub);
        assertEquals(LocalDate.of(2026, 2, 1), sub.getEndDate());
    }

    @Test void updateSubscription() {
        Subscription sub = new Subscription();
        when(subscriptionRepository.save(sub)).thenReturn(sub);
        assertSame(sub, subscriptionServices.updateSubscription(sub));
    }

    @Test void retrieveSubscriptionFound() {
        Subscription sub = new Subscription();
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(sub));
        assertSame(sub, subscriptionServices.retrieveSubscriptionById(1L));
    }

    @Test void retrieveSubscriptionNotFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(subscriptionServices.retrieveSubscriptionById(1L));
    }

    @Test void getSubscriptionByType() {
        Set<Subscription> expected = new HashSet<>();
        expected.add(new Subscription());
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY)).thenReturn(expected);
        assertEquals(expected, subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY));
    }

    @Test void retrieveSubscriptionsByDates() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        List<Subscription> expected = Collections.singletonList(new Subscription());
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(start, end)).thenReturn(expected);
        assertEquals(expected, subscriptionServices.retrieveSubscriptionsByDates(start, end));
    }

    @Test void retrieveSubscriptionsLogsSubscriptionsAndSkier() {
        Subscription sub = new Subscription();
        sub.setNumSub(1L);
        sub.setEndDate(LocalDate.of(2026, 12, 31));
        Skier skier = new Skier();
        skier.setFirstName("Ali");
        skier.setLastName("Ben Salah");
        when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(Collections.singletonList(sub));
        when(skierRepository.findBySubscription(sub)).thenReturn(skier);

        assertDoesNotThrow(() -> subscriptionServices.retrieveSubscriptions());
        verify(skierRepository).findBySubscription(sub);
    }

    @Test void showMonthlyRecurringRevenue() {
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY)).thenReturn(120F);
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL)).thenReturn(600F);
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL)).thenReturn(1200F);

        assertDoesNotThrow(() -> subscriptionServices.showMonthlyRecurringRevenue());
    }
}
