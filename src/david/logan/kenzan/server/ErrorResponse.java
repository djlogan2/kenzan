package david.logan.kenzan.server;

public class ErrorResponse {
	public String error;
	public int id;
	public ErrorResponse() {}
	public ErrorResponse(String error) { this.error = error; }
	public ErrorResponse(int id) { this.error = "ok"; this.id = id; }
}
