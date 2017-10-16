package david.logan.kenzan.server;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
