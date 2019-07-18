package de.htw.ai.web.askmystudents.controller.teacher;

import de.htw.ai.web.askmystudents.models.AMSModelAndView;
import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.questions.Question;
import de.htw.ai.web.askmystudents.service.LessonService;
import de.htw.ai.web.askmystudents.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/teacher/course/{courseId}/lesson/{lessonId}")
public class QuizController {

    private static final String VIEW_QUIZ = "question/listQuestions";
    private static final String VIEW_EDIT_QUIZ = "quiz/editQuiz";
    private static final String VIEW_CREATE_QUIZ = "quiz/createQuiz";
    private static final String REDIRECT_LESSONS = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/quizzes";

    private final QuizService quizService;
    private final LessonService lessonService;


    @Autowired
    public QuizController(final QuizService quizService, final LessonService lessonService) {
        this.quizService = quizService;
        this.lessonService = lessonService;
    }


    @GetMapping(value = "/editQuiz/{quizId}")
    public AMSModelAndView editOne(@PathVariable final Long courseId,
                                   @PathVariable final Long lessonId,
                                   @PathVariable final Long quizId,
                                   final AMSModelAndView modelAndView) {
        final Quiz quiz = this.quizService.getById(quizId);
        modelAndView.setViewName(VIEW_EDIT_QUIZ);
        modelAndView.addObject("quiz", quiz);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        return modelAndView;
    }


    @GetMapping(value = "/quizCreation")
    public AMSModelAndView postOne(@PathVariable final Long courseId,
                                   @PathVariable final Long lessonId,
                                   final AMSModelAndView modelAndView) {
        modelAndView.setViewName(VIEW_CREATE_QUIZ);
        modelAndView.addObject("quiz", new Quiz());
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        return modelAndView;
    }


    @PostMapping(value = "/quiz")
    public AMSModelAndView createOne(@PathVariable final Long lessonId,
                                     @ModelAttribute final Quiz quiz) {
        final Quiz quizToCreate = this.quizService.insert(quiz);
        final Lesson lesson = this.lessonService.addChildObject(lessonId, quizToCreate);
        this.quizService.addParentObject(quizToCreate.getId(), lesson);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }


    @PostMapping(value = "/updateQuiz/{quizId}")
    public AMSModelAndView updateOne(@PathVariable final Long quizId,
                                     @ModelAttribute final Quiz quiz) {
        final Quiz quizToUpdate = this.quizService.getById(quizId);
        if (!quizToUpdate.getName().equals(quiz.getName())) {
            quizToUpdate.setName(quiz.getName());
        }
        if (!quizToUpdate.getDescription().equals(quiz.getDescription())) {
            quizToUpdate.setDescription(quiz.getDescription());
        }
        this.quizService.update(quizToUpdate);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }


    @GetMapping(value = "/quiz/{quizId}/questions")
    public AMSModelAndView getOne(@PathVariable final Long courseId,
                                  @PathVariable final Long lessonId,
                                  @PathVariable final Long quizId,
                                  final AMSModelAndView modelAndView) {
        final Quiz quiz = this.quizService.getById(quizId);
        final List<Question> questionList = quiz.getQuestions();
        questionList.sort(Comparator.comparing(Question::getOrderIndex));
        modelAndView.setViewName(VIEW_QUIZ);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        modelAndView.addObject("quizId", quizId);
        modelAndView.addObject("quiz", quiz);
        modelAndView.addObject("questionList", questionList);
        return modelAndView;
    }


    @PostMapping(value = "/deleteQuiz/{quizId}")
    public AMSModelAndView deleteOne(@PathVariable final Long lessonId,
                                     @PathVariable final Long quizId) {
        this.lessonService.deleteChildObject(lessonId, quizId);
        this.quizService.delete(quizId);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }


    @PostMapping(value = "/duplicateQuiz/{quizId}")
    public AMSModelAndView cuplicate(@PathVariable final Long quizId) {
        this.quizService.duplicate(quizId, true);
        return new AMSModelAndView(REDIRECT_LESSONS);
    }
}
