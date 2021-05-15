package insight_centre.aceis.observations;

public class UserLocationReport extends SensorObservation {
	private String user;
	private GPSCoordinates coordinates;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public GPSCoordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(GPSCoordinates coordinates) {
		this.coordinates = coordinates;
	}

	public UserLocationReport(String sensor, GPSCoordinates value, String iri) {
		super(sensor, value, iri);
		// TODO Auto-generated constructor stub
	}

}
