package com.onlinePharmacy.admin.repository;

import com.onlinePharmacy.admin.entity.SalesSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesSnapshotRepository extends JpaRepository<SalesSnapshot, Long> {
    Optional<SalesSnapshot> findBySnapshotDate(LocalDate date);

    List<SalesSnapshot> findBySnapshotDateBetweenOrderBySnapshotDateAsc(
            LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(s.totalRevenue), 0) FROM SalesSnapshot s " +
           "WHERE s.snapshotDate BETWEEN :from AND :to")
    Double sumRevenueBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(s.totalOrders), 0) FROM SalesSnapshot s " +
           "WHERE s.snapshotDate BETWEEN :from AND :to")
    Long sumOrdersBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}