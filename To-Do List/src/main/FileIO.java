package main;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import support.Date.DateFormat;
import support.Event;

/**
 * 
 * Provides input and output for the file that stores data for this program.<br>
 * Data are read from the file and put into private static fields that have their getter and (usually) setter methods.<br>
 * Methods that modify data, such as any setter methods, will ONLY modify the static fields—NOT THE FILE.<br>
 * Data are only saved to the file when the <code>saveToFile()</code> method is called.
 * 
 * 
 * @author James
 * @version 1.2
 */
public abstract class FileIO {

	/**
	 * The directory of the data file
	 */
	public static final String FILE_DIR = "todolist.dat";
	
	/**
	 * The version of FileIO. This prevents improper reading of files that were written by a different FileIO version (which has a different output).<br>Files, no matter the version, should begin with a String representation of the FileIO version that last edited it.
	 */
	public static final String IO_VERSION = "1.2";
	
	/**The file's version*/
	private static String originalFileVersion; 
	/**Setting: true if user is using dark mode; Property added in FileIO version 1.0*/
	private static boolean usesDarkMode; 
	/**Property added in FileIO version 1.0*/
	private static ArrayList<Event> eventList; 
	//Setting: amount of time user should press down on an event to mark it as done; Property added in FileIO version 1.1*/
	private static int longPressMilliseconds; 
	/**Setting: the color of the event when it is marked as done in LIGHT MODE; Property added in FileIO version 1.1*/
	private static int[] lightFinishedEventColorRGB; 
	/**Setting: the color of the event when it is marked as done in DARK MODE; Property added in FileIO version 1.1*/
	private static int[] darkFinishedEventColorRGB;
	/**Setting: the color of the event when it is not marked as done and its end date has been reached in LIGHT MODE; Property added in FileIO version 1.2*/
	private static int[] lightOverdueEventColorRGB;
	/**Setting: the color of the event when it is not marked as done and its end date has been reached in DARK MODE; Property added in FileIO version 1.2*/
	private static int[] darkOverdueEventColorRGB;
	/**Setting: the date format; Property added in FileIO version 1.1*/
	private static DateFormat dateFormat;
	
