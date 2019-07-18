package de.htw.ai.web.askmystudents.models;

import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.service.SpringContextBridge;
import de.htw.ai.web.askmystudents.service.TeacherService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


public class AMSModelAndView extends org.springframework.web.servlet.ModelAndView {

    public AMSModelAndView() {
        super();
        final TeacherService teacherService = SpringContextBridge.services().getTeacherService();
        final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Teacher teacher = teacherService.getTeacher(userDetails.getUsername());
        this.addObject("teacherName", teacher.getName());
    }

    public AMSModelAndView(final String redirectCourses) {
        super(redirectCourses);
    }
}
