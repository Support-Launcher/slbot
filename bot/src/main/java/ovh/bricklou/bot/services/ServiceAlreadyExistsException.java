package ovh.bricklou.bot.services;

import java.io.Serial;

public class ServiceAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = 8893743928912733931L;

    public ServiceAlreadyExistsException(String message) {
        super(message);
    }
}