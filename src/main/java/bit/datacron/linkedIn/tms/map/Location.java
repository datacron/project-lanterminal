package bit.datacron.linkedIn.tms.map;

import bit.datacron.linkedIn.tms.physics.AmbientTemperature;

public class Location {
	
	private String description;
	private AmbientTemperature ambientTemp;
	
	public Location(String description) {
		this.description = description;
		ambientTemp = new AmbientTemperature();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public AmbientTemperature getAmbientTemp() {
		return ambientTemp;
	}	

}
