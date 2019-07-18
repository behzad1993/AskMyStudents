package de.htw.ai.web.askmystudents.controller.teacher;

import de.htw.ai.web.askmystudents.models.AMSModelAndView;
import de.htw.ai.web.askmystudents.models.courses.Course;
import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/teacher")
public class CourseController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String VIEW_COURSE = "lesson/listLessons";
    private static final String VIEW_COURSES = "course/listCourses";
    private static final String VIEW_EDIT_COURSE = "course/editCourse";
    private static final String VIEW_CREATE_COURSE = "course/createCourse";
    private static final String REDIRECT_COURSES = "redirect:/teacher/courses";


    private final CourseService courseService;

    @Autowired
    public CourseController(final CourseService courseService) {
        this.courseService = courseService;
    }


    @GetMapping(value = "/courses")
    public AMSModelAndView getAll(final AMSModelAndView modelAndView) {
        final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final List<Course> courseList = this.courseService.getAllFromUser(userDetails.getUsername());
        courseList.sort(Comparator.comparing(Course::getName));
        modelAndView.setViewName(VIEW_COURSES);
        modelAndView.addObject("courseList", courseList);
        return modelAndView;
    }


    @GetMapping(value = "/editCourse/{id}")
    public AMSModelAndView editOne(@PathVariable final Long id, final AMSModelAndView modelAndView) {
        final Course course = this.courseService.getById(id);
        modelAndView.addObject("course", course);
        modelAndView.setViewName(VIEW_EDIT_COURSE);
        return modelAndView;
    }


    @GetMapping(value = "/courseCreation")
    public AMSModelAndView postOne(final AMSModelAndView modelAndView) {
        modelAndView.addObject("course", new Course());
        modelAndView.setViewName(VIEW_CREATE_COURSE);
        return modelAndView;
    }


    @PostMapping(value = "/course")
    public AMSModelAndView createOne(@ModelAttribute final Course course) {
        final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        course.setUsername(userDetails.getUsername());
        this.courseService.insert(course);
        return new AMSModelAndView(REDIRECT_COURSES);
    }


    @PostMapping(value = "/editCourse/{id}/updateCourse")
    public AMSModelAndView updateOne(@PathVariable final Long id, @ModelAttribute final Course course) {
        final Course courseToUpdate = this.courseService.getById(id);
        if (!courseToUpdate.getName().equals(course.getName())) {
            courseToUpdate.setName(course.getName());
        }
        if (!courseToUpdate.getDescription().equals(course.getDescription())) {
            courseToUpdate.setDescription(course.getDescription());
        }
        this.courseService.update(courseToUpdate);
        this.log.info("Edited course {} (id: {}) from user {}", courseToUpdate.getName(), courseToUpdate.getId(), course.getUsername());
        return new AMSModelAndView(REDIRECT_COURSES);
    }


    @GetMapping(value = "/course/{courseId}/lessons")
    public AMSModelAndView getOne(@PathVariable final Long courseId, final AMSModelAndView modelAndView) {
        final List<Lesson> lessonList = this.courseService.getObjects(courseId);
        lessonList.sort(Comparator.comparing(Lesson::getName));
        modelAndView.setViewName(VIEW_COURSE);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonList", lessonList);
        return modelAndView;
    }


    @PostMapping(value = "/deleteCourse/{courseId}")
    public AMSModelAndView deleteOne(@PathVariable final Long courseId) {
        this.courseService.delete(courseId);
        return new AMSModelAndView(REDIRECT_COURSES);
    }


    @PostMapping(value = "/course/duplicate/{courseId}")
    public AMSModelAndView cuplicate(@PathVariable final Long courseId) {
        this.courseService.duplicate(courseId, false);
        return new AMSModelAndView(REDIRECT_COURSES);
    }
}