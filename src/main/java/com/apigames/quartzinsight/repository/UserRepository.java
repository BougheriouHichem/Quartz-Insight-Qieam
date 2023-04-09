package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Games;
import com.apigames.quartzinsight.entity.Users;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableAspectJAutoProxy
public interface UserRepository extends JpaRepository<Users, Long> {

    List<Users> findUsersByEmail(String email);

    //List<Users> findUsersByUsername(String username);

    @Query("SELECT f.friends FROM Friends f WHERE f.user.id = :userId")
    List<Users> findFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f.friends FROM Friends f WHERE f.user.id = :userId AND f.friends.id = :friendId")
    Users findByUserIdAndFriendId(@Param("userId") long userId, @Param("friendId") long friendId);


    @Query("SELECT g.games FROM UserGame g WHERE g.user.id = :userId AND g.games.id = :gameId")
    Games findByUserIdAndGameId(long userId, long gameId);
}
