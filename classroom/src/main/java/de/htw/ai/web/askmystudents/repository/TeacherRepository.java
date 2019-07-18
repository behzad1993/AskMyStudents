package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.users.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    public Teacher findByEmailIgnoreCase(@Param("email") String email);

    public Teacher findByConfirmationToken(@Param("token") String token);
}
