package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Games;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableAspectJAutoProxy
public interface GameRepository extends JpaRepository<Games, Long> {
    @Query("SELECT g FROM Games g where g.availability = true")
    List<Games> findGamesByAvailability();

    List<Games> findGamesByTitle(String title);

    /*@Query("UPDATE Games g set g.availability = false where g.id = :id")
    void updateGame(@Param("id") Long id);*/
}
