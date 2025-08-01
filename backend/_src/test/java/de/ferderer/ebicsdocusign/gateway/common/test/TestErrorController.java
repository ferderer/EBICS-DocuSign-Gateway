package de.ferderer.ebicsdocusign.gateway.common.test;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestErrorController {

    @PostMapping("/base-exception")
    public String throwBaseException() {
        throw new AppException(ErrorCode.E_NOT_FOUND, HttpStatus.NOT_FOUND, "userId", 123);
    }
    
    @PostMapping("/generic-exception")
    public String throwGenericException() {
        throw new RuntimeException("Something went wrong");
    }
    
    @PostMapping("/validation")
    public String testValidation(@Valid @RequestBody TestRequest request) {
        return "OK";
    }
    
    @PostMapping("/constraint-violation")
    public String testConstraintViolation(@Email @RequestParam String email) {
        return "OK";
    }
    
    public record TestRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        @Email(message = "Invalid email format")
        String email
    ) {}
}
