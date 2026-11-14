package com.lifecycle.repository;
import com.lifecycle.entity.Warranty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    List<Warranty> findByAssetId(Long assetId);
    void deleteByAssetId(Long assetId);
}
