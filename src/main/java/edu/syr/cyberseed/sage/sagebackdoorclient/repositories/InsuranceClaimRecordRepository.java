package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.InsuranceClaimRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceClaimRecordRepository extends JpaRepository<InsuranceClaimRecord, Long> {
    InsuranceClaimRecord findById(Integer id);
}
