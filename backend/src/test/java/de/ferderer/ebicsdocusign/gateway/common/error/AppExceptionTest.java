package de.ferderer.ebicsdocusign.gateway.common.error;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AppExceptionTest {

    @Test
    void constructor_withNullErrorCode_shouldThrowNPE() {
        assertThatThrownBy(() -> new AppException(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Cannot invoke \"de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode.getMessage()\" because \"errorCode\" is null");
    }

    @Test
    void constructor_withNullErrorCodeAndCause_shouldThrowNPE() {
        RuntimeException cause = new RuntimeException("Test cause");
        
        assertThatThrownBy(() -> new AppException(null, cause))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Cannot invoke \"de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode.getMessage()\" because \"errorCode\" is null");
    }

    @Test
    void withData_withNullKey_shouldThrowNPE() {
        AppException exception = new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        
        assertThatThrownBy(() -> exception.withData(null, "value"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Data key cannot be null");
    }

    @Test
    void withData_shouldReturnSameInstance() {
        AppException exception = new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        AppException result = exception.withData("key", "value");
        assertThat(result).isSameAs(exception);
    }

    @Test
    void withData_shouldAllowMethodChaining() {
        AppException exception = new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        
        AppException result = exception
            .withData("key1", "value1")
            .withData("key2", "value2")
            .withData("key3", "value3");
        
        assertThat(result).isSameAs(exception);
        assertThat(exception.getData())
            .hasSize(3)
            .containsEntry("key1", "value1")
            .containsEntry("key2", "value2")
            .containsEntry("key3", "value3");
    }

    @Test
    void withData_shouldModifySameDataMap() {
        AppException exception = new AppException(ErrorCode.INTERNAL_SERVER_ERROR);

        var dataMapBefore = exception.getData();
        exception.withData("key", "value");
        
        assertThat(exception.getData()).isSameAs(dataMapBefore);
        assertThat(dataMapBefore).containsEntry("key", "value");
    }

    @Test
    void withData_shouldOverwriteExistingKeys() {
        AppException exception = new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        
        exception
            .withData("key", "original")
            .withData("key", "overwritten");
        
        assertThat(exception.getData())
            .hasSize(1)
            .containsEntry("key", "overwritten");
    }

    @Test
    void withData_shouldAcceptNullValues() {
        AppException exception = new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        
        exception.withData("nullValue", null);
        
        assertThat(exception.getData())
            .hasSize(1)
            .containsEntry("nullValue", null);
    }
}
