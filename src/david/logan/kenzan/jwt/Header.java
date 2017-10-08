package david.logan.kenzan.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Header {
	//
	// There are a few others, if we extend this to support
	// REST APIs other than our test ones here, we can add
	// the appropriate fields.
	//
	@JsonProperty("alg")
	public String alg;
}
