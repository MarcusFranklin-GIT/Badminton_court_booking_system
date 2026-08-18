package com.courtbooking.batmition.repository;

import com.courtbooking.batmition.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByCourtId(String courtId);
    List<Booking> findByCustomerEmail(String email);
    List<Booking> findByCourtIdAndStartTimeBetween(String courtId, LocalDateTime start, LocalDateTime end);
}
