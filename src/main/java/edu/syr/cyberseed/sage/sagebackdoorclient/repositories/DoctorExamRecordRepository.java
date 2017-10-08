package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.DoctorExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorExamRecordRepository extends JpaRepository<DoctorExamRecord, Long> {
    DoctorExamRecord findByDoctor(String doctor);
    DoctorExamRecord findById(Integer record_id);
}
