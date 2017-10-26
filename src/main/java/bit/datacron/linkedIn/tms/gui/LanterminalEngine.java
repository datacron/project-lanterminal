package bit.datacron.linkedIn.tms.gui;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import bit.datacron.linkedIn.tms.system.TemperatureMonitoringSystem;

public class LanterminalEngine {
	
	private TemperatureMonitoringSystem tMS;
	private Screen screen;
	private TextColor foreC, backC;
	
	private boolean isOn;
	private String[] locationDescriptions;
	private double tempThreshold = 50;
	private int[] posX = {3, 8, 25, 70};
	private String[] labels = { "#", "LOCATION", "TEMPERATURE", "STATUS" };
		
	public LanterminalEngine(TemperatureMonitoringSystem tMS) {
		try {
			this.tMS = tMS;
			this.initialize();
		} catch (IOException e) {
			// TODO more
			e.printStackTrace();
		}
	}
	
	private void initialize() throws IOException {
		screen = new DefaultTerminalFactory().createScreen();
		foreC = TextColor.ANSI.RED;
		backC = TextColor.ANSI.BLACK;
	}
	
	public void turnOn() throws IOException {
		screen.startScreen();
		setup();
		drawUI("  SSI TERMINAL v1.0 ", "  F10: Quit  ");
		isOn = true;
		displayData();
		screen.stopScreen();
	}
	
	public void turnOff() throws IOException {
		isOn = false;
		screen.stopScreen();
	}
	
	public void drawUI(String header, String footer) throws IOException {
		
		// Disabling cursor visibility
		screen.setCursorPosition(null);
		
		// Adjusting dimension values for zero indexes.
		int width = screen.getTerminalSize().getColumns()-1;
		int height = screen.getTerminalSize().getRows()-1;
		
		// Clearing the screen, creating header & footer with inverted colors.
		screen.clear();
		screen.newTextGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
				.withBackgroundColor(foreC).withForegroundColor(backC));
		screen.newTextGraphics().drawLine(0, height, width, height, new TextCharacter(' ')
				.withBackgroundColor(foreC).withForegroundColor(backC));
		screen.newTextGraphics().setBackgroundColor(foreC).setForegroundColor(backC)
		  .putCSIStyledString(0, 0, header);
		screen.newTextGraphics().setBackgroundColor(foreC).setForegroundColor(backC)
		  .putCSIStyledString(0, height, footer);
		
		// Using a key-value data structure for mapping data descriptions to screen. 
		Map<Integer, String> colSetup = new HashMap<>();
		for (int i = 0; i < posX.length; i++) {
			colSetup.put(posX[i], labels[i]);
		}
		
		int row = 2;  // Row value is common
		
		// Iterating over HashMap and casting Lanterna Spell; 
		colSetup.forEach( (k,v) -> screen.newTextGraphics().putString(k, row, v));
		
