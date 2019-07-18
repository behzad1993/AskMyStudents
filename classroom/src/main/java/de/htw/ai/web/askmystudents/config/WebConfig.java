package de.htw.ai.web.askmystudents.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String VIEW_COURSES = "redirect:/teacher/courses";

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/loginTeacher").setViewName("login/loginTeacher");
        registry.addViewController("/").setViewName(VIEW_COURSES);
        registry.addViewController("/index").setViewName(VIEW_COURSES);
    }

}