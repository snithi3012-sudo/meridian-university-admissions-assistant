package com.university.admissions.service;

import com.university.admissions.model.Admission;
import com.university.admissions.repository.AdmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdmissionService {

    private final AdmissionRepository admissionRepository;

    @Autowired
    public AdmissionService(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    public Admission createAdmissionInfo(Admission admission) {
        return admissionRepository.save(admission);
    }

    public List<Admission> getAllAdmissionInfo() {
        return admissionRepository.findAll();
    }

    public List<Admission> getByCategory(String category) {
        List<Admission> results = admissionRepository.findByCategoryIgnoreCase(category);
        if (results.isEmpty()) {
            throw new NoSuchElementException("No admission information found for category: " + category);
        }
        return results;
    }

    public Admission updateAdmissionInfo(String id, Admission updated) {
        Admission existing = admissionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admission info not found with id: " + id));

        existing.setCategory(updated.getCategory());
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());

        return admissionRepository.save(existing);
    }

    public void deleteAdmissionInfo(String id) {
        if (!admissionRepository.existsById(id)) {
            throw new NoSuchElementException("Admission info not found with id: " + id);
        }
        admissionRepository.deleteById(id);
    }
}
