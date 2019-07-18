package de.htw.ai.web.askmystudents.controller.teacher;

import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.repository.TeacherRepository;
import de.htw.ai.web.askmystudents.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

class RegisterControllerTest {

    @InjectMocks
    private RegisterController registerController;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherService teacherService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_getRegisterPage() {
        final Teacher teacher = new Teacher();
        teacher.setEmail("test@test");
        final ModelAndView modelAndView = this.registerController.showRegistrationPage(new ModelAndView(), new Teacher());
        final Map<String, Object> model = modelAndView.getModel();
        final Object teacherObject = model.get("teacher");
        assertEquals(modelAndView.getViewName(), "login/register");
        assertEquals(teacherObject instanceof Teacher, true);
    }


    @Test
    public void test_getConfirmPage_wrongToken_withInvalidToken() {
        final ModelAndView modelAndView = this.registerController.showConfirmationPage(new ModelAndView(), "randomToken");
        final Map<String, Object> model = modelAndView.getModel();
        assertEquals(model.containsKey("invalidToken"), true);
    }


    @Test
    public void test_getConfirmPage_teacherEnabled_withInvalidToken() {
        final Teacher teacherMock = mock(Teacher.class);
        Mockito.when(teacherMock.isEnabled()).thenReturn(true);
        final ModelAndView modelAndView = this.registerController.showConfirmationPage(new ModelAndView(), "randomToken");
        final Map<String, Object> model = modelAndView.getModel();
        assertEquals(model.containsKey("invalidToken"), true);
    }

    @Test
    public void test_getConfirmPage_teacherEnabled_withValidToken() {
        final Teacher teacherMock = mock(Teacher.class);
        Mockito.when(teacherMock.isEnabled()).thenReturn(false);
        Mockito.when(this.teacherService.getTeacherByToken(anyString())).thenReturn(teacherMock);
        final ModelAndView modelAndView = this.registerController.showConfirmationPage(new ModelAndView(), "randomToken");
        final Map<String, Object> model = modelAndView.getModel();
        assertEquals(model.containsKey("confirmationToken"), true);
    }
}