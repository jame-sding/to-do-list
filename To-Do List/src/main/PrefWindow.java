package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import support.Date.DateFormat;

/**
 * A pop up window that allows the user to modify certain settings of the program
 * @author James
 */
public class PrefWindow extends JFrame {
	
	private static final long serialVersionUID = 1265702759846293267L;
	
	/**The size of the gap between the actual content of this window and the window's edges*/
	private static final int GAP_FROM_WINDOW_EDGES = 15;
	/**The size of the gap between the preferences*/
	private static final int DEFAULT_COMPONENT_SEPARATION = 5;
	/**The size of the gap between the bottom buttons*/
	private static final int BOTTOM_BUTTONS_SEPARATION = 3;
	
	/**The window icon*/
	private final ImageIcon prefWindowIcon = new ImageIcon(getClass().getResource("/todopreferencesicon.png"));
	/**Title font*/
	private final Font titleFont = new Font("Segoe UI Semibold", Font.PLAIN, 24);
	/**The icon for the small color picker button in the preferences dialog window*/
	private final ImageIcon colorPickerIcon = new ImageIcon(getClass().getResource("/colorpicker.png"));
	
	/**A reference to the main window's pane*/
	private MainPane mainPane;
	
	/**The combo box for the date format*/
	private JComboBox<DateFormat> dateFormatComboBox;
	/**The text field with the amount of time the user needs to hold down the mouse to mark an event as done*/
	private JTextField longPressField;
	private JTextField lightFinishedColorField;
	private JTextField darkFinishedColorField;
	private JTextField lightOverdueColorField;
	private JTextField darkOverdueColorField;
	private JButton resetButton;
	private JButton appearanceModeButton;
	private JButton okayButton;
	
	private ColorPickerWindow colorPickerWindow = new ColorPickerWindow(ColorSetting.FINISHED_L);
	
	public enum ColorSetting{FINISHED_L, OVERDUE_L, FINISHED_D, OVERDUE_D}
	
