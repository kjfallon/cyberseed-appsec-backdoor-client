package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MedicalRecordWithoutAutoIdRepository extends JpaRepository<MedicalRecordWithoutAutoId, Long>{

    List<MedicalRecordWithoutAutoId> findByOwner(String owner);
    List<MedicalRecordWithoutAutoId> findByPatient(String patient);
    MedicalRecordWithoutAutoId findById(Integer id);
    List<MedicalRecordWithoutAutoId> findByViewContaining(String view);
    List<MedicalRecordWithoutAutoId> findByEditContaining(String edit);

}
