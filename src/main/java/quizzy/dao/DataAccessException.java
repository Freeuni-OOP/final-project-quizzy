package quizzy.dao;

// wraps SQLException so the service/servlet layers don't have to deal with checked JDBC exceptions
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
