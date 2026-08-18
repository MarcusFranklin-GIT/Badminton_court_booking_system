package com.courtbooking.batmition.controller;

import com.courtbooking.batmition.model.Court;
import com.courtbooking.batmition.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {
    private final CourtService courtService;

    @PostMapping
    public ResponseEntity<Court> createCourt(@RequestBody Court court) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courtService.createCourt(court));
    }

    @GetMapping
    public ResponseEntity<List<Court>> getAllCourts() {
        return ResponseEntity.ok(courtService.getAllCourts());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Court>> getAvailableCourts() {
        return ResponseEntity.ok(courtService.getAvailableCourts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Court> getCourtById(@PathVariable String id) {
        return ResponseEntity.ok(courtService.getCourtById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Court> updateCourt(@PathVariable String id, @RequestBody Court court) {
        return ResponseEntity.ok(courtService.updateCourt(id, court));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourt(@PathVariable String id) {
        courtService.deleteCourt(id);
        return ResponseEntity.noContent().build();
    }
}