	/**
	 * Creates a pop-up preference window
	 * @param mainPane The main pane that this preference window will be attached to
	 */
	public PrefWindow(MainPane mainPane) {
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Preferences");
		setSize(325, 325);
		setIconImage(prefWindowIcon.getImage());
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		
		this.mainPane = mainPane;
		
		//Contains nearly all of the components in this window
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		//Contains the title of this window
		JLabel titleLabel = new JLabel("Preferences");
		titleLabel.setFont(titleFont);
		mainPanel.add(Box.createRigidArea(new Dimension(0, GAP_FROM_WINDOW_EDGES)));
		mainPanel.add(titleLabel);
		
		
		//For the dateFormat setting in FileIO
		JPanel dateSettingPanel = new JPanel();
		dateSettingPanel.setLayout(new BoxLayout(dateSettingPanel, BoxLayout.X_AXIS));
		dateSettingPanel.setAlignmentX(LEFT_ALIGNMENT);
		dateSettingPanel.add(new JLabel("<html>Date Format: </html>"));
		dateFormatComboBox = new JComboBox<DateFormat>(new DateFormat[] {DateFormat.DMY, DateFormat.MDY, DateFormat.YMD});
		dateFormatComboBox.setSelectedItem(FileIO.getDateFormat());
		dateFormatComboBox.setAlignmentX(LEFT_ALIGNMENT);
		dateFormatComboBox.setMaximumSize(new Dimension(10000, 22));
		dateSettingPanel.add(dateFormatComboBox);
		mainPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		mainPanel.add(dateSettingPanel);
		
		//For the longPressMilliseconds setting in FileIO
		JPanel markEventAsDoneLongPressPanel = new JPanel();
		markEventAsDoneLongPressPanel.setLayout(new BoxLayout(markEventAsDoneLongPressPanel, BoxLayout.X_AXIS));
		markEventAsDoneLongPressPanel.setAlignmentX(LEFT_ALIGNMENT);
		markEventAsDoneLongPressPanel.add(new JLabel("<html>Time Required to Hold an Event <br></br>to Mark It As Done (in milliseconds)</html>"));
		longPressField = new JTextField("" + FileIO.getLongPressMilliseconds());
		longPressField.setAlignmentX(LEFT_ALIGNMENT);
		longPressField.setMaximumSize(new Dimension(10000, 22));
		markEventAsDoneLongPressPanel.add(longPressField);
		mainPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		mainPanel.add(markEventAsDoneLongPressPanel);
		
		//For the lightFinishedEventColorRGB setting in FileIO
		lightFinishedColorField = new JTextField();
		JPanel lightFinishedColorPanel = createColorFieldJPanel("<html>Light Mode Finished Event Color</html>", lightFinishedColorField, FileIO.getLightFinishedEventColorRGB());
		mainPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		mainPanel.add(lightFinishedColorPanel);
		
		//For the darkFinishedEventColorRGB setting in FileIO
		darkFinishedColorField = new JTextField();
		JPanel darkFinishedColorPanel = createColorFieldJPanel("<html>Dark Mode Unfinished Event Color</html>", darkFinishedColorField, FileIO.getDarkFinishedEventColorRGB());
		mainPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		mainPanel.add(darkFinishedColorPanel);
		
		//For the lightOverdueEventColorRGB setting in FileIO
		lightOverdueColorField = new JTextField();
		JPanel lightOverdueColorPanel = createColorFieldJPanel("<html>Light Mode Overdue Event Color</html>", lightOverdueColorField, FileIO.getLightOverdueEventColorRGB());
		mainPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		mainPanel.add(lightOverdueColorPanel);
		
		//For the darkOverdueEventColorRGB setting in FileIO
		darkOverdueColorField = new JTextField();
		JPanel darkOverdueColorPanel = createColorFieldJPanel("<html>Dark Mode Overdue Event Color</html>", darkOverdueColorField, FileIO.getDarkOverdueEventColorRGB());
		mainPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		mainPanel.add(darkOverdueColorPanel);
		
		
		//Contains the reset, light/dark mode button, and OK button.
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
		bottomButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		resetButton = new JButton("Reset");
		
		appearanceModeButton = new JButton();
		appearanceModeButton.setMinimumSize(new Dimension(135, 22));
		if(UIManager.getLookAndFeel().getClass().equals(FlatLightLaf.class)) {
			
			appearanceModeButton.setText("Turn on Dark Mode");
			
		} else {
			
			appearanceModeButton.setText("Turn on Light Mode");
			
		}
		
		okayButton = new JButton("OK");
		
		bottomButtonPanel.add(Box.createHorizontalGlue());
		bottomButtonPanel.add(resetButton);
		bottomButtonPanel.add(Box.createRigidArea(new Dimension(BOTTOM_BUTTONS_SEPARATION, 0)));
		bottomButtonPanel.add(appearanceModeButton);
		bottomButtonPanel.add(Box.createRigidArea(new Dimension(BOTTOM_BUTTONS_SEPARATION, 0)));
		bottomButtonPanel.add(okayButton);
		
		
		mainPanel.add(Box.createVerticalGlue());
		mainPanel.add(bottomButtonPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, GAP_FROM_WINDOW_EDGES)));
		
		
		add(Box.createRigidArea(new Dimension(GAP_FROM_WINDOW_EDGES, 0)));
		add(mainPanel);
		add(Box.createRigidArea(new Dimension(GAP_FROM_WINDOW_EDGES, 0)));
		
		//Add listeners to the components
		addListeners();
					
	}
	
	private void addListeners() {
		
		//Repaint this window when it is activated
		this.addWindowListener(new WindowAdapter() {
		
			@Override
			public void windowActivated(WindowEvent e) {
				
				e.getComponent().repaint();
				e.getComponent().revalidate();
				System.out.println("window activiated");
				
			}
		
		});
		
		dateFormatComboBox.addActionListener((e) -> {
			
			switch((DateFormat) dateFormatComboBox.getSelectedItem()) {
			
			case DMY:
				FileIO.setDateFormat(DateFormat.DMY);
				break;
			case MDY:
				FileIO.setDateFormat(DateFormat.MDY);
				break;
			case YMD:
				FileIO.setDateFormat(DateFormat.YMD);
				break;
			default:
				throw new UnsupportedOperationException();
			
			}
			
			//Reloading all of the date fields
			mainPane.updateColorsAndDateFormats();
			
		});
		
		//Switches the focus to the okay button, which triggers the focusLost method below
		longPressField.addKeyListener(new KeyAdapter() {
		
			@Override
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					okayButton.requestFocus();
					
				}
				
			}
		
		});
		
		//Sets the longPressMilliseconds setting in FileIO
		longPressField.addFocusListener(new FocusAdapter() {
		
			@Override
			public void focusGained(FocusEvent e) {
				
				//Reset the outline
				longPressField.putClientProperty("JComponent.outline", null);
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				try {
					
					FileIO.setLongPressMilliseconds(Integer.parseInt(longPressField.getText()));
					
				} catch(NumberFormatException ex) {
					
					//If the user input was unparsed, then reset the input and create an error outline
					longPressField.setText("" + FileIO.getLongPressMilliseconds());
					longPressField.putClientProperty("JComponent.outline", "error");
					
				}
				
			}
		
		});
		
		/**
		 * Key adapter for text boxes dealing with RGB
		 */
		KeyAdapter colorFieldKeyAdapter = new KeyAdapter() {
		
			@Override
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					okayButton.requestFocus();
					
				}
				
			}
		
		};
		
		/**
		 * Focus adapter for text boxes dealing with RGB
		 */
		FocusAdapter colorFieldFocusAdapter = new FocusAdapter() {
		
			@Override
			public void focusGained(FocusEvent e) {
				
				//Reset the outline of the text field
				((JTextField) e.getSource()).putClientProperty("JComponent.outline", null);
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				JTextField source = (JTextField) e.getSource();
				
				if(source == lightFinishedColorField) {
					
					setColorField(ColorSetting.FINISHED_L);
					
				} else if(source == darkFinishedColorField) {
					
					setColorField(ColorSetting.FINISHED_D);
					
				} else if(source == lightOverdueColorField) {
					
					setColorField(ColorSetting.OVERDUE_L);
					
				} else if(source == darkOverdueColorField) {
					
					setColorField(ColorSetting.OVERDUE_D);
					
				}
				
			}
		
		};
		
		lightFinishedColorField.addKeyListener(colorFieldKeyAdapter);
		lightFinishedColorField.addFocusListener(colorFieldFocusAdapter);
		darkFinishedColorField.addKeyListener(colorFieldKeyAdapter);
		darkFinishedColorField.addFocusListener(colorFieldFocusAdapter);
		lightOverdueColorField.addKeyListener(colorFieldKeyAdapter);
		lightOverdueColorField.addFocusListener(colorFieldFocusAdapter);
		darkOverdueColorField.addKeyListener(colorFieldKeyAdapter);
		darkOverdueColorField.addFocusListener(colorFieldFocusAdapter);
		
		//Resets to the default settings
		resetButton.addActionListener((e) -> {
			
			FileIO.setDefaultSettings();
			lightFinishedColorField.setText(FileIO.getLightFinishedEventColorRGB()[0] + " " + FileIO.getLightFinishedEventColorRGB()[1] + " " + FileIO.getLightFinishedEventColorRGB()[2]);
			darkFinishedColorField.setText(FileIO.getDarkFinishedEventColorRGB()[0] + " " + FileIO.getDarkFinishedEventColorRGB()[1] + " " + FileIO.getDarkFinishedEventColorRGB()[2]);
			lightOverdueColorField.setText(FileIO.getLightOverdueEventColorRGB()[0] + " " + FileIO.getLightOverdueEventColorRGB()[1] + " " + FileIO.getLightOverdueEventColorRGB()[2]);
			darkOverdueColorField.setText(FileIO.getDarkOverdueEventColorRGB()[0] + " " + FileIO.getDarkOverdueEventColorRGB()[1] + " " + FileIO.getDarkOverdueEventColorRGB()[2]);
			
			//Updates the table colors
			mainPane.updateColorsAndDateFormats();
			
		});
		
		//Switches the light/dark mode
		appearanceModeButton.addActionListener((e) -> {
			
			//Find out what the current look and feel is
			boolean isSwitchingToDarkMode = false;
			if(UIManager.getLookAndFeel().getClass().equals(FlatLightLaf.class)) {
				
				//Currently, it is light mode, so we're going to switch to dark mode
				isSwitchingToDarkMode = true;
				
			}
			
			//Switch the appearance button icon, and get the new look and feel
			LookAndFeel lookAndFeel = switchAppearanceButtonIcon(isSwitchingToDarkMode);
			mainPane.switchAppearanceButtonIcon(isSwitchingToDarkMode);
			
			//Actually apply the new look and feel to the program
			try {
				
				//Change this setting in FileIO
				FileIO.setUseDarkMode(isSwitchingToDarkMode);
				UIManager.setLookAndFeel(lookAndFeel);
				//Reload the look and feels of the windows
				mainPane.updateLookAndFeel();
				
			} catch (UnsupportedLookAndFeelException ex) {

				ex.printStackTrace();

			}
						
		});
		
		//Disposes the preference window
		okayButton.addActionListener((e) -> {
			
			dispose();
			
		});
		
	}
	
	/**
	 * Set the color for a particular setting to FileIO, according to what is written in the text field
	 * @param colorSetting The color setting (ex.: dark mode finished, light mode overdue)
	 */
	private void setColorField(ColorSetting colorSetting) {
		
		JTextField colorField;
		int[] originalRGB;
		//Obtain the text box that correlates to colorSetting as well as the original RGB
		switch(colorSetting) {
		
		case FINISHED_D:
			colorField = darkFinishedColorField;
			originalRGB = FileIO.getDarkFinishedEventColorRGB();
			break;
		case FINISHED_L:
			colorField = lightFinishedColorField;
			originalRGB = FileIO.getLightFinishedEventColorRGB();
			break;
		case OVERDUE_D:
			colorField = darkOverdueColorField;
			originalRGB = FileIO.getDarkOverdueEventColorRGB();
			break;
		case OVERDUE_L:
			colorField = lightOverdueColorField;
			originalRGB = FileIO.getLightOverdueEventColorRGB();
			break;
		default:
			throw new UnsupportedOperationException();
		
		}
		
		//The array that will store the RGB values inputed by the user
		int[] inputedRGB = new int[3];
		
		//Scanner for the user inputed text
		Scanner scanner = new Scanner(colorField.getText());
		//Try to parse RGB values out of the inputed text.
		try {
			
			//If there is leading or trailing whitespace
			if(!colorField.getText().trim().equals(colorField.getText())) {
				
				throw new Exception();
				
			}
			
			inputedRGB[0] = scanner.nextInt();
			inputedRGB[1] = scanner.nextInt();
			inputedRGB[2] = scanner.nextInt();
			
			//If there is extra input, throw an exception because then the input is invalid.
			if(scanner.hasNext()) {
				
				throw new Exception();
				
			}
			
			//If the code reaches here, then RGB values were successfully parsed and can be applied
			switch(colorSetting) {
			
			case FINISHED_D:
				FileIO.setDarkFinishedEventColorRGB(inputedRGB);
				break;
			case FINISHED_L:
				FileIO.setLightFinishedEventColorRGB(inputedRGB);
				break;
			case OVERDUE_D:
				FileIO.setDarkOverdueEventColorRGB(inputedRGB);
				break;
			case OVERDUE_L:
				FileIO.setLightOverdueEventColorRGB(inputedRGB);
				break;
			default:
				throw new UnsupportedOperationException();
			
			}
			
			//Reload the table
			mainPane.updateColorsAndDateFormats();
			
		} catch(Exception ex) {
			
			//When the user input is invalid, reset the text field and give an error outline
			colorField.setText(originalRGB[0] + " " + originalRGB[1] + " " + originalRGB[2]);
			colorField.putClientProperty("JComponent.outline", "error");
			
		} finally {
			
			scanner.close();
			
		}
		
	}
	
	/**
	 * Creates a JPanel for a color setting
	 * @param colorFieldName The text for the JLabel
	 * @param textFieldReference The JTextField that will be used
	 * @param rgb The RGB values for the color setting
	 * @return A JPanel with a JLabel and <code>textFieldReference</code> that contains the RGB values in text
	 */
	private JPanel createColorFieldJPanel(String colorFieldName, JTextField textFieldReference, int[] rgb) {
		
		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
		temp.setAlignmentX(LEFT_ALIGNMENT);
		temp.add(new JLabel(colorFieldName));
		textFieldReference.setText(rgb[0] + " " + rgb[1] + " " + rgb[2]);
		textFieldReference.setAlignmentX(LEFT_ALIGNMENT);
		textFieldReference.setMaximumSize(new Dimension(64, 22));
		temp.add(textFieldReference);
		JButton colorPickerButton = new JButton(colorPickerIcon);
		colorPickerButton.addActionListener((e) -> {
						
			ColorSetting colorSetting = null;
			if(textFieldReference == lightFinishedColorField) {
				
				colorSetting = ColorSetting.FINISHED_L;
				
			} else if(textFieldReference == darkFinishedColorField) {
				
				colorSetting = ColorSetting.FINISHED_D;
				
			} else if(textFieldReference == lightOverdueColorField) {
				
				colorSetting = ColorSetting.OVERDUE_L;
				
			} else if(textFieldReference == darkOverdueColorField) {
				
				colorSetting = ColorSetting.OVERDUE_D;
				
			} else {
				
				throw new UnsupportedOperationException();
				
			}
			
			if(colorSetting != null) {
				
				if(colorPickerWindow != null) {
					
					colorPickerWindow.dispose();
					
				}
				
				colorPickerWindow = new ColorPickerWindow(colorSetting);
				colorPickerWindow.setVisible(true);
				colorPickerWindow.requestOKButtonFocus();
				
			}
			
		});
		temp.add(Box.createRigidArea(new Dimension(DEFAULT_COMPONENT_SEPARATION, 0)));
		temp.add(colorPickerButton);
		return temp;
		
	}
	
	/**
	 * Requests focus for the OK button
	 */
	public void requestOKButtonFocus() {
		
		okayButton.requestFocusInWindow();
		
	}
	
	/**
	 * Set the appearance button icon to the current 
	 * @param button The button
	 * @param useIcon Set true if the button should use the icon, false if the button should use text
	 */
	public LookAndFeel switchAppearanceButtonIcon(boolean isSwitchingToDarkMode) {
		
		//The look and feel that the program will be set to at the end of this method
		LookAndFeel lookAndFeel;
		
		if(isSwitchingToDarkMode) {
			
			//Switching to dark mode
			
			//Set the new look and feel to FlatDarkLaf (dark mode)
			lookAndFeel = new FlatDarkLaf();
			//Update the icon so that it looks like it allows the user to switch back to light mode
			appearanceModeButton.setText("Turn on Light Mode");
			
		} else {
			
			//Switching to light mode
			
			//Set the new look and feel to FlatLightLaf (light mode)
			lookAndFeel = new FlatLightLaf();
			//Update the icon so that it looks like it allows the user to switch back to dark mode
			appearanceModeButton.setText("Turn on Dark Mode");
			
		}
		
		return lookAndFeel;
		
	}
	
	/**
	 * Sets the text in a color text field, and applies the new color to the program
	 * @param color The color to set the text to
	 * @param colorSetting The color setting of the text field
	 */
	public void setTextFieldColor(Color color, ColorSetting colorSetting) {
		
		JTextField textField = null;
		
		switch(colorSetting) {
		
		case FINISHED_L:
			textField = lightFinishedColorField;
			break;
		case FINISHED_D:
			textField = darkFinishedColorField;
			break;
		case OVERDUE_L:
			textField = lightOverdueColorField;
			break;
		case OVERDUE_D:
			textField = darkOverdueColorField;
			break;
		
		}
		
		textField.setText(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
		
		setColorField(colorSetting);
		
	}
	
	public ColorPickerWindow getColorPickerWindow() {
		
		return colorPickerWindow;
		
	}
		
	public class ColorPickerWindow extends JFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 614894598885931815L;

		/**The window icon*/
		private final ImageIcon colorChooserWindowIcon = new ImageIcon(getClass().getResource("/todocolorchoosericon.png"));
		
		private ColorSetting colorSetting;
		
		private JColorChooser colorChooser;
		private JButton okayButton;
		private JButton cancelButton;
		
		public ColorPickerWindow(ColorSetting colorSetting) {
			
			String windowTitle;
			Color initialColor;
			switch(colorSetting) {
			
			case FINISHED_D:
				windowTitle = "Color Picker (Dark Mode Finished)";
				initialColor = new Color(FileIO.getDarkFinishedEventColorRGB()[0], FileIO.getDarkFinishedEventColorRGB()[1], FileIO.getDarkFinishedEventColorRGB()[2]);
				break;
			case FINISHED_L:
				windowTitle = "Color Picker (Light Mode Finished)";
				initialColor = new Color(FileIO.getLightFinishedEventColorRGB()[0], FileIO.getLightFinishedEventColorRGB()[1], FileIO.getLightFinishedEventColorRGB()[2]);
				break;
			case OVERDUE_D:
				windowTitle = "Color Picker (Dark Mode Overdue)";
				initialColor = new Color(FileIO.getDarkOverdueEventColorRGB()[0], FileIO.getDarkOverdueEventColorRGB()[1], FileIO.getDarkOverdueEventColorRGB()[2]);
				break;
			case OVERDUE_L:
				windowTitle = "Color Picker (Light Mode Overdue)";
				initialColor = new Color(FileIO.getLightOverdueEventColorRGB()[0], FileIO.getLightOverdueEventColorRGB()[1], FileIO.getLightOverdueEventColorRGB()[2]);
				break;
			default:
				windowTitle = "Color Picker";
				initialColor = Color.WHITE;
				break;
			
			}
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setTitle(windowTitle);
			setSize(750, 306);
			setIconImage(colorChooserWindowIcon.getImage());
			setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
			
			this.colorSetting = colorSetting;
			
			JPanel colorChooserPanel = new JPanel();
			colorChooser = new JColorChooser(initialColor);
			colorChooser.setChooserPanels(new AbstractColorChooserPanel[] {colorChooser.getChooserPanels()[1]});
			colorChooser.setPreviewPanel(new JPanel());
			colorChooser.setAlignmentX(CENTER_ALIGNMENT);
			colorChooserPanel.setAlignmentX(CENTER_ALIGNMENT);
			colorChooserPanel.add(colorChooser);
			
			JPanel bottomButtonPanel = new JPanel();
			bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
			okayButton = new JButton("OK");
			okayButton.setAlignmentX(CENTER_ALIGNMENT);
			cancelButton = new JButton("Cancel");
			cancelButton.setAlignmentX(CENTER_ALIGNMENT);
			bottomButtonPanel.add(okayButton);
			bottomButtonPanel.add(Box.createRigidArea(new Dimension(BOTTOM_BUTTONS_SEPARATION, 0)));
			bottomButtonPanel.add(cancelButton);
			
			
			add(Box.createRigidArea(new Dimension(0, GAP_FROM_WINDOW_EDGES)));
			add(colorChooserPanel);
			add(bottomButtonPanel);
			add(Box.createRigidArea(new Dimension(0, GAP_FROM_WINDOW_EDGES)));
			
			addListeners();
			
		}
		
		private void addListeners() {
			
			okayButton.addActionListener((e) -> {
				
				Color pickedColor = colorChooser.getColor();
				setTextFieldColor(pickedColor, colorSetting);
				
				System.out.println(lightFinishedColorField.getSize());
				dispose();
				
			});
			
			cancelButton.addActionListener((e) -> {
				
				dispose();
				
			});
			
		}
		
		public void forceOneChooserPanel(int chooserPanelIndex) {
			
			colorChooser.setChooserPanels(new AbstractColorChooserPanel[] {colorChooser.getChooserPanels()[chooserPanelIndex]});
			
		}
		
		/**
		 * Requests focus for the OK button
		 */
		public void requestOKButtonFocus() {
			
			okayButton.requestFocusInWindow();
			
		}
		
	}
	
}
