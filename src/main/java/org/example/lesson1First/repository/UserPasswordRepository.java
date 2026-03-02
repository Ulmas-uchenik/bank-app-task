package org.example.lesson1First.repository;

import org.example.lesson1First.entity.db.User;
import org.example.lesson1First.entity.db.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, String> {
    Optional<UserPassword> findByEmail(String email);
}
