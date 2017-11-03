package david.logan.kenzan.server;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

//
//	This guy tells spring to use this method whenever there is an access denied
//	error from the @PreAuthorize annotation. It allows JSON returns, rather than
//	the XML/HTML "403" norm.
//
@ControllerAdvice
public class ExceptionControllerAdvice {

	@ExceptionHandler(AccessDeniedException.class)
	public @ResponseBody ErrorResponse exception(AccessDeniedException e)
	{
		System.out.println("AccessDeniedException message: " + e.getMessage());
		return new ErrorResponse(ErrorNumber.NOT_AUTHORIZED_FOR_OPERATION, "Insufficient authority");
	}

	@ExceptionHandler(GenericJDBCException.class)
	public @ResponseBody ErrorResponse exception2(GenericJDBCException e)
	{
		System.out.println("PersistenceException message: " + e.getMessage());
		if(e.getCause().getMessage().equals("Unable to insert duplicate username"))
			return new ErrorResponse(ErrorNumber.DUPLICATE_RECORD, "Cannot insert or update duplicate record");
		else
			return new ErrorResponse(ErrorNumber.UNKNOWN_ERROR, e.getCause().getMessage());
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public @ResponseBody ErrorResponse exception3(ConstraintViolationException e)
	{
		return new ErrorResponse(ErrorNumber.CANNOT_INSERT_MISSING_FIELDS, e.getCause().getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public @ResponseBody ErrorResponse exception4(MethodArgumentNotValidException e)
	{
		System.out.println("4: " + e.getLocalizedMessage() + "," + e.getMessage() + "," + e.getParameter());
		if(e.getBindingResult().getFieldError().getRejectedValue() == null)
			if(e.getBindingResult().getObjectName().equals("employee"))
				return new ErrorResponse(ErrorNumber.CANNOT_INSERT_MISSING_FIELDS, e.getBindingResult().getFieldError().getField() + " cannot be null");
			else
				return new ErrorResponse(ErrorNumber.INVALID_USERNAME_OR_PASSWORD, e.getBindingResult().getFieldError().getField() + " cannot be null");
		else
			return new ErrorResponse(ErrorNumber.UNKNOWN_ERROR, e.getMessage());
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public @ResponseBody ErrorResponse exception5(HttpMessageNotReadableException e)
	{
		System.out.println("5: " + e.getLocalizedMessage() + "," + e.getMessage());
		//if(e.getCause() != null && (e.getCause() instanceof HttpMessageNotReadableException))
		//	return new ErrorResponse(ErrorNumber.CANNOT_INSERT_MISSING_FIELDS, e.getMessage());
		if( e.getCause() == null || !(e.getCause() instanceof UnrecognizedPropertyException) )
			if(e.getMessage().contains("Required request body is missing"))
				if(e.getMessage().contains("jwt.Login"))
					return new ErrorResponse(ErrorNumber.INVALID_USERNAME_OR_PASSWORD, e.getMessage());
				else
					return new ErrorResponse(ErrorNumber.CANNOT_INSERT_MISSING_FIELDS, "Some other body is missing: " + e.getMessage());
			else
				return new ErrorResponse(ErrorNumber.UNKNOWN_ERROR, "Not a missing property, not a missing response body: " + e.getMessage());

		UnrecognizedPropertyException upe = (UnrecognizedPropertyException) e.getCause();
		if(upe.getReferringClass().getName().endsWith(".Employee"))
			return new ErrorResponse(ErrorNumber.CANNOT_INSERT_UNKNOWN_FIELDS, upe.getPropertyName() + " is an unknown field");
		else
			return new ErrorResponse(ErrorNumber.INVALID_USERNAME_OR_PASSWORD, upe.getPropertyName() + " is an unknown field");
	}
}
