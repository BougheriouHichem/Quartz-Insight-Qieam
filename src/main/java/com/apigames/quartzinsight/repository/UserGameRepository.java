package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Games;
import com.apigames.quartzinsight.entity.UserGame;
import com.apigames.quartzinsight.entity.Users;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@EnableAspectJAutoProxy
@Transactional
public interface UserGameRepository extends JpaRepository<UserGame, Long> {
    @Query("SELECT u.games FROM UserGame u WHERE u.user.id = :userId ")
    List<Games> findGamesByUserId(@Param("userId") Long userId);

    //void deleteByUserAndGames(Users user, Games games);
    @Query("SELECT u.games FROM UserGame u WHERE u.games.id = :gameId AND u.user.id = :userId")
    Optional<Games> findByUserIdAndGameId(@Param("userId") long userId, @Param("gameId") long gameId);

    void deleteByUserAndGames(Users user, Games game);
}
