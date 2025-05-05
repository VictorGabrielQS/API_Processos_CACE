package cace.processos_api.exception;

public class EntityInUseException  extends RuntimeException {
    public EntityInUseException(String message) {
        super(message);
    }
}
