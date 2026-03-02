package org.example.lesson1First.repository;

import org.example.lesson1First.entity.db.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            update bank_accounts t
                        set user_id = t.user_id || 'REMOVED',
                        number = t.number || 'REMOVED'
                        where number = :NUMBER
            """)
    void changeUserIdOnRemoved(@Param("NUMBER") String number);
}
