package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Games;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableAspectJAutoProxy
public interface GameRepository extends JpaRepository<Games, Long> {
    @Query("SELECT g FROM Games g where g.availability = true")
    List<Games> findGamesByAvailability();

    List<Games> findGamesByTitle(String title);
}
