package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

/**
 * Repository interface for accessing User data.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Custom method to fetch user by email using JPQL.
     * 
     * @param email user email
     * @return User entity if found, otherwise null
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByUserName(@Param("email") String email);
}
