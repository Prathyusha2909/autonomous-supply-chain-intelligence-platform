package com.autonomous.supplychain.shipment.repository;

import com.autonomous.supplychain.common.model.ShipmentStatus;
import com.autonomous.supplychain.shipment.domain.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, String> {
    List<ShipmentEntity> findTop50ByOrderByUpdatedAtDesc();

    List<ShipmentEntity> findByStatusInOrderByUpdatedAtDesc(Collection<ShipmentStatus> statuses);
}
