package main;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * The main window of the program
 * @author James
 * @version 1.4.1
 */
public class ToDoWindow extends JFrame {

	/**
	 * The version of the GUI
	 */
	public static final String UI_VERSION = "1.4.1";
	
	private static final long serialVersionUID = 313600793175012718L;

	//The main pane
	private MainPane mainPane;
	
	//The menu items in the window menu
	private JMenuItem helpMenuItem;
	private JMenuItem aboutMenuItem;
	private JMenuItem prefMenuItem;
		
	/**The window icon*/
	private final ImageIcon windowIcon = new ImageIcon(getClass().getResource("/todoicon.png"));
	/**The center of the screen*/
	private final Point centerPoint;
	
	/**
	 * Creates the window
	 */
	public ToDoWindow() {
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 600);
		setTitle("To-Do List");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setIconImage(windowIcon.getImage());
		//Put the window in the center of the screen
		Point nonAdjustedCenterPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		centerPoint = new Point(nonAdjustedCenterPoint.x - getWidth()/2, nonAdjustedCenterPoint.y - getHeight()/2);
		setLocation(centerPoint);
		
		mainPane = new MainPane();
		add(Box.createRigidArea(new Dimension(15, 0)));
		add(mainPane);
		add(Box.createRigidArea(new Dimension(15, 0)));
		
		//The menu bar at the top of the window
		JMenuBar menuBar = new JMenuBar();
		
		//The help menu tab
		JMenu helpMenu = new JMenu("Help");
		helpMenuItem = new JMenuItem("Help");
		helpMenu.add(helpMenuItem);
		aboutMenuItem = new JMenuItem("About");
		helpMenu.add(aboutMenuItem);
		
		//The settings menu tab
		JMenu settingsMenu = new JMenu("Settings");
		prefMenuItem = new JMenuItem("Preferences");
		settingsMenu.add(prefMenuItem);
		
		menuBar.add(helpMenu);
		menuBar.add(settingsMenu);
		
		setJMenuBar(menuBar);
				
		addListeners();
		
	}

	/**
	 * Adds listeners to some components, which provides functionality to them
	 */
	private void addListeners() {
		
		//When the program is about to exit, save FileIO's data to the file
		//Or repaint the main window when it is activated
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				FileIO.saveToFile();
				System.out.println("saved to file");
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
				reloadWindows();
				System.out.println("window activiated");
				
			}
			
			public void windowOpened(WindowEvent e) {
				
				reloadWindows();
				System.out.println("window opened");
				
			}
			
		});
		
		//Help window pop up
		helpMenuItem.addActionListener((e) -> {
			
			mainPane.getHelpWindow().setVisible(true);
			mainPane.getHelpWindow().setLocation(centerPoint);
			
		});
		
		//About window pop up
		aboutMenuItem.addActionListener((e) -> {
			
			mainPane.getAboutWindow().setVisible(true);
			mainPane.getAboutWindow().setLocation(centerPoint);
			
		});
		
		//Preferences window pop up
		prefMenuItem.addActionListener((e) -> {
			
			mainPane.getPrefWindow().setVisible(true);
			//The OK button should be focused when this window is popped up
			mainPane.getPrefWindow().requestOKButtonFocus();
			mainPane.getPrefWindow().setLocation(centerPoint);
			
		});
		
	}
	
	/**
	 * Reload the look and feel of the pop up windows
	 */
	public void reloadWindows() {
		
		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(mainPane.getHelpWindow());
		SwingUtilities.updateComponentTreeUI(mainPane.getAboutWindow());
		SwingUtilities.updateComponentTreeUI(mainPane.getPrefWindow());
		SwingUtilities.updateComponentTreeUI(mainPane.getPrefWindow().getColorPickerWindow());
		mainPane.getPrefWindow().getColorPickerWindow().forceOneChooserPanel(1);
		
	}
	
	//Start the program
	public static void main(String[] args) {

		//Set the custom theme
		FlatLaf.registerCustomDefaultsSource("support");
		FlatDarkLaf.setup();
		
		//Load the program frame
		SwingUtilities.invokeLater(() -> {
			
			//Read the file
			FileIO.readFromFile();
			
			try {
				
				//Set the light or dark mode
				if(FileIO.getUseDarkMode()) {
					
					UIManager.setLookAndFeel(new FlatDarkLaf());
					
				} else {
					
		            UIManager.setLookAndFeel(new FlatLightLaf());
					
				}
				
				//Putting the menu bar below the window header
				UIManager.put("TitlePane.menuBarEmbedded", false);
	            
			} catch (Exception e) {
				
	            e.printStackTrace();
	        
			} 
			
    		ToDoWindow gui = new ToDoWindow();
    		gui.setVisible(true);
    		//The input event text box should be focused when the program starts
    		gui.mainPane.requestEventFieldFocus();
    		
		});
		
		//Make the program save to file when the program is closed unexpectedly
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			
			FileIO.saveToFile();
			
		}));
		
	}

}
