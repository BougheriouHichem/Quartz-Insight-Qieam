package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.UserGame;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableAspectJAutoProxy
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

}
