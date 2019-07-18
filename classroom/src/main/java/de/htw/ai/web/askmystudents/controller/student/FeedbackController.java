package de.htw.ai.web.askmystudents.controller.student;

import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.feedbacks.Feedback;
import de.htw.ai.web.askmystudents.models.users.Student;
import de.htw.ai.web.askmystudents.service.Encoder;
import de.htw.ai.web.askmystudents.service.FeedbackService;
import de.htw.ai.web.askmystudents.service.LessonService;
import de.htw.ai.web.askmystudents.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/student/lesson/{pin}/{studentName}/{encodedStudentName}")
public class FeedbackController {

    private static final String VIEW_FEEDBACK = "student/feedback";
    private static final String REDIRECT_LOGIN = "redirect:/student/login";

    private final LessonService lessonService;
    private final FeedbackService feedbackService;
    private final StudentService studentService;
    private final Encoder encoder;


    @Autowired
    public FeedbackController(final LessonService lessonService, final FeedbackService feedbackService, final StudentService studentService, final Encoder encoder) {
        this.lessonService = lessonService;
        this.feedbackService = feedbackService;
        this.studentService = studentService;
        this.encoder = encoder;
    }


    private ModelAndView checkPath(final String studentName, final String birthday, final String encodedStudentName) {
        final boolean matchingStringsAreEqual = this.encoder.matchingStrings(studentName + birthday, encodedStudentName);
        ModelAndView modelAndView = null;
        if (!matchingStringsAreEqual) {
            modelAndView = new ModelAndView(REDIRECT_LOGIN);
            modelAndView.addObject("lessonHasNotStartetMessage", "You are a hacker! Do not change the url manually!");
            modelAndView.addObject(new Student());
        }
        return modelAndView;
    }


    public ModelAndView reLogin(final String messageName, final String message, final RedirectAttributes redir) {
        final ModelAndView modelAndView = new ModelAndView(REDIRECT_LOGIN);
        redir.addFlashAttribute(messageName, message);
        modelAndView.addObject(new Student());
        return modelAndView;
    }


    public ModelAndView checkParams(final String studentName, final String encodedStudentName, final int pin, final RedirectAttributes redir) {
        final Lesson lesson = this.lessonService.getByPin(pin);
        if (!lesson.getIsActive()) {
            return reLogin("lessonHasNotStartetMessage", "Das Live Feedback wurde gestoppt.", redir);
        }
        final Student student = this.studentService.getByNameAndPin(studentName, pin);
        if (student == null) {
            return reLogin("lessonHasNotStartetMessage", "Es tut uns leid, du kannst dich nicht einloggen. Ist die Vorlesung vielleicht erneut gestartet worden? Bitte versuche es erneut!", redir);
        }
        final ModelAndView modelAndViewLogin = checkPath(studentName, student.getBirthday().toString(), encodedStudentName);
        if (modelAndViewLogin != null) {
            return modelAndViewLogin;
        }
        return null;
    }

    @GetMapping(value = "/feedback")
    public ModelAndView joinLesson(final ModelAndView modelAndView,
                                   @PathVariable final String studentName,
                                   @PathVariable final String encodedStudentName,
                                   @PathVariable final int pin,
                                   final RedirectAttributes redir) {

        final ModelAndView modelAndViewChecked = checkParams(studentName, encodedStudentName, pin, redir);
        if (modelAndViewChecked != null) {
            return modelAndViewChecked;
        }

        final Student student = this.studentService.getByNameAndPin(studentName, pin);
        modelAndView.setViewName(VIEW_FEEDBACK);
        modelAndView.addObject("student", student);
        modelAndView.addObject("feedback", new Feedback());
        modelAndView.addObject("pin", pin);
        return modelAndView;
    }


    @PostMapping(value = "/feedback")
    public ModelAndView addFeedback(@ModelAttribute final Feedback feedback,
                                    @PathVariable final int pin,
                                    @PathVariable final String studentName,
                                    @PathVariable final String encodedStudentName,
                                    final RedirectAttributes redir) {

        final ModelAndView modelAndViewChecked = checkParams(studentName, encodedStudentName, pin, redir);
        if (modelAndViewChecked != null) {
            return modelAndViewChecked;
        }

        feedback.setStudentName(studentName);
        feedback.setLesson(this.lessonService.getByPin(pin));
        final Feedback feedbackToCreate = this.feedbackService.insert(feedback);

        final Lesson lessonByFeedback = feedbackToCreate.getLesson();
        this.lessonService.addChildObject(lessonByFeedback.getId(), feedbackToCreate);
        this.feedbackService.insert(feedbackToCreate);

        final ModelAndView modelAndView = new ModelAndView(VIEW_FEEDBACK);
        modelAndView.addObject("successMessage", "Vielen Dank, wir haben dein Feedback erhalten.");
        modelAndView.addObject("studentName", studentName);
        return modelAndView;
    }

}
