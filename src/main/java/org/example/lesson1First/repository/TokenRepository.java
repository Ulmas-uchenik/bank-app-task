package org.example.lesson1First.repository;

import jakarta.ws.rs.PathParam;
import org.example.lesson1First.entity.db.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    @Query(value = """
            select t from TokenEntity t
            where t.user.email = :email and t.expired = false and t.revoked = false
            """)
    List<TokenEntity> findAllAvailableUserToken(@PathParam(value = "email") String email);

    Optional<TokenEntity> findByToken(String token);

    @Modifying
    @Query(value = """
            delete from TokenEntity t
            where t.expired = true or t.revoked = true
            """)
    void deleteAllByExpiredTrueOrRevokedTrue();
}
