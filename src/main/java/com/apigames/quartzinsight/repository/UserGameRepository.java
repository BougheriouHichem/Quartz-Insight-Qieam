package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Games;
import com.apigames.quartzinsight.entity.UserGame;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableAspectJAutoProxy
public interface UserGameRepository extends JpaRepository<UserGame, Long> {
    @Query("SELECT u.games FROM UserGame u WHERE u.user.id = :userId ")
    List<Games> findGamesByUserId(@Param("userId") Long userId);
}
