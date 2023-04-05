package com.apigames.quartzinsight.repository;

import com.apigames.quartzinsight.entity.Friends;
import com.apigames.quartzinsight.entity.Users;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@EnableAspectJAutoProxy
@Transactional
public interface FriendRepository extends JpaRepository<Friends, Long> {
    /*  List<Friends> findAllByUser_Username(String username);*/
    void deleteByUserOrFriends(Users user, Users friend);
   /* List<Friends> findAllByUser_Id(long id);*/
    /*void deleteFriendsById(long friendId);*/

    void deleteByUserAndFriends(Users user, Users friend);

}