		// Drawing the double line box
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(0, 1, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
		screen.newTextGraphics().drawLine(1, 1, width-1, 1, new TextCharacter(Symbols.DOUBLE_LINE_HORIZONTAL)
				.withForegroundColor(ANSI.YELLOW));
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(width, 1, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(0, 2, Symbols.DOUBLE_LINE_VERTICAL);
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(width, 2, Symbols.DOUBLE_LINE_VERTICAL);
		
		
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(0, 3, Symbols.DOUBLE_LINE_T_RIGHT);
		screen.newTextGraphics().drawLine(1, 3, width-1, 3, new TextCharacter(Symbols.DOUBLE_LINE_HORIZONTAL)
				.withForegroundColor(ANSI.YELLOW));
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(width, 3, Symbols.DOUBLE_LINE_T_LEFT);
		
		screen.newTextGraphics().drawLine(0, 4, 0, height-3, new TextCharacter(Symbols.DOUBLE_LINE_VERTICAL)
				.withForegroundColor(ANSI.YELLOW));
		screen.newTextGraphics().drawLine(width, 4, width, height-3, new TextCharacter(Symbols.DOUBLE_LINE_VERTICAL)
				.withForegroundColor(ANSI.YELLOW));
		
		//DOUBLE_LINE_BOTTOM_LEFT_CORNER
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(0, height-2, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
		screen.newTextGraphics().drawLine(1, height-2, width-1, height-2, new TextCharacter(Symbols.DOUBLE_LINE_HORIZONTAL)
				.withForegroundColor(ANSI.YELLOW));
		screen.newTextGraphics().setForegroundColor(ANSI.YELLOW).setCharacter(width, height-2, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
		
		// Highest temperature statistic
		screen.newTextGraphics().putString(posX[0], height-1, "Highest Temperature Record: ");
		
		// Finally, refreshing the screen to make our changes visible.
		screen.refresh();		
	}
	
	private void displayData() throws IOException {
		// Row Starting Position and offset for each row
		int rowDefault = 2;
		int row = rowDefault;
		int rowOffset = 2;
		
		// Now lengthy temperature numbers shouldn't be a problem
		String dPattern = "#.#";
		DecimalFormat decimalFormat = new DecimalFormat(dPattern);
		
		// Temperature bar setup
		double temp;
		Map<Character, Integer> tBarBorders = new HashMap<>();
		char markIn = '[';
		char markOut = ']';
		tBarBorders.put(markIn, posX[2]+5);
		tBarBorders.put(markOut, posX[3]-3);
		int barIn = tBarBorders.get(markIn)+1;
		int barOut;
		int maxBarOut = tBarBorders.get(markOut)-1;
		int maxBarLength = maxBarOut - barIn;
		int barLength = 0;
		char barSpacer = Symbols.BLOCK_SPARSE; //'-';
		char barFiller = Symbols.BLOCK_SOLID; //'#';
		
		// Status setup
		String status = "";
		Map<String, TextColor> statusMap = new HashMap<>();
		statusMap.put("COLD", ANSI.WHITE);
		statusMap.put("COOL", ANSI.CYAN);
		statusMap.put("NORMAL", ANSI.GREEN);
		statusMap.put("WARM", ANSI.YELLOW);
		statusMap.put("HOT", ANSI.RED);
		
		// Keystroke setup
		KeyStroke keyStroke;
		
		// Highest temperature recorded
		Double highestTemp = 0.0;
		int highestTempIndex = 0;
		
		
		
		while (isOn) {
			
			// Monitoring keyboard input
			keyStroke = screen.pollInput();
			if (keyStroke != null && keyStroke.getKeyType() == KeyType.F10) {
				screen.clear();
				cursorWait(0, 2, 666);
				typeln(">_ SYSTEM EXIT HOTKEY ON", 0, 0);
				cursorWait(0, 2, 1111);
				typeln(">_ SESSION TERMINATED", 0, 1);
				cursorWait(0, 2, 1111);
				break;
			}
				
			
			// Iterating over sensors and displaying data
			for (int i = 0; i < tMS.getTemperatures().length; i++) {
				row += rowOffset;
				
				// Sensor number
				screen.newTextGraphics().setForegroundColor(foreC)
				  .putCSIStyledString(posX[0], row, Integer.toString(i+1));
				
				// Sensor location
				screen.newTextGraphics().setForegroundColor(ANSI.WHITE)
				  .putCSIStyledString(posX[1], row, locationDescriptions[i]);
				
				// Sensor Temperature (formatting decimal number)
				temp = tMS.getTemperatures()[i];
				if (highestTemp < temp) {
					highestTemp = temp;
					highestTempIndex = i;
				}
					
				screen.newTextGraphics().setForegroundColor(ANSI.GREEN)
				  .putCSIStyledString(posX[2], row, decimalFormat.format(temp));
				
				// Sensor temperature bar
				screen.setCharacter(tBarBorders.get(markIn), row, new TextCharacter(markIn));
				screen.newTextGraphics().drawLine(barIn, row, maxBarOut, row, new TextCharacter(barSpacer)
						.withForegroundColor(foreC));
				screen.setCharacter(tBarBorders.get(markOut), row, new TextCharacter(markOut));
				
				// Calculating bar length
				barLength = new Double( (temp * maxBarLength) / tempThreshold) .intValue();
				barOut = barLength+barIn;
				
				// Validating bar length
				if (barOut > maxBarOut) {
					barOut = maxBarOut;
				} else if (barOut < barIn) {
					barOut = barIn;
				}
				
				// Creating the bar
				screen.newTextGraphics().drawLine(barIn, row, barOut, row,
						new TextCharacter(barFiller).withForegroundColor(foreC));
				
				
				// Mapping each status to a specific ANSI COLOR 
				if (temp < 10) {
					status = "COLD";
				} else if (temp < 20) {
					status = "COOL";
				} else if (temp < 30) {
					status = "NORMAL";
				} else if (temp < 35) {
					status = "WARM";
				} else if (temp > 35) {
					status = "HOT";
				}
				
				
				// Making sure that status target "surface" is clean
				screen.newTextGraphics().drawLine(posX[3], row, posX[3]+6, row, new TextCharacter(' ')
						.withForegroundColor(foreC));
				// Displaying status with the mapped color
				screen.newTextGraphics().setForegroundColor(statusMap.get(status))
					.putCSIStyledString(posX[3], row, status);
				
				// Displaying highest temperature recorded (if there's a temperature change)
				if (highestTemp.equals(temp)) {
					int height = screen.getTerminalSize().getRows()-1;
					int width = screen.getTerminalSize().getColumns()-1;
					screen.newTextGraphics().drawLine(posX[2]+6, height-1,  width-1, height-1, ' ');
					screen.newTextGraphics().setForegroundColor(ANSI.YELLOW)
					.putString(posX[2]+6, height-1, locationDescriptions[highestTempIndex] 
							+ " " + decimalFormat.format(temp));
				}
				
			
			}
			
			row = rowDefault;
			
			// Make changes visible.
			screen.refresh();
			
		}
	}
	
	
	private void setup() throws IOException {
		cursorWait(0, 0, 1111);
		typeln(">_ SENSOR SHADOW INTERFACE READY", 0, 0);
		cursorWait(0, 0, 999);
		typeln(">_ INITIALIZING TEMPERATURE SUPPORT TRANSMUTATOR MATRIX", 0, 4);
		cursorWait(0, 0, 888);
		typeln(">_ ....................................................", 0, 6);
		cursorWait(0, 0, 777);
		typeln(">_ CONNECTING TO " + tMS.getTemperatures().length + "-SENSOR SYSTEM", 0, 6);
		cursorWait(0, 0, 666);
	}
	
	public void cursorWait(int col, int row, int millis) {
		
		screen.setCursorPosition(null);
		//screen.setCursorPosition(new TerminalPosition(col, row));
		
		try {
			screen.refresh();
			
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Remember MOTHER? This is fun!
	public void typeln(String msg, int col, int row) {
		TextColor defC = foreC;
		foreC = TextColor.ANSI.GREEN;
		int interval = 11;
				
		for (int i = 0; i < msg.length(); i++) {
			screen.setCursorPosition(new TerminalPosition(col+i, row));
			screen.setCharacter( (col+i), row, new TextCharacter(msg.charAt(i), foreC, backC));
			
			try {
				screen.refresh();
				Thread.sleep(ThreadLocalRandom.current().nextInt(interval*3));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		foreC = defC;
	}

	public String[] getLocationDescriptions() {
		return locationDescriptions;
	}

	public void setLocationDescriptions(String[] locationDescriptions) {
		this.locationDescriptions = locationDescriptions;
	}

}
