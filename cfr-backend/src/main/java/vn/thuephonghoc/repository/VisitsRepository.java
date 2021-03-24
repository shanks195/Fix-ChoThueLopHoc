package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.thuephonghoc.entity.Visits;

import java.util.List;

@Repository
public interface VisitsRepository extends JpaRepository<Visits,Long> {

    /**
     * findByDate
     * @param date
     * @return Visits
     */
    Visits findByDate(String date);

    /**
     * @param date1
     * @param date2
     * @return List
     */
    @Query(value = "select * FROM visits where create_time between ?1 and ?2",nativeQuery = true)
    List<Visits> findAllVisits(String date1, String date2);
}
