package org.example.lesson1First.repository;

import jakarta.data.repository.CrudRepository;
import org.example.lesson1First.entity.db.Transaction;
import org.example.lesson1First.entity.db.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
}
