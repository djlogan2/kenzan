package david.logan.kenzan.jwt;

import javax.validation.constraints.NotNull;

//
//	Just the class for Jackson to serialize/deserialize
//	Clients call our login endpoint with this JSON object
//
public class Login {
	@NotNull
	public String username;
	@NotNull // Should we allow a null password? That would mean the user can not login at all
	public String password;
}
