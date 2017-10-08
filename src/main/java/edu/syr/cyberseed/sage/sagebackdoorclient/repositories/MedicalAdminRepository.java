package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.Medical_admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalAdminRepository extends JpaRepository<Medical_admin, Long> {
    Medical_admin findByUsername(String username);
}
