package de.evaspringbuch.eva11mobpay.usermobpay.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

//@RepositoryRestResource
public interface PayUserRepository extends JpaRepository<PayUser, Long> {
    Optional<PayUser> findByName(String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM PayUser c WHERE c.name = ?1 and c.state = 'AVAILABLE'")
    boolean existsByNameAndAvailable(String name);

}
