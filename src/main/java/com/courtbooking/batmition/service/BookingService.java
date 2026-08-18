package com.courtbooking.batmition.service;

import com.courtbooking.batmition.model.Booking;
import com.courtbooking.batmition.model.Court;
import com.courtbooking.batmition.repository.BookingRepository;
import com.courtbooking.batmition.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;

    public Booking createBooking(Booking booking) {
        // Check if court exists
        Court court = courtRepository.findById(booking.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        // Check if court is available
        if (!court.isAvailable()) {
            throw new RuntimeException("Court is not available");
        }

        // Check for time slot conflicts
        List<Booking> conflictingBookings = bookingRepository
                .findByCourtIdAndStartTimeBetween(
                        booking.getCourtId(),
                        booking.getStartTime().minusHours(2),
                        booking.getEndTime().plusHours(2)
                );

        boolean hasConflict = conflictingBookings.stream()
                .anyMatch(existingBooking ->
                        !existingBooking.getStatus().equals(Booking.BookingStatus.CANCELLED) &&
                        isTimeOverlapping(existingBooking, booking)
                );

        if (hasConflict) {
            throw new RuntimeException("Time slot is already booked");
        }

        // Calculate price
        long hours = Duration.between(booking.getStartTime(), booking.getEndTime()).toHours();
        booking.setTotalPrice(hours * court.getPricePerHour());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    private boolean isTimeOverlapping(Booking existing, Booking newBooking) {
        return newBooking.getStartTime().isBefore(existing.getEndTime()) &&
               newBooking.getEndTime().isAfter(existing.getStartTime());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getBookingsByCourtId(String courtId) {
        return bookingRepository.findByCourtId(courtId);
    }

    public Booking cancelBooking(String id) {
        Booking booking = getBookingById(id);
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }
}
