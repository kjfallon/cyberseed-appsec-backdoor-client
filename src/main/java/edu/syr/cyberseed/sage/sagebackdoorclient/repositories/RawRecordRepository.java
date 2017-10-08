package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.RawRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawRecordRepository extends JpaRepository<RawRecord, Long> {
    RawRecord findById(Integer record_id);
}
