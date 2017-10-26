package bit.datacron.linkedIn.tms.system;

public class TemperatureMonitoringSystem implements Runnable {
	
	private boolean monitoringEnabled;
	private int refreshRate;  // milliseconds
	private TemperatureSensor[] tSensors;
	private double[] temperatures;
	
	public TemperatureMonitoringSystem(TemperatureSensor[] tSensors, int refreshRate) {
		this.tSensors = tSensors;
		this.monitoringEnabled = true;
		this.refreshRate = refreshRate;
		this.temperatures = new double[tSensors.length];
	}
	
	public void run() {	
		while (monitoringEnabled) {
				
			try {
				Thread.sleep(refreshRate);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public double[] getTemperatures() {
		for (int i = 0; i < tSensors.length; i++) {
			temperatures[i] = tSensors[i].getTemperature();
		}
		return temperatures;
	}
	
	public void terminate() {
		monitoringEnabled = false;
	}
	

}
