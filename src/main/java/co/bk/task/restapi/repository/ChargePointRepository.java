package co.bk.task.restapi.repository;

import co.bk.task.restapi.model.ChargePoint;
import co.bk.task.restapi.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargePointRepository extends JpaRepository<ChargePoint, Long> {

}
