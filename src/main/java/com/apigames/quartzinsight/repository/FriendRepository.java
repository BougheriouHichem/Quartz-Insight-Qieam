package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Friends;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableAspectJAutoProxy
public interface FriendRepository extends JpaRepository<Friends, Long> {
   /* List<Friends> findAllByUser_Id(long id); List<Friends> findAllByUser_Username(String username);*/

}
