package bit.datacron.linkedIn.tms.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AmbiTemperRegulator implements Runnable {
	
	private List<AmbientTemperature> aTL;
	private double avgTemp;
	private double offset;
	private boolean isOn;

	public AmbiTemperRegulator(double avgTemp, double offset) {
		this.avgTemp = avgTemp;
		this.offset = offset;
		aTL = new ArrayList<AmbientTemperature>();
		isOn = true;
	}

	public void addAmbientTemperature(AmbientTemperature aT) {
		aTL.add(aT);
	}

	public void run() {
	
		// Initializing temperatures with the avgTemp value
		this.initialize();

		while(isOn) {
			this.simulate();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void simulate() {
		
		double cTemp;
		int n;
	
		for (int i = 0; i < aTL.size(); i++) {
			cTemp = aTL.get(i).getTemp();
			n = ThreadLocalRandom.current().nextInt(2);
			if (n == 0)
				n = - 1;
			aTL.get(i).setTemp(cTemp + (ThreadLocalRandom.current().nextDouble(offset))*n);
		}
	}
	
	private synchronized void initialize() {
		aTL.forEach( aT -> aT.setTemp(avgTemp));
	}
	
	public void terminate() {
		isOn = false;
	}
	
	
}
