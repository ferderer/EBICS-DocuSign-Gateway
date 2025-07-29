package de.ferderer.ebicsdocusign.gateway.common.error;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    private final ErrorCode code;
    private final Map<String, Object> data = new HashMap<>();

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = Objects.requireNonNull(errorCode, "ErrorCode cannot be null");
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = Objects.requireNonNull(errorCode, "ErrorCode cannot be null");
    }

    public ErrorCode getCode() {
        return code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public HttpStatus getStatus() {
        return code.getHttpStatus();
    }

    public AppException withData(String key, Object value) {
        Objects.requireNonNull(key, "Data key cannot be null");
        this.data.put(key, value);
        return this;
    }
}

