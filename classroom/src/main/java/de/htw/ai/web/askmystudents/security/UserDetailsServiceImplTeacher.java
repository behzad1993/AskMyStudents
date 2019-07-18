package de.htw.ai.web.askmystudents.security;

import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImplTeacher implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TeacherService teacherService;

    @Autowired
    public UserDetailsServiceImplTeacher(final TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        final Teacher teacher = this.teacherService.getTeacher(username);

        if (teacher == null) {
            throw new UsernameNotFoundException("User" + username + "was not found in the database");
        }

        final UserDetails user = User
                .withUsername(teacher.getEmail())
                .accountLocked(!teacher.isEnabled())
                .password(teacher.getPassword())
                .roles(teacher.getRole())
                .build();
        this.log.info("User with username {} logged in", user.getUsername());
        return user;
    }
}
