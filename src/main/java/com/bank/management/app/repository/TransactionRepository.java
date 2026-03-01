package com.bank.management.app.repository;

import com.bank.management.app.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
              SELECT t FROM Transaction t
              JOIN FETCH t.account a
              JOIN FETCH a.customer c
              WHERE a.id = :accountId
              ORDER BY t.transactionDate DESC
            """)
    Page<Transaction> findByAccountIdWithCustomer(
            @Param("accountId") Long accountId,
            Pageable pageable
    );
   Page<Transaction> findByAccountId(Long accountId, Pageable pageable);


}
