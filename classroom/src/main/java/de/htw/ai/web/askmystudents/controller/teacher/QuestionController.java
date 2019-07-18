package de.htw.ai.web.askmystudents.controller.teacher;

import de.htw.ai.web.askmystudents.models.AMSModelAndView;
import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.questions.Answer;
import de.htw.ai.web.askmystudents.models.questions.MultipleChoiceQuestion;
import de.htw.ai.web.askmystudents.models.questions.Question;
import de.htw.ai.web.askmystudents.models.questions.SingleChoiceQuestion;
import de.htw.ai.web.askmystudents.service.AnswerService;
import de.htw.ai.web.askmystudents.service.QuestionService;
import de.htw.ai.web.askmystudents.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}")
public class QuestionController {

    private static final String VIEW_QUESTION_PRESENTATION = "presentation/questionContent";
    private static final String VIEW_QUESTION_DETAILS = "question/detailsQuestions";
    private static final String VIEW_EDIT_QUESTION_SINGLE = "question/editQuestionSingle";
    private static final String VIEW_CREATE_MULTIPLE_CHOICE_QUESTION = "question/createQuestion";
    private static final String REDIRECT_QUESTIONS = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}/questions";
    private static final String REDIRECT_QUESTION_EDIT_SINGLE = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}/editQuestionSingle/{questionId}";
    private static final Long MULTIPLECHOICE_QUESTION = 1L;

    final private QuizService quizService;
    final private QuestionService questionService;
    final private AnswerService answerService;


    @Autowired
    public QuestionController(final QuizService quizService, final QuestionService questionService, final AnswerService answerService) {
        this.quizService = quizService;
        this.questionService = questionService;
        this.answerService = answerService;
    }


    private AMSModelAndView createBasicView(final AMSModelAndView modelAndView, final Question question,
                                            final Long courseId, final Long lessonId, final Long quizId) {
        modelAndView.addObject("question", question);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        modelAndView.addObject("quizId", quizId);
        return modelAndView;
    }


    @GetMapping(value = "/editQuestionSingle/{questionId}")
    public AMSModelAndView editOneSingle(@PathVariable final Long courseId,
                                         @PathVariable final Long lessonId,
                                         @PathVariable final Long quizId,
                                         @PathVariable final Long questionId,
                                         final AMSModelAndView modelAndView) {
        final Question question = this.questionService.getById(questionId);
        if (question.isRandomOrder()) {
            Collections.shuffle(question.getAnswers());
        } else {
            question.getAnswers().sort(Comparator.comparing(Answer::getOrderIndex));
        }
        final long discriminatorValue = question instanceof MultipleChoiceQuestion ? 1 : 2;
        modelAndView.setViewName(VIEW_EDIT_QUESTION_SINGLE);
        modelAndView.addObject("discriminatorValue", discriminatorValue);
        createBasicView(modelAndView, question, courseId, lessonId, quizId);
        return modelAndView;
    }


    @GetMapping(value = "/questionPresentation/{orderIndex}")
    public AMSModelAndView questionPresentation(@PathVariable final Long courseId,
                                                @PathVariable final Long lessonId,
                                                @PathVariable final Long quizId,
                                                @PathVariable final Long orderIndex,
                                                final AMSModelAndView modelAndView) {

        final List<Question> questionList = this.quizService.getObjects(quizId);
        Collections.sort(questionList, Comparator.comparing(Question::getOrderIndex));
        final Question question;
        if (orderIndex == 0) {
            question = questionList.get(0);
        } else {
            question = questionList.get(Math.toIntExact(orderIndex));
        }

        final List<Answer> answerList = question.getAnswers();
        if (question.isRandomOrder()) {
            Collections.shuffle(answerList);
        } else {
            Collections.sort(answerList, Comparator.comparing(Answer::getOrderIndex));
        }
        question.setAnswers(answerList);

        final AMSModelAndView view = createBasicView(modelAndView, question, courseId, lessonId, quizId);
        view.setViewName(VIEW_QUESTION_PRESENTATION);
        return view;
    }


    @PostMapping(value = "/next/{orderIndex}")
    public AMSModelAndView nextQuestion(@PathVariable final Long courseId,
                                        @PathVariable final Long lessonId,
                                        @PathVariable final Long quizId,
                                        @PathVariable final Long orderIndex,
                                        final AMSModelAndView modelAndView) {
        final Long nextIndex = orderIndex + 1;
        final AMSModelAndView view = getQuestionDetails(courseId, lessonId, quizId, nextIndex, modelAndView);
        final String redirect = "redirect:/teacher/course/{courseId}/lesson/{lessonId}/quiz/{quizId}/questionPresentation/";
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(redirect).append(nextIndex);
        final String s = stringBuilder.toString();
        view.setViewName(s);
        return view;
    }


    @GetMapping(value = "/shuffle/{questionId}")
    public AMSModelAndView shuffleAnswers(@PathVariable final Long courseId,
                                          @PathVariable final Long lessonId,
                                          @PathVariable final Long quizId,
                                          @PathVariable final Long questionId,
                                          final AMSModelAndView modelAndView) {
        this.questionService.shuffle(questionId);
        final Question question = this.questionService.getById(questionId);
        final AMSModelAndView view = createBasicView(modelAndView, question, courseId, lessonId, quizId);
        view.setViewName(VIEW_EDIT_QUESTION_SINGLE);
        return view;

    }

    @GetMapping(value = "/questionCreation/{discriminatorValue}")
    public AMSModelAndView postOne(@PathVariable final Long courseId,
                                   @PathVariable final Long lessonId,
                                   @PathVariable final Long quizId,
                                   @PathVariable final Long discriminatorValue,
                                   final AMSModelAndView modelAndView) {
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("lessonId", lessonId);
        modelAndView.addObject("quizId", quizId);
        modelAndView.addObject("discriminatorValue", discriminatorValue);
        final Question question;
        if (discriminatorValue.equals(MULTIPLECHOICE_QUESTION)) {
            question = new MultipleChoiceQuestion();
        } else {
            question = new SingleChoiceQuestion();
        }
        question.setQuiz(this.quizService.getById(quizId));
        modelAndView.addObject("question", question);
        modelAndView.setViewName(VIEW_CREATE_MULTIPLE_CHOICE_QUESTION);
        return modelAndView;
    }


    @GetMapping(value = "/question/{questionId}/questionDetails")
    public AMSModelAndView getQuestionDetails(@PathVariable final Long courseId,
                                              @PathVariable final Long lessonId,
                                              @PathVariable final Long quizId,
                                              @PathVariable final Long questionId,
                                              final AMSModelAndView modelAndView) {
        final Question question = this.questionService.getById(questionId);
        final List<Answer> answerList = question.getAnswers();

        if (question.hasRandomOrder()) {
            Collections.shuffle(answerList);
        } else {
            answerList.sort(Comparator.comparing(Answer::getOrderIndex));
        }
        modelAndView.setViewName(VIEW_QUESTION_DETAILS);
        modelAndView.addObject("answerList", answerList);
        createBasicView(modelAndView, question, courseId, lessonId, quizId);
        return modelAndView;
    }


    @PostMapping(value = "/{discriminatorValue}")
    public AMSModelAndView createOneSCQ(@PathVariable final Long quizId,
                                        @PathVariable final Long discriminatorValue,
                                        @ModelAttribute final SingleChoiceQuestion questionSC,
                                        @ModelAttribute final MultipleChoiceQuestion questionMC) {
        if (discriminatorValue == 1) {
            return createOne(quizId, this.questionService.insert(questionMC));
        } else {
            return createOne(quizId, this.questionService.insert(questionSC));
        }
    }


    private AMSModelAndView createOne(final Long quizId, final Question questionToCreate) {
        final Quiz quiz = this.quizService.addChildObject(quizId, questionToCreate);
        this.questionService.addParentObject(questionToCreate.getId(), quiz);
        return new AMSModelAndView(REDIRECT_QUESTIONS);
    }


    @PostMapping(value = "/updateQuestion/{questionId}")
    public AMSModelAndView updateOne(@PathVariable final Long questionId,
                                     @PathVariable final Long courseId,
                                     @PathVariable final Long lessonId,
                                     @PathVariable final Long quizId,
                                     @ModelAttribute final MultipleChoiceQuestion mcq,
                                     @ModelAttribute final SingleChoiceQuestion scq,
                                     final RedirectAttributes redir) {
        final Question questionToUpdate = this.questionService.getById(questionId);
        final int discriminatorValue = questionToUpdate instanceof SingleChoiceQuestion ? 2 : 1;
        final Question question;
        if (discriminatorValue == 1) {
            question = mcq;
        } else {
            question = scq;
        }
        final List<Answer> answerList = question.getAnswers();

        if (!answerList.equals(questionToUpdate.getAnswers())) {
            final boolean questionAllowed = controlAnswer(answerList, question);
            if (!questionAllowed) {
//                final AMSModelAndView modelAndView = createBasicView(new AMSModelAndView(), question, courseId, lessonId, quizId);
                if (discriminatorValue == 1) {
                    redir.addFlashAttribute("errorMessage", "Anzahl der richtigen Antworten muss mindestens 1 sein");
                } else {
                    redir.addFlashAttribute("errorMessage", "Anzahl der richtigen Antworten muss genau 1 sein");
                }
                final AMSModelAndView modelAndView = new AMSModelAndView();
                modelAndView.setViewName(REDIRECT_QUESTION_EDIT_SINGLE);
                return modelAndView;
            }
            final List<Answer> answersToUpdate = this.answerService.insertAll(answerList);
            questionToUpdate.setAnswers(answersToUpdate);
        }

        if (!questionToUpdate.getText().equals(question.getText())) {
            questionToUpdate.setText(question.getText());
        }
        if (questionToUpdate.getTimer() != question.getTimer()) {
            questionToUpdate.setTimer(question.getTimer());
        }
        if (questionToUpdate.isRandomOrder() != question.isRandomOrder()) {
            questionToUpdate.setRandomOrder(question.isRandomOrder());
        }

        this.questionService.update(questionToUpdate);
        return new AMSModelAndView(REDIRECT_QUESTIONS);
    }

    private boolean controlAnswer(final List<Answer> answerList, final Question question) {
        if (question instanceof SingleChoiceQuestion) {
            return checkAnswersForSC(answerList);
        } else {
            return checkAnswersForMC(answerList);
        }
    }

    private boolean checkAnswersForMC(final List<Answer> answerList) {
        final long count = answerList.stream().filter(answer -> answer.getIsCorrect()).count();
        return count > 0 ? true : false;
    }

    private boolean checkAnswersForSC(final List<Answer> answerList) {
        final long count = answerList.stream().filter(answer -> answer.getIsCorrect()).count();
        return count == 1 ? true : false;
    }


    @PostMapping(value = "/decreaseIndex/{questionId}")
    public AMSModelAndView decreaseIndex(@PathVariable final Long questionId,
                                         @PathVariable final Long quizId) {
        this.questionService.decreaseIndex(questionId, quizId);
        return new AMSModelAndView(REDIRECT_QUESTIONS);
    }


    @PostMapping(value = "/increaseIndex/{questionId}")
    public AMSModelAndView increaseIndex(@PathVariable final Long questionId,
                                         @PathVariable final Long quizId) {
        this.questionService.increaseIndex(questionId, quizId);
        return new AMSModelAndView(REDIRECT_QUESTIONS);
    }


    @PostMapping(value = "/deleteQuestion/{questionId}")
    public AMSModelAndView deleteOne(@PathVariable final Long quizId,
                                     @PathVariable final Long questionId) {
        this.quizService.deleteChildObject(quizId, questionId);
        this.questionService.delete(questionId);
        return new AMSModelAndView(REDIRECT_QUESTIONS);
    }


    @PostMapping(value = "/duplicateQuestion/{questionId}")
    public AMSModelAndView duplicate(@PathVariable final Long questionId) {
        this.questionService.duplicate(questionId, true);
        return new AMSModelAndView(REDIRECT_QUESTIONS);
    }
}
