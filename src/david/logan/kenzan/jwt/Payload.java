package david.logan.kenzan.jwt;

import java.util.ArrayList;
import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

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
