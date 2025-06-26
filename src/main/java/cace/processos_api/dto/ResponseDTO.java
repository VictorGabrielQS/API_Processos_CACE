package cace.processos_api.dto;


public class ResponseDTO<T> {
    private boolean found;
    private T data;
    private String message;

    public ResponseDTO(boolean found, T data, String message) {
        this.found = found;
        this.data = data;
        this.message = message;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
