package david.logan.kenzan.jwt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Iterator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import david.logan.kenzan.db.Employee;
import david.logan.kenzan.db.EmployeeRole;
import david.logan.kenzan.server.AppConfigXML;
import david.logan.kenzan.server.ErrorNumber;
import david.logan.kenzan.server.JWTAuthenticationServiceException;

//
//	This class pulls apart the JWT token, and validates the various fields.
//	It will also create a token upon request. If you created the token with
//	the constructore JwtToken(Employee emp), the generated token will have all
//	valid information in the payload.
//
public class JwtToken {
	private Header header;
	private Payload payload;
	
	public ArrayList<String> getRoles() { return payload.roles; }
	public String getUsername() { return payload.username; }
	
	public JwtToken(String token)
	{
		if(token == null || token.isEmpty())
			throw new JWTAuthenticationServiceException(ErrorNumber.NO_AUTHORIZATION_TOKEN);
		
		if(!token.startsWith("Bearer "))
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_TOKEN_NO_BEARER);
		
		String[] triples = token.replaceAll("Bearer ", "").split("\\.");
		
		if(triples == null || triples.length != 3)
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_TOKEN_PARSE_ERROR);
		
		ObjectMapper mapper = new ObjectMapper();
		String header = new String(Base64.getDecoder().decode(triples[0]));
		String payload = new String(Base64.getDecoder().decode(triples[1].getBytes()));
		byte[] signature = Base64.getDecoder().decode(triples[2].getBytes());
		
		try {
			this.header = mapper.readValue(header,  Header.class);
			this.payload = mapper.readValue(payload,  Payload.class);
		} catch (IOException e1) {
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_TOKEN_PARSE_ERROR);
		}
		
		if(this.header.alg == null || !this.header.alg.equals("HS256"))
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_HEADER_INVALID_ALGORITHM);
		
		if(this.payload.issuer == null || this.payload.issuer.isEmpty())
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_PAYLOAD_NO_ISSUER);
		if(!this.payload.issuer.equals(AppConfigXML.getProperties().getProperty("kenzan.jwt.issuer")))
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_PAYLOAD_INVALID_ISSUER);
				
		if(this.payload.atissued == null)
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_PAYLOAD_NO_ISSUED);
		
		if(this.payload.expiration == null)
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_PAYLOAD_NO_EXPIRATION);
		
		if(this.payload.atissued.after(this.payload.expiration) || this.payload.atissued.after(Calendar.getInstance()))
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_PAYLOAD_INVALID_ISSUED);
		
		if(this.payload.username == null || this.payload.username.isEmpty())
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_PAYLOAD_NO_USERNAME);

		Charset asciiCs = Charset.forName("US-ASCII");
        Mac sha256_HMAC;
		try {
			sha256_HMAC = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(asciiCs.encode(AppConfigXML.getProperties().getProperty("kenzan.jwt.signing.key")).array(), "HmacSHA256");
	        sha256_HMAC.init(secret_key);
	        byte[] mac_data = sha256_HMAC.doFinal(asciiCs.encode(triples[0] + "." + triples[1]).array());
	        if(!Arrays.equals(mac_data, signature))
	        		throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_TOKEN_INVALID_SIGNATURE);
		} catch (NoSuchAlgorithmException e) {
			throw new JWTAuthenticationServiceException(ErrorNumber.UNKNOWN_ERROR, e.getMessage());
		} catch (InvalidKeyException e) {
    			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_TOKEN_INVALID_SIGNATURE, e.getMessage());
		}
		
		if(Calendar.getInstance().after(this.payload.expiration))
			throw new JWTAuthenticationServiceException(ErrorNumber.INVALID_AUTHORIZATION_TOKEN_EXPIRED);
	}
	
	public JwtToken(Employee emp)
	{
		header = new Header();
		header.alg = "HS256";
		
		payload = new Payload();
		payload.issuer = AppConfigXML.getProperties().getProperty("kenzan.jwt.issuer");
		payload.atissued = Calendar.getInstance();
		payload.expiration = (Calendar) payload.atissued.clone();
		payload.expiration.add(Calendar.MINUTE, Integer.parseInt(AppConfigXML.getProperties().getProperty("kenzan.jwt.expiration.minutes")));
		payload.username = emp.getUsername();
		payload.roles = new ArrayList<String>();
		for(Iterator<EmployeeRole> i = emp.getRoles().iterator() ; i.hasNext() ; )
		{
			EmployeeRole er = i.next();
			payload.roles.add(er.getRole());
		}
	}
	
	public String getToken() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException
	{
		ObjectMapper mapper = new ObjectMapper();
		String jsonHeader = mapper.writeValueAsString(header);
		String jsonPayload = mapper.writeValueAsString(payload);
		String retString = Base64.getEncoder().encodeToString(jsonHeader.getBytes()) + "." + Base64.getEncoder().encodeToString(jsonPayload.getBytes());
		Charset asciiCs = Charset.forName("US-ASCII");
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(asciiCs.encode(AppConfigXML.getProperties().getProperty("kenzan.jwt.signing.key")).array(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] mac_data = sha256_HMAC.doFinal(retString.getBytes());
        retString = retString + "." + Base64.getEncoder().encodeToString(mac_data);
        return "Bearer " + retString;
	}
}
