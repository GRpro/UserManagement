package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Roles;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.SecurityService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private SecurityService securityService;

  @Autowired
  private UserValidator userValidator;

  /**
   * Return user registration page.
   */
  @RequestMapping(value = "/registration", method = RequestMethod.GET)
  public String registration(Model model) {
    model.addAttribute("userForm", new User());

    return "registration";
  }

  @RequestMapping(value = "/admin/user/{id}/state", method = RequestMethod.POST)
  public String changeUserState(@PathVariable("id") Long userId,
                                @RequestParam("action") String action) {
    User user = userService.findById(userId);

    switch (action) {
      case "block":
        user.setPending(true);
        break;
      case "activate":
        user.setPending(false);
        break;
      default:
        throw new RuntimeException("Unknown user state was specified " + action);
    }

    userService.update(user);

    return "redirect:/home";
  }

  /**
   * Register new user.
   */
  @RequestMapping(value = "/registration", method = RequestMethod.POST)
  public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
    userValidator.validate(userForm, bindingResult);

    if (bindingResult.hasErrors()) {
      return "registration";
    }

    // User is in the pending state until its account is approved by administrator.
    userForm.setPending(true);

    userService.save(userForm);

    securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

    return "redirect:/home";
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(@RequestParam("error") Optional<String> error,
                      @RequestParam("logout") Optional<String> logout,
                      Model model) {
    error.ifPresent(s ->
        model.addAttribute("error", "Your username and password is invalid."));
    logout.ifPresent(s ->
        model.addAttribute("message", "You have been logged out successfully."));

    return "login";
  }

  /**
   * Redirect user to home page depending on role.
   * Only authenticated users are able to access the home page.
   *
   * @return home page model and view
   */
  @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
  public ModelAndView homePage() {
    ModelAndView model = new ModelAndView();

    List<String> roles = getUserRoles();

    if (roles.contains(Roles.ADMIN.toString())) {
      model.setViewName("admin");

      // TODO pagination
      Collection<User> users = userService.findAllUsers();

      model.addObject("users", users);
      model.addObject("message", "Welcome user");

      return model;
    }

    if (roles.contains(Roles.USER.toString())) {
      model.setViewName("home");

      User user = userService.findByUsername(securityService.findLoggedInUsername());

      model.addObject("userData", user);
      model.addObject("message", "Welcome admin");

      return model;
    }

    return null;
  }

  private List<String> getUserRoles() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof UserDetails) {
      Collection<? extends GrantedAuthority> authorities = ((UserDetails) principal).getAuthorities();
      return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    return Collections.emptyList();
  }
}
