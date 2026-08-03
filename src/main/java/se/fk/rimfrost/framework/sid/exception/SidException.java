package se.fk.rimfrost.framework.sid.exception;

/**
 * Exception thrown by the SID adapter when the SID service returns an error
 * or an unexpected response.
 */
public class SidException extends Exception
{
   private final ErrorType errorType;

   /**
    * @param errorType the category of error
    * @param message   a description of the error
    */
   public SidException(ErrorType errorType, String message)
   {
      super(message);

      this.errorType = errorType;
   }

   /**
    * @param errorType the category of error
    * @param message   a description of the error
    * @param cause     the underlying exception
    */
   public SidException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);

      this.errorType = errorType;
   }

   /**
    * Returns the error type categorising this exception.
    *
    * @return the error type
    */
   public ErrorType getErrorType()
   {
      return errorType;
   }

   /**
    * Categorises the type of error returned by the SID service.
    */
   public enum ErrorType
   {
      NOT_FOUND, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR
   }
}
