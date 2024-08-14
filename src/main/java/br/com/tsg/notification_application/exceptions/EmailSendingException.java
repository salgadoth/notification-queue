package br.com.tsg.notification_application.exceptions;

public class EmailSendingException extends Exception{
    public EmailSendingException(String message){
        super(message);
    }

    public EmailSendingException(String message, Throwable cause){
        super(message, cause);
    }
}

