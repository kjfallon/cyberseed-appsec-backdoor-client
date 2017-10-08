package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.DiagnosisRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRecordRepository extends JpaRepository<DiagnosisRecord, Long> {
    DiagnosisRecord findByDoctor(String doctor);
    DiagnosisRecord findById(Integer record_id);
}
