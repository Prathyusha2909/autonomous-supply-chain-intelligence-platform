package com.autonomous.supplychain.shipment.repository;

import com.autonomous.supplychain.shipment.domain.ShipmentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEventEntity, UUID> {
    List<ShipmentEventEntity> findByShipmentIdOrderByOccurredAtDesc(String shipmentId);
}
