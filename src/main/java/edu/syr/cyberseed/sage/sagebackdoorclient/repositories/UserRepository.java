package edu.syr.cyberseed.sage.sagebackdoorclient.repositories;


import edu.syr.cyberseed.sage.sagebackdoorclient.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    void deleteUserByUsername(String username);

}
