package david.logan.kenzan.jwt;

//
//	Just the class for Jackson to serialize/deserialize
//	Clients call our login endpoint with this JSON object
//
public class Login {
	public String username;
	public String password;
}
