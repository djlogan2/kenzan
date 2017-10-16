package david.logan.kenzan.server;

//
//	Error response JSON object.
//	TODO: Ensure that every "non-data" return returns one of these in every situation.
//
public class ErrorResponse {
	public String error;
	public ErrorNumber errorcode;
	public int id;
	public ErrorResponse() { errorcode = ErrorNumber.NONE; this.id = 0; this.error = null; }
	public ErrorResponse(ErrorNumber errorcode, String error) { this.errorcode = errorcode; this.error = error; }
	public ErrorResponse(int id) { this.error = null; this.errorcode = ErrorNumber.NONE; this.id = id; }
}
