package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.Role;
import com.hellokoding.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);

  @Query("from User u where :role member u.roles")
  Collection<User> findAllUsers(@Param("role") Role role);

}
