package com.ite5year.services;
import com.ite5year.models.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import com.ite5year.repositories.ApplicationUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
public class ApplicationUserDetailsServiceImpl implements UserDetailsService {
    final private ApplicationUserRepository applicationUserRepository;
    final private AuthenticationService authenticationService;
    public ApplicationUserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository, AuthenticationService authenticationService) {
        this.applicationUserRepository = applicationUserRepository;
        this.authenticationService = authenticationService;
    }



    public Optional<ApplicationUser> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUserDetailsImpl userPrincipal = (ApplicationUserDetailsImpl) auth.getPrincipal();
        String userEmail = userPrincipal.getEmail();
        return applicationUserRepository.findByEmail(userEmail);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ApplicationUser user = applicationUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        return ApplicationUserDetailsImpl.build(user);
    }
}
