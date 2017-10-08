package david.logan.kenzan.jwt;

import java.util.ArrayList;
import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

//
//	The payload section of the JWT. Again, we are only using the fields
//	important to our own token. If we extend this to work with other REST
//	APIs, we will likely have to extend this. One thing I know for sure is
//	that the hard coded fields are currently defined in the JWT standard.
//	However, past that, the payload can contain ANY valid JSON data,
//	from fields to arrays to maps, etc. It's 100% extensible past the
//	standard field names. In our version, since we know what we want to
//	have in the extensible portion, we put in the ArrayList<String> of
//	valid roles, which can then be inserted directly into the Authentication
//	object as SimpleGrantedAuthority objects. As spring requires, the role strings
//	will be ROLE_<whatever>, and you'll see in the REST controller hasRole('<whatever>')
//
//	By the way, none of the code validates the JWT against the database. If the token
//	is valid, and it's correctly signed, it takes the roles from the token WITHOUT revalidating
//	from the database. That means that a user with a valid token will have up to "expiration minutes"
//	minutes to fiddle around before he or she has a role change go into effect.
//
public class Payload {
	@JsonProperty("iss")
	public String issuer;
	@JsonProperty("exp")
	public Calendar expiration;
	@JsonProperty("atIssued")
	public Calendar atissued;
	@JsonProperty("username")
	public String username;
	@JsonProperty("roles")
	public ArrayList<String> roles;
}
