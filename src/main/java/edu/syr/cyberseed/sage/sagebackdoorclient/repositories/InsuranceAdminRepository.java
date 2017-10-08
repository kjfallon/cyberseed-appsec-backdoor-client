package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.Insurance_admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceAdminRepository extends JpaRepository<Insurance_admin, Long> {
    Insurance_admin findByUsername(String username);
}
