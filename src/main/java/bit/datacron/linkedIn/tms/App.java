package bit.datacron.linkedIn.tms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bit.datacron.linkedIn.tms.gui.LanterminalEngine;
import bit.datacron.linkedIn.tms.map.Location;
import bit.datacron.linkedIn.tms.physics.AmbiTemperRegulator;
import bit.datacron.linkedIn.tms.system.AmbientTemperatureSensor;
import bit.datacron.linkedIn.tms.system.TemperatureMonitoringSystem;
import bit.datacron.linkedIn.tms.system.TemperatureSensor;

public class App {
	
	public static void main( String[] args ) {

		// Setting up new locations to work with
		List<Location> locationList = new ArrayList<Location>();
		locationList.add(new Location("Power Generator"));
		locationList.add(new Location("Basement"));
		locationList.add(new Location("Medical Room"));
		locationList.add(new Location("Comms Control"));
		locationList.add(new Location("Supply Storage"));
		locationList.add(new Location("Staff Quarters"));
		locationList.add(new Location("Server Farm"));
		locationList.add(new Location("Computronium C"));
		locationList.add(new Location("Continuity B"));
		
		// Declaring an array of sensors and allocating as much memory as the length of the locations list.
		TemperatureSensor[] sensorList = new AmbientTemperatureSensor[locationList.size()];
		// Initializing the array with the new sensors while assigning the location temperatures
		for (int i = 0; i < sensorList.length; i++) {
			sensorList[i] = new AmbientTemperatureSensor(locationList.get(i).getAmbientTemp());
		}
		
		// Setting up physics; ambient temperature regulator - controller - simulator
		AmbiTemperRegulator tCS = new AmbiTemperRegulator(25.0, 0.5);
		Thread tCSThread = new Thread(tCS);
		for (int i = 0; i < locationList.size(); i++) {
			((AmbiTemperRegulator) tCS).addAmbientTemperature(locationList.get(i).getAmbientTemp());
		}
		tCSThread.start();
		
		// Setting up temperature monitoring system.
		TemperatureMonitoringSystem tMS = new TemperatureMonitoringSystem(sensorList, 500);
		Thread tMSThread = new Thread(tMS);
		tMSThread.start();
		
		// Setting up terminal
		LanterminalEngine lE = new LanterminalEngine(tMS);
		
		// Convert the locations to simple strings to use with SSI
		String[] locationDescriptions = new String[locationList.size()];
		int i = -1;
		for (Location loc : locationList) {
			i++;
			locationDescriptions[i] = loc.getDescription();
		}
		lE.setLocationDescriptions(locationDescriptions);
		
		// Finally, turn on our monitor
		try {
			lE.turnOn();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Throwing active threads to the lions; clean up and exit
		System.out.println("Killing threads; exit");
		tCS.terminate();
		tMS.terminate();
		while (tCSThread.isAlive() || tMSThread.isAlive() ) { }
		System.exit(0);
						
	}
	
		
}