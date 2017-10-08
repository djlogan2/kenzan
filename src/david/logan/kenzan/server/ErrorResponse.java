package david.logan.kenzan.server;

//
//	Error response JSON object.
//	TODO: Ensure that every "non-data" return returns one of these in every situation.
//
public class ErrorResponse {
	public String error;
	public int id;
	public ErrorResponse() {}
	public ErrorResponse(String error) { this.error = error; }
	public ErrorResponse(int id) { this.error = "ok"; this.id = id; }
}
