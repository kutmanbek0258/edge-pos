package kg.edgepos.authservice.exception;

import kg.edgepos.authservice.type.ErrorLevel;

public class RegistrationException extends InformationException {

    public RegistrationException(String description) {
        super(description, null, ErrorLevel.ERROR);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause, ErrorLevel.ERROR);
    }
}
