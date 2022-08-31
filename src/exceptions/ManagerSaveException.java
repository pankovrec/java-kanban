package exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(ManagerSaveException e) {
        super(e);
    }
}
