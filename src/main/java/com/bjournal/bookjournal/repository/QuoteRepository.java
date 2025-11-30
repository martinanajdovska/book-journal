package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findAllByUserUsernameAndBookId(String username, Long bookId);
    List<Quote> findAllByUserUsername(String username);
    // convert text from CLOB(oid) to binary(bytea) and then to text type before comparison
    @Query(value = "SELECT q.* from Quote q join journal_user u on q.user_id=u.id where u.username = ?1 and convert_from(lo_get(q.text), 'UTF-8') ilike concat('%',?2,'%')", nativeQuery = true)
    List<Quote> findAllByUserUsernameAndTextContainingIgnoreCase(String username, String text);
}
