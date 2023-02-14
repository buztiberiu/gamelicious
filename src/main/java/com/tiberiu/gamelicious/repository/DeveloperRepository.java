package com.tiberiu.gamelicious.repository;

import com.tiberiu.gamelicious.model.Developer;
import com.tiberiu.gamelicious.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    @Query("SELECT s FROM Developer s WHERE s.name = ?1")
    Optional<Developer> findDeveloperByName(String name);

    @Query("SELECT s FROM Developer s WHERE s.email = ?1")
    Optional<Publisher> findPublisherByEmail(String email);
}
