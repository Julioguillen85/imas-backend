package com.martec.imas.agencia.repository;

import com.martec.imas.agencia.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByIsActiveTrueOrderByDisplayOrderAsc();

    List<Service> findByCategoryIdAndIsActiveTrueOrderByDisplayOrderAsc(Long categoryId);
}