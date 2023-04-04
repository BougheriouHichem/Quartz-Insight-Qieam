package com.apigames.quartzinsight.repository;


import com.apigames.quartzinsight.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friends, Long> {
    List<Friends> findAllByUser_Id(long id);

}
