package com.courtbooking.batmition.service;

import com.courtbooking.batmition.model.Court;
import com.courtbooking.batmition.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourtService {
    private final CourtRepository courtRepository;

    public Court createCourt(Court court) {
        return courtRepository.save(court);
    }

    public List<Court> getAllCourts() {
        return courtRepository.findAll();
    }

    public List<Court> getAvailableCourts() {
        return courtRepository.findByIsAvailable(true);
    }

    public Court getCourtById(String id) {
        return courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));
    }

    public Court updateCourt(String id, Court courtDetails) {
        Court court = getCourtById(id);
        court.setName(courtDetails.getName());
        court.setLocation(courtDetails.getLocation());
        court.setAvailable(courtDetails.isAvailable());
        court.setPricePerHour(courtDetails.getPricePerHour());
        return courtRepository.save(court);
    }

    public void deleteCourt(String id) {
        courtRepository.deleteById(id);
    }
}
