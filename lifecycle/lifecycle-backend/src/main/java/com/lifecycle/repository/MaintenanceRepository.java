package com.lifecycle.repository;
import com.lifecycle.entity.MaintenanceSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MaintenanceRepository extends JpaRepository<MaintenanceSchedule, Long> {
    List<MaintenanceSchedule> findByAssetId(Long assetId);
    void deleteByAssetId(Long assetId);
}
