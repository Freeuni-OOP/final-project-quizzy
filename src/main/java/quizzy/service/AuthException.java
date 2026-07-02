package quizzy.service;

// thrown when registration/login input is bad in a way we want to show the user (message is safe to display)
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
