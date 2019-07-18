package de.htw.ai.web.askmystudents.controller.teacher;

import de.htw.ai.web.askmystudents.models.AMSModelAndView;
import de.htw.ai.web.askmystudents.models.questions.Answer;
import de.htw.ai.web.askmystudents.models.questions.Question;
import de.htw.ai.web.askmystudents.service.AnswerService;
import de.htw.ai.web.askmystudents.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}/question/{questionId}")
public class AnswerController {

    private static final String VIEW_CREATE_ANSWER = "answer/createAnswer";
    private static final String REDIRECT_QUESTION_EDIT = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}/editQuestionSingle/{questionId}";
    private static final String REDIRECT_ANSWER_DETAILS = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}/question/{questionId}/questionDetails";

    private final QuestionService questionService;
    private final AnswerService answerService;


    @Autowired
    public AnswerController(final QuestionService questionService, final AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }


    @GetMapping(value = "/answerCreation")
    public AMSModelAndView postOne(final AMSModelAndView modelAndView,
                                   @PathVariable final Long courseId,
                                   @PathVariable final Long lessonId,
                                   @PathVariable final Long quizId,
                                   @PathVariable final Long questionId) {
        modelAndView.addObject("answer", new Answer());
        modelAndView.addObject("questionId", questionId);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        modelAndView.addObject("quizId", quizId);
        modelAndView.setViewName(VIEW_CREATE_ANSWER);
        return modelAndView;
    }


    @PostMapping(value = "/answer")
    public AMSModelAndView createOne(@PathVariable final Long questionId,
                                     @ModelAttribute final Answer answer) {
        final Answer answerToCreate = this.answerService.insert(answer);
        final Question question = this.questionService.addChildObject(questionId, answerToCreate);
        this.answerService.addParentObject(answerToCreate.getId(), question);
        return new AMSModelAndView(REDIRECT_QUESTION_EDIT);
    }


    private AMSModelAndView deleteAnswer(final Long questionId, final Long answerId) {
        this.questionService.deleteChildObject(questionId, answerId);
        this.answerService.delete(answerId);
        return new AMSModelAndView(REDIRECT_QUESTION_EDIT);
    }

    @PostMapping(value = "/deleteAnswer/{answerId}")
    public AMSModelAndView deleteOneGET(@PathVariable final Long questionId,
                                        @PathVariable final Long answerId) {
        return deleteAnswer(questionId, answerId);
    }

    @GetMapping(value = "/deleteAnswer/{answerId}")
    public AMSModelAndView deleteOnePOST(@PathVariable final Long questionId,
                                         @PathVariable final Long answerId) {
        return deleteAnswer(questionId, answerId);
    }


    @PostMapping(value = "/decreaseIndex/{answerId}")
    public AMSModelAndView decreaseIndex(@PathVariable final Long questionId,
                                         @PathVariable final Long answerId) {
        this.answerService.decreaseIndex(answerId, questionId);
        return new AMSModelAndView(REDIRECT_ANSWER_DETAILS);
    }


    @PostMapping(value = "/increaseIndex/{answerId}")
    public AMSModelAndView increaseIndex(@PathVariable final Long questionId,
                                         @PathVariable final Long answerId) {
        this.answerService.increaseIndex(answerId, questionId);
        return new AMSModelAndView(REDIRECT_ANSWER_DETAILS);
    }
}
