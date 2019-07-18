package de.htw.ai.web.askmystudents.controller.student;

import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.users.Student;
import de.htw.ai.web.askmystudents.service.LessonService;
import de.htw.ai.web.askmystudents.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/student")
public class StudentController {

    private static final String VIEW_LOGIN = "login/loginStudent";
    private static final String REDIRECT_LESSON = "redirect:/student/lesson/{pin}/{studentName}/{encodedStudentName}/feedback";

    private final StudentService studentService;
    private final LessonService lessonService;


    @Autowired
    public StudentController(final StudentService studentService, final LessonService lessonService) {
        this.studentService = studentService;
        this.lessonService = lessonService;
    }


    @GetMapping(value = "/login")
    public ModelAndView loginPage(final ModelAndView modelAndView) {
        modelAndView.addObject(new Student());
        modelAndView.setViewName(VIEW_LOGIN);
        return modelAndView;
    }


    @PostMapping(value = "/login")
    public ModelAndView processLoginForm(@ModelAttribute final Student logStudent) {
        final int pin = logStudent.getPin();
        final String name = logStudent.getName();
        final Student student = this.studentService.getByNameAndPin(name, pin);
        if (student != null) {
            final ModelAndView modelAndView = new ModelAndView(VIEW_LOGIN);
            modelAndView.addObject("studentExistsMessage", "Oops!  There is already a student joined with the username.");
            return modelAndView;
        }

        final Lesson lesson = this.lessonService.getByPin(pin);
        if (lesson == null) {
            final ModelAndView modelAndView = new ModelAndView(VIEW_LOGIN);
            modelAndView.addObject("pinDoesNotExistMessage", "Oops!  There is no lesson with this PIN.");
            return modelAndView;
        }
        if (!lesson.getIsActive()) {
            final ModelAndView modelAndView = new ModelAndView(VIEW_LOGIN);
            modelAndView.addObject("lessonHasNotStartetMessage", "The lesson has not started yet. Please wait.");
            return modelAndView;
        }

        final Student studentToCreate = this.studentService.insert(logStudent);

        final String redirect = REDIRECT_LESSON
                .replace("{pin}", String.valueOf(pin))
                .replace("{studentName}", studentToCreate.getName())
                .replace("{encodedStudentName}", studentToCreate.getToken());
        return new ModelAndView(redirect);
    }
}
