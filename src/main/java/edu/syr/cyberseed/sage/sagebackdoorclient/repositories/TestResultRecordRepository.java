package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.TestResultRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultRecordRepository extends JpaRepository<TestResultRecord, Long> {
    TestResultRecord findByDoctor(String doctor);
    TestResultRecord findById(Integer record_id);
}
