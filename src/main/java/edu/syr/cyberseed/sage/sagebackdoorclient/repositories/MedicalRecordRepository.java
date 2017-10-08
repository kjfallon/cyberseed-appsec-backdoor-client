package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>{

    List<MedicalRecord> findByOwner(String owner);
    List<MedicalRecord> findByPatient(String patient);
    MedicalRecord findById(Integer id);
    List<MedicalRecord> findByViewContaining(String view);
    List<MedicalRecord> findByEditContaining(String edit);
}
