package exceptions;

import java.io.*;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(ManagerSaveException e) {
        super(e);
    }
}
