package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Role;
import com.hellokoding.auth.model.Roles;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.RoleRepository;
import com.hellokoding.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public void save(User user) {
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    if (Objects.isNull(user.getRoles()) || user.getRoles().isEmpty()) {
      Role role = roleRepository.findByName(Roles.USER.toString());
      user.setRoles(Collections.singleton(role));
    }

    userRepository.save(user);
  }

  @Override
  public void update(User user) {
    userRepository.save(user);
  }

  @Override
  public User findById(Long id) {
    return userRepository.findOne(id);
  }

  @Override
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public Collection<User> findAllUsers() {
    Role userRole = roleRepository.findByName(Roles.USER.toString());

    return userRepository.findAllUsers(userRole);
  }

  @Override
  public Collection<User> findAllAdmins() {
    Role userRole = roleRepository.findByName(Roles.ADMIN.toString());

    return userRepository.findAllUsers(userRole);
  }
}
