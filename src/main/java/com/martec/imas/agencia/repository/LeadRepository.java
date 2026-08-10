package com.martec.imas.agencia.repository;

import com.martec.imas.agencia.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findAllByOrderByCreatedAtDesc();
    List<Lead> findBySourceOrderByCreatedAtDesc(String source);
}
