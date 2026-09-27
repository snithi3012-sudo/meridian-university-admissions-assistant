package com.university.admissions.controller;

import com.university.admissions.model.Admission;
import com.university.admissions.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * General admission information - eligibility, fees, deadlines,
 * documents, process, scholarships, hostel, contact, etc.
 * Separate from Course, since this info isn't tied to one specific course.
 *
 * POST   /api/admissions              - add a new piece of admission info
 * GET    /api/admissions              - list everything
 * GET    /api/admissions/{category}   - get all entries for a category, e.g. "eligibility"
 * PUT    /api/admissions/{id}         - update one entry by its id
 * DELETE /api/admissions/{id}         - delete one entry by its id
 */
@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    @Autowired
    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping
    public ResponseEntity<Admission> createAdmissionInfo(@RequestBody Admission admission) {
        Admission saved = admissionService.createAdmissionInfo(admission);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Admission>> getAllAdmissionInfo() {
        return ResponseEntity.ok(admissionService.getAllAdmissionInfo());
    }

    @GetMapping("/{category}")
    public ResponseEntity<?> getByCategory(@PathVariable String category) {
        try {
            return ResponseEntity.ok(admissionService.getByCategory(category));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundBody(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmissionInfo(@PathVariable String id, @RequestBody Admission admission) {
        try {
            return ResponseEntity.ok(admissionService.updateAdmissionInfo(id, admission));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundBody(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmissionInfo(@PathVariable String id) {
        try {
            admissionService.deleteAdmissionInfo(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundBody(ex.getMessage()));
        }
    }

    private Map<String, Object> notFoundBody(String message) {
        return Map.of(
                "success", false,
                "message", message,
                "status", 404
        );
    }
}
