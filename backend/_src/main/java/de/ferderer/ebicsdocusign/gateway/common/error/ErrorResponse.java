package de.ferderer.ebicsdocusign.gateway.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolation;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import org.springframework.validation.FieldError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String code,
    String path,
    Map<String, Object> data,
    Set<ViolatedConstraint> invalid
) {
    
    public static record ViolatedConstraint(String code, String path, Object value) {
        public ViolatedConstraint(ConstraintViolation<?> violation) {
            this(violation.getMessage(), violation.getPropertyPath().toString(), violation.getInvalidValue());
        }

        public ViolatedConstraint(FieldError violation) {
            this(violation.getDefaultMessage(), violation.getField(), violation.getRejectedValue());
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Instant timestamp = Instant.now();
        private int status;
        private String error;
        private String code;
        private String path;
        private Map<String, Object> data;
        private Set<ViolatedConstraint> invalid;
        
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder status(int status) {
            this.status = status;
            return this;
        }
        
        public Builder error(String error) {
            this.error = error;
            return this;
        }
        
        public Builder code(String code) {
            this.code = code;
            return this;
        }
        
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        
        public Builder data(Map<String, Object> data) {
            this.data = data;
            return this;
        }
        
        public Builder invalid(Set<ViolatedConstraint> invalid) {
            this.invalid = invalid;
            return this;
        }
        
        public ErrorResponse build() {
            return new ErrorResponse(timestamp, status, error, code, path, data, invalid);
        }
    }
}
