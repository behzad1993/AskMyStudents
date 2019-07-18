package de.htw.ai.web.askmystudents.controller.teacher;

import de.htw.ai.web.askmystudents.models.AMSModelAndView;
import de.htw.ai.web.askmystudents.models.courses.Course;
import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.feedbacks.Feedback;
import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/teacher/course/{courseId}")
public class LessonController {

    private static final String VIEW_LESSON = "quiz/listQuizzes";
    private static final String VIEW_START_LESSON = "feedback/listFeedback";
    private static final String VIEW_EDIT_LESSON = "lesson/editLesson";
    private static final String VIEW_CREATE_LESSON = "lesson/createLesson";
    private static final String REDIRECT_LESSONS = "redirect:/teacher/course/{courseId}/lessons";
    private static final String REDIRECT_FEEDBACKS = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/feedback";

    private final LessonService lessonService;
    private final CourseService courseService;
    private final FeedbackService feedbackService;
    private final StudentService studentService;
    private final TeacherService teacherService;


    @Autowired
    public LessonController(final LessonService lessonService,
                            final CourseService courseService,
                            final FeedbackService feedbackService,
                            final StudentService studentService,
                            final TeacherService teacherService) {
        this.lessonService = lessonService;
        this.courseService = courseService;
        this.feedbackService = feedbackService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }


    private String getNameFromTeacher() {
        final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Teacher teacher = this.teacherService.getTeacher(userDetails.getUsername());
        final String name = teacher.getName();
        return name;
    }


    @GetMapping(value = "/editLesson/{lessonId}")
    public AMSModelAndView editOne(@PathVariable final Long courseId,
                                   @PathVariable final Long lessonId,
                                   final AMSModelAndView modelAndView) {
        final Lesson lesson = this.lessonService.getById(lessonId);
        modelAndView.setViewName(VIEW_EDIT_LESSON);
        modelAndView.addObject("teacherName", getNameFromTeacher());
        modelAndView.addObject("lesson", lesson);
        modelAndView.addObject("courseId", courseId);
        return modelAndView;
    }


    @GetMapping(value = "/lesson/{lessonId}/feedback")
    public AMSModelAndView showFeedbacks(@PathVariable final Long lessonId,
                                         @PathVariable final Long courseId,
                                         final AMSModelAndView modelAndView) {
        final Lesson lesson = this.lessonService.getById(lessonId);
        final List<Feedback> feedbackList = lesson.getFeedbacks();
        final Course course = this.courseService.getById(courseId);
        modelAndView.setViewName(VIEW_START_LESSON);
        modelAndView.addObject("lessonName", lesson.getName());
        modelAndView.addObject("isActive", lesson.getIsActive());
        modelAndView.addObject("teacherName", course.getUsername());
        modelAndView.addObject("feedbackList", feedbackList);
        modelAndView.addObject("pin", lesson.getPin());
        return modelAndView;
    }


    @PostMapping(value = "/lesson/{lessonId}/start")
    public AMSModelAndView startLessonTest(@PathVariable final Long lessonId) {
        final Lesson lesson = this.lessonService.getById(lessonId);
        if (!lesson.getIsActive()) {
            lesson.setIsActive(true);
            this.lessonService.setLessonIsActive(lesson.getId(), true);
        }
        return new AMSModelAndView(REDIRECT_FEEDBACKS);
    }


    @PostMapping(value = "/lesson/{lessonId}/stop")
    public AMSModelAndView startLesson(@PathVariable final Long lessonId,
                                       final AMSModelAndView modelAndView) {
        final Lesson lesson = this.lessonService.getById(lessonId);
        lesson.setIsActive(false);
        this.lessonService.setLessonIsActive(lesson.getId(), false);
        this.studentService.deleteFromLesson(lesson.getPin());
        return new AMSModelAndView(REDIRECT_FEEDBACKS);
    }


    @GetMapping(value = "/lessonCreation")
    public AMSModelAndView postOne(@PathVariable final Long courseId,
                                   final AMSModelAndView modelAndView) {
        modelAndView.setViewName(VIEW_CREATE_LESSON);
        modelAndView.addObject("lesson", new Lesson());
        modelAndView.addObject("courseId", courseId);
        return modelAndView;
    }


    @PostMapping(value = "/lesson")
    public AMSModelAndView createOne(@PathVariable final Long courseId,
                                     @ModelAttribute final Lesson lesson) {
        final Lesson lessonToCreate = this.lessonService.insert(lesson);
        final Course course = this.courseService.addChildObject(courseId, lessonToCreate);
        this.lessonService.addParentObject(lessonToCreate.getId(), course);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }


    @PostMapping(value = "/updateLesson/{lessonId}")
    public AMSModelAndView updateOne(@PathVariable final Long lessonId,
                                     @ModelAttribute final Lesson lesson) {
        final Lesson lessonToUpdate = this.lessonService.getById(lessonId);
        if (!lessonToUpdate.getName().equals(lesson.getName())) {
            lessonToUpdate.setName(lesson.getName());
        }
        if (!lessonToUpdate.getDescription().equals(lesson.getDescription())) {
            lessonToUpdate.setDescription(lesson.getDescription());
        }
        this.lessonService.update(lessonToUpdate);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }


    @GetMapping(value = "/lesson/{lessonId}/quizzes")
    public AMSModelAndView getOne(@PathVariable final Long courseId,
                                  @PathVariable final Long lessonId,
                                  final AMSModelAndView modelAndView) {
        final List<Quiz> quizList = this.lessonService.getObjects(lessonId);
        quizList.sort(Comparator.comparing(Quiz::getName));
        modelAndView.setViewName(VIEW_LESSON);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        modelAndView.addObject("quizList", quizList);
        return modelAndView;
    }

    @PostMapping(value = "/deleteLesson/{lessonId}")
    public AMSModelAndView deleteOne(@PathVariable final Long courseId,
                                     @PathVariable final Long lessonId) {
        this.courseService.deleteChildObject(courseId, lessonId);
        this.lessonService.delete(lessonId);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }


    @PostMapping(value = "/lesson/{lessonId}/deleteFeedback/{feedbackId}")
    public AMSModelAndView deleteFeedback(@PathVariable final Long feedbackId,
                                          @PathVariable final Long lessonId) {
        this.lessonService.deleteFeedback(lessonId, feedbackId);
        this.feedbackService.delete(feedbackId);
        return new AMSModelAndView(REDIRECT_FEEDBACKS);
    }


    @PostMapping(value = "/duplicateLesson/{lessonId}")
    public AMSModelAndView cuplicate(@PathVariable final Long lessonId) {
        this.lessonService.duplicate(lessonId, true);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }
}
