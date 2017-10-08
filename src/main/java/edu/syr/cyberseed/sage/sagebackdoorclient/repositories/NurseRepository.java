package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;

import edu.syr.cyberseed.sage.sagebackdoorclient.entities.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
    Nurse findByUsername(String username);
}
