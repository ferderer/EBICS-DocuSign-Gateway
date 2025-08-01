package de.ferderer.ebicsdocusign.gateway.common.error;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException {

    private static final String ASSERTION_ARGS_NOT_EVEN =
        "Uneven number arguments! Data arguments to AppException(id, ...data) should be given in pairs: name, value!";
    private static final String ASSERTION_ODD_ARG_NOT_STRING =
        "Every odd data argument to AppException(id, ...data) must be a string!";

    private final StandardErrorCode code;
    private final HttpStatus status;
    private final Map<String, Object> data = new HashMap<>();

    public AppException(StandardErrorCode errorCode, Object... data) {
        super(errorCode.name());
        code = errorCode;
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        mapData(data);
    }

    public AppException(StandardErrorCode errorCode, Throwable cause, Object... data) {
        super(errorCode.name(), cause);
        code = errorCode;
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        mapData(data);
    }

    public AppException(StandardErrorCode errorCode, HttpStatus httpStatus, Object... data) {
        super(errorCode.name());
        code = errorCode;
        status = httpStatus;
        mapData(data);
    }

    public AppException(StandardErrorCode errorCode, Throwable cause, HttpStatus httpStatus, Object... data) {
        super(errorCode.name(), cause);
        code = errorCode;
        status = httpStatus;
        mapData(data);
    }

    private void mapData(Object[] data) {
        Assert.isTrue((data.length % 2) == 0, ASSERTION_ARGS_NOT_EVEN);

        for(int i = 1, N = data.length; i < N; i += 2) {
            Assert.isInstanceOf(String.class, data[i - 1], ASSERTION_ODD_ARG_NOT_STRING);
            this.data.put((String) data[i - 1], data[i]);
        }
    }
}

