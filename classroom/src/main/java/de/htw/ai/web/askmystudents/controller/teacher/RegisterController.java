package de.htw.ai.web.askmystudents.controller.teacher;

import com.neverbounce.api.model.Result;
import com.neverbounce.api.model.SafeToSend;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.service.EmailService;
import de.htw.ai.web.askmystudents.service.Encoder;
import de.htw.ai.web.askmystudents.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

// TODO: cleanup register page. Controller should not have that many logic inside.

@RestController
public class RegisterController {


    private static final String VIEW_REGISTER = "login/register";
    private static final String VIEW_CONFIRM = "login/confirm";

    private final Encoder encoder;
    private final TeacherService teacherService;
    private final EmailService emailService;


    @Autowired
    public RegisterController(final Encoder encoder, final TeacherService teacherService, final EmailService emailService) {
        this.encoder = encoder;
        this.teacherService = teacherService;
        this.emailService = emailService;
    }


    @GetMapping(value = "/register")
    public ModelAndView showRegistrationPage(final ModelAndView modelAndView, final Teacher teacher) {
        modelAndView.addObject("teacher", teacher);
        modelAndView.setViewName(VIEW_REGISTER);
        return modelAndView;
    }


    @PostMapping(value = "/register")
    public ModelAndView processRegistrationForm(final ModelAndView modelAndView, @Valid final Teacher teacher, final BindingResult bindingResult, final HttpServletRequest request) {

        final Teacher teacherExists = this.teacherService.getTeacher(teacher.getEmail());

        if (teacherExists != null) {
            modelAndView.addObject("alreadyRegisteredMessage", "Ups! Es gibt schon einen Benutzer mit der Email Adresse.");
            modelAndView.setViewName(VIEW_REGISTER);
            bindingResult.reject("email");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName(VIEW_REGISTER);
        }

        final Result result = this.emailService.checkEmail(teacher.getEmail());
        final SafeToSend safeToSend = result.isSafeToSend();
        if (safeToSend.equals(SafeToSend.NO)) {
            final String errorMessage = String.format("Ups! Die Email %s konnte nicht erreicht werden.", teacher.getEmail());
            modelAndView.addObject("alreadyRegisteredMessage", errorMessage);
            modelAndView.setViewName(VIEW_REGISTER);
            bindingResult.reject("email");
        } else { // new user so we create user and send confirmation e-mail


            teacher.setEnabled(false);

            teacher.setConfirmationToken(UUID.randomUUID().toString());

            this.teacherService.save(teacher);

            final String appUrl = request.getScheme() + "://" + request.getServerName();

            final SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(teacher.getEmail());
            registrationEmail.setSubject("Registration Confirmation");
            registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                    + appUrl + "/confirm?token=" + teacher.getConfirmationToken());
            registrationEmail.setFrom("postmaster@sandbox61423596226740ffb03fbaceadbc3964.mailgun.org");

            this.emailService.sendEmail(registrationEmail);

            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + teacher.getEmail());
            modelAndView.setViewName(VIEW_REGISTER);
        }

        return modelAndView;
    }


    @GetMapping(value = "/confirm")
    public ModelAndView showConfirmationPage(final ModelAndView modelAndView, @RequestParam("token") final String token) {

        final Teacher teacher = this.teacherService.getTeacherByToken(token);

        if (teacher == null) {
            modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else if (teacher.isEnabled()) {
            modelAndView.addObject("invalidToken", "Oops!  Youre confirmation link has been expired. Did you already set your password?.");
        } else {
            modelAndView.addObject("confirmationToken", teacher.getConfirmationToken());
        }

        modelAndView.setViewName(VIEW_CONFIRM);
        return modelAndView;
    }


    @PostMapping(value = "/confirm")
    public ModelAndView processConfirmationForm(final ModelAndView modelAndView, final BindingResult bindingResult, @RequestParam final Map requestParams, final RedirectAttributes redir) {

        final String password = (String) requestParams.get("password");
        final String confirmPassword = (String) requestParams.get("confirmPassword");
        final String token = (String) requestParams.get("token");

        modelAndView.setViewName(VIEW_CONFIRM);

        if (!password.equals(confirmPassword)) {
            bindingResult.reject("password");
            redir.addFlashAttribute("errorMessage", "Your passwords are not identical. Please try again.");
            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            return modelAndView;
        }

        final Zxcvbn passwordCheck = new Zxcvbn();
        final Strength strength = passwordCheck.measure(password);
        if (strength.getScore() < 1) {
            bindingResult.reject("password");
            redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");
            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            return modelAndView;
        }

        final Teacher teacher = this.teacherService.getTeacherByToken(token);
        teacher.setPassword(this.encoder.encode(password));
        teacher.setEnabled(true);
        this.teacherService.save(teacher);

        modelAndView.addObject("successMessage", "Your password has been set!");
        return modelAndView;
    }
}