	/**
	 * Read the contents of the file, and put them into private static fields for access through getter and setter methods.
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void readFromFile() {
		
		try {

			ObjectInputStream input = new ObjectInputStream(new FileInputStream(FILE_DIR));
			
			originalFileVersion = input.readUTF();
						
			if(originalFileVersion.equals(IO_VERSION)) {
				
				//The FileIO that last edited this file is up to date, so it can be read normally
				
				usesDarkMode = input.readBoolean();
				eventList = (ArrayList<Event>) input.readObject();
				longPressMilliseconds = input.readInt();
				lightFinishedEventColorRGB = (int[]) input.readObject();
				darkFinishedEventColorRGB = (int[]) input.readObject();
				lightOverdueEventColorRGB = (int[]) input.readObject();
				darkOverdueEventColorRGB = (int[]) input.readObject();
				dateFormat = (DateFormat) input.readObject();
				
			} else {
				
				//The FileIO that last edited this file is old
				readOldFileVersion(originalFileVersion, input);
				
			}
			
			input.close();
			
		} catch(EOFException e) {
			
			if(originalFileVersion == null) {
				
				//Since the file version should always be at the beginning of the file, if originalFileVersion is null, then the file is empty
				setDefaultValues();
				
			} else {
				
				//The file ended abruptly for whatever reason, meaning that the file is corrupted or isn't in the right format
				e.printStackTrace();
				
			}
			
		} catch(FileNotFoundException e) {
			
			//If file wasn't found, just set the default values. A new file will be created when saveToFile() is called
			originalFileVersion = IO_VERSION;
			setDefaultValues();
			
		} catch(IOException e) {

			e.printStackTrace();

		} catch (ClassNotFoundException e) {

			e.printStackTrace();

		}
		
	}
	
	/**
	 * Clear the file's contents and output the private static fields to the file<br>
	 * If there is no file, a new file with directory at <code>FILE_DIR</code> will be created<br>
	 * Similar to the <code>flush()</code> method of many output streams
	 */
	public synchronized static void saveToFile() {
		
		try {

			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(FILE_DIR));
			
			output.writeUTF(IO_VERSION);
			output.writeBoolean(usesDarkMode);
			output.writeObject(eventList);
			output.writeInt(longPressMilliseconds);
			output.writeObject(lightFinishedEventColorRGB);
			output.writeObject(darkFinishedEventColorRGB);
			output.writeObject(lightOverdueEventColorRGB);
			output.writeObject(darkOverdueEventColorRGB);
			output.writeObject(dateFormat);
			
			output.close();
			
		} catch (IOException e) {

			e.printStackTrace();

		}
		
	}
	
	/**
	 * Adds an event
	 * @param event The event to add
	 */
	public static void addEvent(Event event) {
		
		eventList.add(event);
		
	}
	
	/**
	 * Removes an event
	 * @param event The <i>exact</i> event object to remove (there is no implemented <code>equals</code> method for Event)
	 * @return True if the event object was found and removed in the internal ArrayList, false otherwise
	 */
	public static boolean removeEvent(Event event) {
		
		return eventList.remove(event);
		
	}
	
	/**
	 * Edits an event's title
	 * @param event The event to edit
	 * @param newEventTitle The new event title
	 * @return True if the event title was successfully edited
	 */
	public static boolean editEventTitle(Event event, String newEventTitle) {
		
		//Note that Event has no equals method, meaning that indexOf will look for the exact object in eventList
		//This prevents weird stuff from happening when there's two events with equal properties, and the user edits one of them
		
		int eventIndex = eventList.indexOf(event);
		
		if(eventIndex == -1) {
			
			return false;
			
		} else {
			
			eventList.get(eventIndex).setEventTitle(newEventTitle);
			return true;
			
		}
		
	}
	
	/**
	 * Get all events saved to the file
	 * @return A new <code>ArrayList</code> of <i>the <b>exact</b> Event objects</i> that are saved in the internal Event <code>ArrayList</code>
	 */
	public static ArrayList<Event> getEvents() {
		
		ArrayList<Event> eventListCopy = new ArrayList<Event>();
		for(int i = 0; i < eventList.size(); i++) {
			
			eventListCopy.add(eventList.get(i));
			
		}
		
		return eventListCopy;
		
	}
	
	/**
	 * Get the version of FileIO that last saved to this file<br>
	 * @return The FileIO version that last edited this file
	 */
	public static String getOriginalFileVersion() {
		
		return originalFileVersion;
		
	}
	
	/**
	 * Get whether or not the program is using dark mode<br>
	 * Setting added in FileIO Version 1.0
	 * @return True if program is using dark mode
	 */
	public static boolean getUseDarkMode() {
		
		return usesDarkMode;
		
	}
	
	/**
	 * Set whether or not the program should use dark mode<br>
	 * Setting added in FileIO Version 1.0
	 * @param usesDarkMode True if the program should use dark mode
	 */
	public static void setUseDarkMode(boolean usesDarkMode) {
		
		FileIO.usesDarkMode = usesDarkMode;
		
	}
	
	/**
	 * Get the amount of time that the user needs to press down on an event in order to mark it as done<br>
	 * Setting added in FileIO Version 1.1
	 * @return The amount of time user needs to press down
	 */
	public static int getLongPressMilliseconds() {

		return longPressMilliseconds;

	}

	/**
	 * Set the amount of time that the user needs to press down on an event in order to mark it as done<br>
	 * Setting added in FileIO Version 1.1
	 * @param longPressSeconds The amount of time user needs to press down
	 */
	public static void setLongPressMilliseconds(int longPressSeconds) {

		FileIO.longPressMilliseconds = longPressSeconds;

	}

	/**
	 * Get the light mode finished event color<br>
	 * Setting added in FileIO Version 1.1
	 * @return A new array of the RGB values of the color of events when they are marked as done in light mode
	 */
	public static int[] getLightFinishedEventColorRGB() {

		return new int[] {lightFinishedEventColorRGB[0], lightFinishedEventColorRGB[1], lightFinishedEventColorRGB[2]};

	}

	/**
	 * Set the light mode finished event color<br>
	 * Setting added in FileIO Version 1.1
	 * @param lightFinishedEventColorRGB The RGB values of the color of events when they are marked as done in light mode
	 */
	public static void setLightFinishedEventColorRGB(int[] lightFinishedEventColorRGB) {

		FileIO.lightFinishedEventColorRGB = new int[] {lightFinishedEventColorRGB[0], lightFinishedEventColorRGB[1], lightFinishedEventColorRGB[2]};

	}

	/**
	 * Get the dark mode finished event color<br>
	 * Setting added in FileIO Version 1.1
	 * @return A new array of the RGB values of the color of events when they are marked as done in dark mode
	 */
	public static int[] getDarkFinishedEventColorRGB() {

		return darkFinishedEventColorRGB;

	}

	/**
	 * Set the dark mode finished event color<br>
	 * Setting added in FileIO Version 1.1
	 * @param darkFinishedEventColorRGB The RGB values of the color of events when they are marked as done in dark mode
	 */
	public static void setDarkFinishedEventColorRGB(int[] darkFinishedEventColorRGB) {

		FileIO.darkFinishedEventColorRGB = darkFinishedEventColorRGB;

	}

	/**
	 * Get the light mode overdue event color<br>
	 * Setting added in FileIO Version 1.2
	 * @return The RGB values of the color of events when they are overdue in light mode
	 */
	public static int[] getLightOverdueEventColorRGB() {

		return lightOverdueEventColorRGB;

	}

	/**
	 * Set the light mode overdue event color<br>
	 * Setting added in FileIO Version 1.2
	 * @param lightUnfinishedLateEventColorRGB The RGB values of the color of events when they are overdue in light mode
	 */
	public static void setLightOverdueEventColorRGB(int[] lightUnfinishedLateEventColorRGB) {

		FileIO.lightOverdueEventColorRGB = lightUnfinishedLateEventColorRGB;

	}

	/**
	 * Get the dark mode overdue event color<br>
	 * Setting added in FileIO Version 1.2
	 * @return The RGB values of the color of events when they are overdue in dark mode
	 */
	public static int[] getDarkOverdueEventColorRGB() {

		return darkOverdueEventColorRGB;

	}

	/**
	 * Set the dark mode overdue event color<br>
	 * Setting added in FileIO Version 1.2
	 * @param darkUnfinishedLateEventColorRGB The RGB values of the color of events when they are overdue in dark mode
	 */
	public static void setDarkOverdueEventColorRGB(int[] darkUnfinishedLateEventColorRGB) {

		FileIO.darkOverdueEventColorRGB = darkUnfinishedLateEventColorRGB;

	}

	/**
	 * Get the date format that is currently being used<br>
	 * Setting added in FileIO Version 1.1
	 * @return The date format
	 */
	public static DateFormat getDateFormat() {

		return dateFormat;

	}

	/**
	 * Set the date format that will be used<br>
	 * Version 1.1
	 * @param dateFormat The date format
	 */
	public static void setDateFormat(DateFormat dateFormat) {
	
		FileIO.dateFormat = dateFormat;
	
	}

	/**
	 * Sets the default settings.
	 */
	public static void setDefaultSettings() {
		
		longPressMilliseconds = 600;
		lightFinishedEventColorRGB = new int[] {255, 196, 0};
		darkFinishedEventColorRGB = new int[] {128, 98, 0};
		lightOverdueEventColorRGB = new int[] {252, 81, 81};
		darkOverdueEventColorRGB = new int[] {126, 41, 41};
		
	}
	
	//Reads old file versions, and fills in the default for any new values
	@SuppressWarnings("unchecked")
	private static void readOldFileVersion(String version, ObjectInputStream input) throws IOException, ClassNotFoundException {
		
		switch(version) {
		
		case "1.0":
			setDefaultValues();
			usesDarkMode = input.readBoolean();
			eventList = (ArrayList<Event>) input.readObject();
			break;
		case "1.1":
			setDefaultValues();
			usesDarkMode = input.readBoolean();
			eventList = (ArrayList<Event>) input.readObject();
			longPressMilliseconds = input.readInt();
			lightFinishedEventColorRGB = (int[]) input.readObject();
			darkFinishedEventColorRGB = (int[]) input.readObject();
			dateFormat = (DateFormat) input.readObject();
			break;
		default:
			throw new UnsupportedClassVersionError();
			
		}
		
	}
	
	//Is like setDefaultSettings(), but also sets the event list
	private static void setDefaultValues() {
		
		usesDarkMode = false;
		eventList = new ArrayList<Event>();
		longPressMilliseconds = 600;
		lightFinishedEventColorRGB = new int[] {255, 196, 0};
		darkFinishedEventColorRGB = new int[] {128, 98, 0};
		lightOverdueEventColorRGB = new int[] {252, 81, 81};
		darkOverdueEventColorRGB = new int[] {126, 41, 41};
		dateFormat = DateFormat.MDY;
		
	}

}
