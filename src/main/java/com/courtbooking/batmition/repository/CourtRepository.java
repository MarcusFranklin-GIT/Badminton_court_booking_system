package com.courtbooking.batmition.repository;

import com.courtbooking.batmition.model.Court;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends MongoRepository<Court, String> {
    List<Court> findByIsAvailable(boolean isAvailable);
}
