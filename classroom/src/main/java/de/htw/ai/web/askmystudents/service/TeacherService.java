package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    @Autowired
    TeacherRepository teacherRepository;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public void save(final Teacher teacher) {
//        teacher.setPassword(getPasswordEncoder().encode(teacher.getPassword()));
        this.teacherRepository.save(teacher);
    }

    public Teacher getTeacher(final String username) {
        return this.teacherRepository.findByEmailIgnoreCase(username);
    }


    public Teacher getTeacherByToken(final String token) {
        return this.teacherRepository.findByConfirmationToken(token);
    }
}
