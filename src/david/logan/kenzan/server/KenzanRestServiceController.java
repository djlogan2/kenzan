package david.logan.kenzan.server;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import david.logan.kenzan.db.Employee;
import david.logan.kenzan.db.KenzanDAO;
import david.logan.kenzan.jwt.JwtToken;
import david.logan.kenzan.jwt.Login;
import david.logan.kenzan.jwt.LoginResponse;

//
//	Obviously, the main guy. All standard stuff here, each endpoint is defined,
//	with @PreAuthorize when necessary, calling the DAO layer, and returning
//	appropriate data and responses.
//
@RestController
@RequestMapping("/rest")
public class KenzanRestServiceController {
	@Autowired
	KenzanDAO dbDAO;
	@RequestMapping("/get_emp")
	public Employee get_emp(@RequestParam(value="id", defaultValue="-1") int id)
	{
		return dbDAO.getEmployee(id);
	}
	
	@RequestMapping("/get_all")
	public List<Employee> get_all()
	{
		return dbDAO.getEmployees();
	}

	@RequestMapping(value = "/add_emp", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADD_EMP')")
	public ErrorResponse add_emp(@Validated @RequestBody Employee newEmployee)
	{
		int id = dbDAO.addEmployee(newEmployee);
		if(id == -1)
			return new ErrorResponse(id, ErrorNumber.DUPLICATE_RECORD, "Duplicate record");
		else
			return new ErrorResponse(id);
	}
	
	@RequestMapping("/delete_emp")
	@PreAuthorize("hasRole('DELETE_EMP')")
	public ErrorResponse delete_emp(@RequestParam(value="id", defaultValue="-1") int id)
	{
		if(dbDAO.deleteEmployee(id))
			return new ErrorResponse(id);
		else
			return new ErrorResponse(id, ErrorNumber.CANNOT_DELETE_NONEXISTENT_RECORD, "No records deleted");
	}
	
	@RequestMapping(value = "/update_emp", method = RequestMethod.POST)	
	@PreAuthorize("hasRole('UPDATE_EMP')")
	public ErrorResponse update_emp(@Validated @RequestBody Employee updatedEmployee)
	{
		if(updatedEmployee.isIdNull())
			return new ErrorResponse(ErrorNumber.CANNOT_INSERT_MISSING_FIELDS, "No records updated");
		
		if(dbDAO.updateEmployee(updatedEmployee))
			return new ErrorResponse(updatedEmployee.getId());
		else
			return new ErrorResponse(updatedEmployee.getId(), ErrorNumber.CANNOT_UPDATE_NONEXISTENT_RECORD, "No records updated");
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public LoginResponse login(@Validated @RequestBody Login login)
	{
		LoginResponse resp = new LoginResponse();
		Employee e = dbDAO.getEmployeeByUsername(login.username, login.password);
		if(e == null)
		{
			resp.error = "Unable to validate user/password combination";
			resp.errorcode = ErrorNumber.INVALID_USERNAME_OR_PASSWORD;
		}
		else
		{
			JwtToken token = new JwtToken(e);
			try {
				resp.errorcode = ErrorNumber.NONE;
				resp.jwt = token.getToken();
			} catch (Exception e1) {
				e1.printStackTrace();
				resp.jwt = null;
				resp.error = e1.getMessage();
				resp.errorcode = ErrorNumber.UNKNOWN_ERROR;
			}
		}
		return resp;
	}
	
	@RequestMapping(value = "/set_password", method = RequestMethod.POST)
	public ErrorResponse updatePassword(@Validated @RequestBody Login login)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("Passed in username=" + login.username + ", principal=" + authentication.getName());
		
		boolean authorized = authentication.getName().equals(login.username);

		if(!authorized)
			for(Iterator<? extends GrantedAuthority> i = authentication.getAuthorities().iterator() ; i.hasNext();)
				if(i.next().getAuthority().equals("ROLE_SET_PASSWORD")) authorized = true;
		
		if(!authorized)
			return new ErrorResponse(ErrorNumber.NOT_AUTHORIZED_FOR_OPERATION, "Not authorized");
		
		if(dbDAO.setPassword(login.username, login.password))
			return new ErrorResponse();
		else
			return new ErrorResponse(ErrorNumber.INVALID_USERNAME_OR_PASSWORD, "Unable to set password");
	}
}
