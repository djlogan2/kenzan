package david.logan.kenzan.server;

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
		return new ErrorResponse("Insufficient authority");
	}
}
