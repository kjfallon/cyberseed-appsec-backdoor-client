package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;


import edu.syr.cyberseed.sage.sagebackdoorclient.entities.UserPermissions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionsRepository extends JpaRepository<UserPermissions, Long> {
    UserPermissions findByRole(String role);
}
