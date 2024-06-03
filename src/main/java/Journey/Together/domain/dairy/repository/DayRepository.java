package Journey.Together.domain.dairy.repository;

import Journey.Together.domain.dairy.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayRepository extends JpaRepository<Day,Long> {
}
