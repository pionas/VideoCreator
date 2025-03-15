package pl.excellentapp.ekonkursy.core.exception;

public class NoArticlesException extends RuntimeException {

    public NoArticlesException() {
        super("No articles found! Video creation aborted.");
    }
}
