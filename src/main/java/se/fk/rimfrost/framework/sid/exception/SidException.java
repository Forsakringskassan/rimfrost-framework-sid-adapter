package se.fk.rimfrost.framework.sid.exception;

public class SidException extends Exception
{
   private final ErrorType errorType;

   public SidException(ErrorType errorType, String message)
   {
      super(message);

      this.errorType = errorType;
   }

   public SidException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);

      this.errorType = errorType;
   }

   public ErrorType getErrorType()
   {
      return errorType;
   }

   public enum ErrorType
   {
      NOT_FOUND, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR
   }
}
