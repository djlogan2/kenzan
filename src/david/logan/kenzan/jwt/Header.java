package david.logan.kenzan.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Header {
	@JsonProperty("alg")
	public String alg;
}
