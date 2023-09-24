package co.bk.task.restapi.repository;

import co.bk.task.restapi.model.ChargeSession;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeSessionListRepository extends ListCrudRepository<ChargeSession, Long> {
    List<ChargeSession> findChargeSessionsByVehicleId(Long vehicleId);
}