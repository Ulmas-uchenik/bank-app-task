package org.example.lesson1First.repository;

import jakarta.ws.rs.PathParam;
import org.example.lesson1First.entity.db.Transaction;
import org.example.lesson1First.enums.TypeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query(value = """
            SELECT t FROM Transaction t WHERE t.sourceBankAccount.id = :bankAccountId
            OR t.targetBankAccount.id = :bankAccountId
            """)
    Page<Transaction> findAllBy(@PathParam("bankAccountId") String bankAccountId, Pageable pageable);

    @Query(value = """
            SELECT t FROM Transaction t WHERE (t.sourceBankAccount.id = :bankAccountId
            OR t.targetBankAccount.id = :bankAccountId) and t.type = :type
            """)
    Page<Transaction> findAllBy(@PathParam("bankAccountId") String bankAccountId, @PathParam("typeT") TypeTransaction type, Pageable pageable);
}
