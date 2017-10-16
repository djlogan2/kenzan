package david.logan.kenzan.server;

//
//	Error response JSON object.
//
public class ErrorResponse {
	public String error;
	public ErrorNumber errorcode;
	public int id;
	public ErrorResponse() { errorcode = ErrorNumber.NONE; this.id = 0; this.error = null; }
	public ErrorResponse(ErrorNumber errorcode, String error) { this.errorcode = errorcode; this.error = error; id = 0; }
	public ErrorResponse(int id, ErrorNumber errorcode, String error)
		{ this.errorcode = errorcode; this.error = error; this.id = id; }
	public ErrorResponse(int id) { this.error = null; this.errorcode = ErrorNumber.NONE; this.id = id; }
}
