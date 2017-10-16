package david.logan.kenzan.server;

public enum ErrorNumber {
	NONE,
	NO_AUTHORIZATION_HEADER,
	INVALID_USERNAME_OR_PASSWORD,
	INVALID_AUTHORIZATION_HEADER_NO_HEADER,
	INVALID_AUTHORIZATION_HEADER_NOT_BEARER,
	INVALID_AUTHORIZATION_TOKEN_PARSE_ERROR,
	INVALID_AUTHORIZATION_HEADER_NO_ALGORITHM,
	INVALID_AUTHORIZATION_HEADER_INVALID_ALGORITHM,
	INVALID_AUTHORIZATION_HEADER_NO_PAYLOAD,
	INVALID_AUTHORIZATION_PAYLOAD_NO_ISSUER,
	INVALID_AUTHORIZATION_PAYLOAD_INVALID_ISSUER,
	INVALID_AUTHORIZATION_PAYLOAD_INVALID_ISSUED,
	INVALID_AUTHORIZATION_PAYLOAD_NO_ISSUED,
	INVALID_AUTHORIZATION_PAYLOAD_INVALID_EXPIRATION,
	INVALID_AUTHORIZATION_PAYLOAD_NO_EXPIRATION,
	INVALID_AUTHORIZATION_PAYLOAD_NO_USERNAME,
	INVALID_AUTHORIATION_INVALID_SIGNATURE,
	NOT_AUTHORIZED_FOR_OPERATION,
	DUPLICATE_RECORD,
	CANNOT_DELETE_NONEXISTENT_RECORD,
	CANNOT_UPDATE_NONEXISTENT_RECORD,
	UNKNOWN_ERROR,
	CANNOT_INSERT_MISSING_FIELDS,
	CANNOT_INSERT_UNKNOWN_FIELDS
}