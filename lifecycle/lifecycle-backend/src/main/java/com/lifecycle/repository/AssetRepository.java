package com.lifecycle.repository;
import com.lifecycle.entity.Asset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AssetRepository extends JpaRepository<Asset, Long> { List<Asset> findByUserId(Long userId); }
