package com.medilabosolutions.type2diabetesfinder.noteservice.controller;

import com.medilabosolutions.type2diabetesfinder.noteservice.error.ApiError;
import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.RequestService;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.RequestServiceImpl;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerExceptionHandlerTest {

    @InjectMocks
    private ControllerExceptionHandler controllerExceptionHandler;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;

    @BeforeAll
    public void setUpForAllTests() {
        requestMock = new MockHttpServletRequest();
        requestMock.setServerName("http://localhost:9003");
        requestMock.setRequestURI("/");
        requestMock.setMethod("GET");
        request = new ServletWebRequest(requestMock);
    }

    @AfterAll
    public void unSetForAllTests() {
        requestMock = null;
        request = null;
    }

    @AfterEach
    public void unsetForEachTest() {
        controllerExceptionHandler = null;
    }

    @SneakyThrows
    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test methodArgumentTypeMismatchException should return a Bad Request ResponseEntity")
    public void methodArgumentTypeMismatchExceptionTestShouldReturnABadRequestResponseEntity() {
        //GIVEN
        Method method = NoteController.class.getDeclaredMethod("getNoteById", String.class, WebRequest.class);
        // index of the parameter @PathVariable("id") String id
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentTypeMismatchException methodArgumentTypeMismatchException
                = new MethodArgumentTypeMismatchException(
                "null"
                , String.class
                , "id"
                , methodParameter
                , new NumberFormatException("Failed to convert value of type 'java.lang.String' to required type 'java.lang.String'; For input string: \"null\"")
        );
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(methodArgumentTypeMismatchException, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test IllegalArgumentException should return a Bad Request ResponseEntity")
    public void illegalArgumentExceptionTestShouldReturnABadRequestResponseEntity() {
        //GIVEN
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Resource Not Found");
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(illegalArgumentException, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @SneakyThrows
    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test methodArgumentNotValidException should return a Bad Request ResponseEntity")
    public void methodArgumentNotValidExceptionTestShouldReturnABadRequestResponseEntity() {
        //GIVEN
        Method method = NoteController.class.getDeclaredMethod("createNote", Optional.class, WebRequest.class);
        // index of the parameter @RequestBody Optional<@Valid Note> optionalNote
        MethodParameter methodParameter = new MethodParameter(method, 0);
        Object noteClass = Note.class;
        DirectFieldBindingResult bindingResult = new DirectFieldBindingResult(noteClass, "Note");
        bindingResult.addError(new FieldError("Note", "content", "content is mandatory"));

        MethodArgumentNotValidException manve = new MethodArgumentNotValidException(methodParameter, bindingResult);

        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(manve, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test constraintViolationException should return a Bad Request ResponseEntity")
    public void constraintViolationExceptionTestShouldReturnABadRequestResponseEntity() {
        //GIVEN
        // Class ConstraintViolationImpl is nested
        ConstraintViolationException cve = new ConstraintViolationException(Set.of(new ConstraintViolationImpl("Constraint violation")));
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(cve, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test resourceNotFoundException should return a Bad Request ResponseEntity")
    public void resourceNotFoundExceptionTesShouldReturnABadRequestResponseEntity() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Resource Not Found");
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(resourceNotFoundException, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test nullPointerException should return a Bad Request ResponseEntity")
    public void nullPointerExceptionTestShouldReturnABadRequestResponseEntity() {
        //GIVEN
        NullPointerException npe = new NullPointerException("Null Pointer Exception");
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(npe, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test badRequestException should return a Bad Request ResponseEntity")
    public void badRequestExceptionTestShouldReturnABadRequestResponseEntity() {
        //GIVEN
        BadRequestException bre = new BadRequestException("Bad request exception");
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.badRequestException(bre, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Bad request");
    }

    @Test
    @Tag("ControllerExceptionHandlerTest")
    @DisplayName("test unexpectedException should return a Internal Server Error ResponseEntity")
    public void unexpectedExceptionTest() {
        //GIVEN
        Exception e = new Exception("Unexpected Error...");
        //WHEN
        ResponseEntity<ApiError> responseEntity = controllerExceptionHandler.unexpectedException(e, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.INTERNAL_SERVER_ERROR)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Internal Server Error");
    }

    //Needed in constraintViolationExceptionTest()
    class ConstraintViolationImpl implements ConstraintViolation<Object> {

        private String message;

        public ConstraintViolationImpl(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return "";
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return null;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> aClass) {
            return null;
        }
    }
}