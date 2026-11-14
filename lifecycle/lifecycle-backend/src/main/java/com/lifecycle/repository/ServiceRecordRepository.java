package com.lifecycle.repository;
import com.lifecycle.entity.ServiceRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByAssetIdOrderByServiceDateDesc(Long assetId);
    void deleteByAssetId(Long assetId);
}
