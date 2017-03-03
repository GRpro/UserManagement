package com.hellokoding.auth;

import com.hellokoding.auth.model.Role;
import com.hellokoding.auth.model.Roles;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.RoleRepository;
import com.hellokoding.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  /**
   * Initialize application with some data.
   */
  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    // populate data

    List<Role> roleList = roleRepository.findAll();

    if (roleList.isEmpty()) {

      List<Role> roles = Arrays.asList(
          new Role(Roles.ADMIN.toString()),
          new Role(Roles.USER.toString())
      );

      roleRepository.save(roles);
      roleRepository.flush();
    }

    User admin = new User(
      "administrator",
        passwordEncoder.encode("administrator"),
      Collections.singleton(roleRepository.findByName(Roles.ADMIN.toString())),
      false);

    userRepository.save(admin);

    User user = new User(
        "username",
        passwordEncoder.encode("password"),
        Collections.singleton(roleRepository.findByName(Roles.USER.toString())),
        true
        );

    userRepository.save(user);
  }
}
