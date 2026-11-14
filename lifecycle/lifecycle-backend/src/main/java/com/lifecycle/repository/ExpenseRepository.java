package com.lifecycle.repository;
import com.lifecycle.entity.Expense;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByAssetIdOrderByExpenseDateDesc(Long assetId);
    void deleteByAssetId(Long assetId);
}
