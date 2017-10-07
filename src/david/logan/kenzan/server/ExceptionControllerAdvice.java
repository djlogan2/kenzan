package david.logan.kenzan.server;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionControllerAdvice {

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseBody
	public ErrorResponse exception(AccessDeniedException e)
	{
		return new ErrorResponse("Insufficient authority");
	}
}
