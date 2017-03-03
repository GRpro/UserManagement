package com.hellokoding.auth.service;

import com.hellokoding.auth.model.User;

import java.util.Collection;

public interface UserService {
  void save(User user);

  void update(User user);

  User findById(Long id);

  User findByUsername(String username);

  Collection<User> findAllUsers();

  Collection<User> findAllAdmins();
}
