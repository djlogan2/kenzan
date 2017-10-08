package david.logan.kenzan.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.TimeZone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import david.logan.kenzan.db.Employee;
import david.logan.kenzan.jwt.Login;
import david.logan.kenzan.jwt.LoginResponse;
import david.logan.kenzan.server.ErrorResponse;

//
//	The REST client. I wrote it to use in the unit tests, but with little to no work,
//	it should be usable in production if need be. Obviously if we do that, we'll have
//	to split this package up, along with some of the DB classes and REST response classes.
//
//	But in general, it works pretty well.
//
//	TODO: Do a way better job returning something sane if the server is down!
//
public class KenzanRestClient {

	private String username;
	private String jwt;
	
	public KenzanRestClient() {}
	
	private <T> T executeAPI(String api, Object data, TypeReference<T> typeRef) //Class<?> clazz)
	{
			try {
				String parameters = "";
				if(api == "get_emp" || api == "delete_emp")
					parameters = "?id=" + (int)data;
				URL url = new URL("http://localhost:8080/Kenzan/rest/" + api + parameters);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				switch(api)
				{
				case "login":
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);
					break;
				case "get_all":
				case "get_emp":
				case "delete_emp":
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Authorization",  jwt);
					conn.setDoOutput(true);
					break;
				default:
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Authorization",  jwt);
					conn.setDoOutput(true);
					break;
				}
				conn.setRequestProperty("Content-Type",  "application/json");
				conn.setRequestProperty("Accept", "application/json");
		
				ObjectMapper mapper = new ObjectMapper();

				if(data != null && api != "get_emp" && api != "delete_emp") {
					mapper.writeValue(conn.getOutputStream(), data);
					conn.getOutputStream().close();
				}
				
				if (conn.getResponseCode() != 200) {
					if(typeRef.getType() instanceof ErrorResponse)
					{
						ErrorResponse resp = new ErrorResponse();
						resp.error = conn.getResponseMessage();
						resp.id = 0;
						return (T)resp;
					}
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
		
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dateFormat.setTimeZone(TimeZone.getTimeZone("America/Denver"));
				mapper.setDateFormat(dateFormat);
				mapper.setTimeZone(TimeZone.getTimeZone("America/Denver"));
				
				T newObj = null;
				try {
					newObj = mapper.readValue(conn.getInputStream(), typeRef);
				} catch(MismatchedInputException e) {
					// This happens when we have no data to map,
					// so just fall through.
				}
				
				conn.getInputStream().close();
				conn.disconnect();
				return newObj;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
	
	public boolean login(String username, String password)
	{
		Login login = new Login();
		this.username = login.username = username;
		login.password = password;
		LoginResponse resp = (LoginResponse)executeAPI("login", login, new TypeReference<LoginResponse>() {});
		if(resp.error == null && resp.jwt != null) {
			this.jwt = resp.jwt;
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Employee> getAllEmployees()
	{
		//ArrayList<Employee> employee_list = new ArrayList<Employee>();
		return (ArrayList<Employee>) executeAPI("get_all", null, new TypeReference<ArrayList<Employee>>() {});
	}
	
	public Employee getEmployee(int id)
	{
		return (Employee) executeAPI("get_emp", id, new TypeReference<Employee>() {});
	}
	
	public int addEmployee(Employee e)
	{
		ErrorResponse resp = (ErrorResponse) executeAPI("add_emp", e, new TypeReference<ErrorResponse>() {});
		if(resp != null && resp.error != null && resp.error.equals("ok"))
			return resp.id;
		else
			return -1;
	}

	public boolean updateEmployee(Employee e)
	{
		ErrorResponse resp = (ErrorResponse) executeAPI("update_emp", e, new TypeReference<ErrorResponse>() {});
		return resp != null && resp.error != null && resp.error.equals("ok");
	}
	
	public boolean deleteEmployee(Employee e)
	{
		ErrorResponse resp = (ErrorResponse) executeAPI("delete_emp", e.getId(), new TypeReference<ErrorResponse>() {});
		return resp != null && resp.error != null && resp.error.equals("ok");
	}
	
	public String getUsername() { return username; }
}
