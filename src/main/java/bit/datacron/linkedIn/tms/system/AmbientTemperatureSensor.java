package bit.datacron.linkedIn.tms.system;

import bit.datacron.linkedIn.tms.physics.AmbientTemperature;

public class AmbientTemperatureSensor implements TemperatureSensor {
	
	private AmbientTemperature ambientTemperature;
	
	public AmbientTemperatureSensor(AmbientTemperature ambientTemperature) {
		this.ambientTemperature = ambientTemperature;
	}

	public double measureHeat() {
		return ambientTemperature.getTemp();
	}

	public double getTemperature() {
		return measureHeat();
	}

}
