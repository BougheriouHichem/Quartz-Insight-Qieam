package com.apigames.quartzinsight.repository;


import com.apigames.quartzinsight.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    @Query("SELECT f.friend FROM Friends f WHERE f.user.id = :userId")
    List<Users> findFriendsByUserId(@Param("userId") Long userId);
}
