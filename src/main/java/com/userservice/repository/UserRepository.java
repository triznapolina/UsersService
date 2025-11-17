package com.userservice.repository;

import com.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


    @Query("select count(c) from PaymentCard c where c.user.id = :userId")
    long countCardsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("update User u set u.active = :active where u.id = :userId")
    int setStatusOfActivity(@Param("userId") Long userId, @Param("active") boolean active);


    boolean existsUserByEmail(String email);
}
