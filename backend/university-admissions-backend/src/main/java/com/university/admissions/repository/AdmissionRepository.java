package com.university.admissions.repository;

import com.university.admissions.model.Admission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionRepository extends MongoRepository<Admission, String> {

    // Spring Data MongoDB auto-generates the query from this method name -
    // no implementation needed. Lets us fetch everything filed under
    // one category, e.g. all "fees" entries.
    List<Admission> findByCategoryIgnoreCase(String category);
}
