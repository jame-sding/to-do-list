package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventObject;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import support.Date;
import support.Event;

/**
 * The main panel that contains most of the GUI. Also contains inner classes that are pop up windows
 * @author James
 */
public class MainPane extends JPanel {
	
	private static final long serialVersionUID = -6774360267421874482L;

	/**The size of the gap between the title ("To-Do") and the menu bar*/
	private static final int WINDOW_TOP_TITLE_GAP = 15;
	/**The size of the gap between the list of events and the right side of the window*/
	private static final int CENTER_PANEL_RIGHT_INDENT = 115;
	/**The separation between most window components*/
	private static final int DEFAULT_COMPONENT_SEPARATION = 2;
	
	/**The title label (should say "To-Do for [IRL today's date]")*/
	private JLabel titleLabel; 
	/**Rolls back the date that the list is showing events for*/
	private JButton backwardsButton; 
	/**Rolls forwards the date that the list is showing events for*/
	private JButton forwardsButton; 
	/**A text field (a.k.a. text box) that displays the date that the list is showing events for. <br>
	 * The user can also directly type a date in this text box to see events for that date.*/
	private JTextField dateField; 
	/**The table model for the event list*/
	private DefaultTableModel eventsTableModel;
	/**The event list is displayed through this table.*/
	private JTable eventsTable;
	/**The scroll pane that <code>eventsTable</code> is put in*/
	private JScrollPane eventsScrollPane;
	/**The pop up menu that displays when the user right clicks on an event in the list. Allows user to delete the event or mark/unmark it as done*/
	private JPopupMenu eventsPopupMenu;
	/**The pop up menu item in <code>eventsPopupMenu</code> that allows the user to delete the selected event*/
	private JMenuItem deletePopupMenuItem;
	/**The pop up menu item in <code>eventsPopupMenu</code> that allows the user to mark the selected event as done*/
	private JMenuItem finishPopupMenuItem;
	/**The pop up menu item in <code>eventsPopupMenu</code> that allows the user to unmark the selected event as done*/
	private JMenuItem unfinishPopupMenuItem;
	/**The text box below the list of events that allows the user to input new events into the list*/
	private JTextField eventField;
	/**When there is an event selected in the list, this text box shows the event's begin date and cannot be edited. 
	 * Otherwise, it is editable and is for the user to customize an event's begin date upon creation*/
	private JTextField beginDateField;
	/**When there is an event selected in the list, this text box shows the event's end date and cannot be edited. 
	 * Otherwise, it is editable and is for the user to customize an event's end date upon creation*/
	private JTextField endDateField;
	/**Switches between light and dark mode*/
	private JButton appearanceButton;
	
	/**The calendar object that contains the currently displayed date in the event list*/
	private Calendar calendar = Calendar.getInstance();	
	/**Today's date. Since <code>getCurrentDayObject()</code> is called <i>right after</i> <code>calendar</code> was initialized, it will return today's date.*/
	private Date todayDate = getCurrentDayObject();
	
	/**The icon for the large light/dark mode button when the program is in dark mode*/
	private final ImageIcon lightModeIcon = new ImageIcon(getClass().getResource("/lightmode.png"));
	/**The icon for the large light/dark mode button when the program is in light mode*/
	private final ImageIcon darkModeIcon = new ImageIcon(getClass().getResource("/darkmode.png"));
	
	//Objects of the inner classes (pop-up window classes)
	private final InfoWindow helpWindow = new InfoWindow(HelpWindowType.HELP);
	private final InfoWindow aboutWindow = new InfoWindow(HelpWindowType.ABOUT);
	private final PrefWindow prefWindow = new PrefWindow(this);
	
	//Enums for the inner classes
	public enum HelpWindowType {HELP, ABOUT}
	
	/**
	 * Creates the main panel of the program
	 */
	public MainPane() {
		
		//BoxLayout stacks components as if they were boxes. This one stacks vertically because of the BoxLayout.Y_AXIS parameter.
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//This panel will be left aligned. Every component added to this panel needs to be left aligned in order to prevent weird alignment problems.
		setAlignmentX(LEFT_ALIGNMENT);
		
		
		
		//Create a panel that just displays the title.
		JPanel titlePanel = new JPanel();
		titlePanel.setAlignmentX(LEFT_ALIGNMENT);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		
		titleLabel = new JLabel("<html><b>To-Do</b><font size=\"-1\">&nbsp for " + getCurrentDayLong() + "</font></html>");
		titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
		titleLabel.setAlignmentY(CENTER_ALIGNMENT);
		titlePanel.add(titleLabel);
		
		
		//The center panel is kind of like a wrapper panel for the following listPanel, and it has the indent specified by CENTER_PANEL_RIGHT_INDENT.
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS)); //This panel will stack horizontally (for the indent).
		centerPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		//The list panel contains the meat of the GUI (the date field, date button, event list, and new event text box).
		//It, however, DOESN'T contain the indent specified by CENTER_PANEL_RIGHT_INDENT.
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); //This panel will stack vertically.
		
		//This panel is the event list header, which contains dateField as well as the date forwards/backwards buttons.
		JPanel eventListHeaderPanel = new JPanel();
		eventListHeaderPanel.setLayout(new BoxLayout(eventListHeaderPanel, BoxLayout.X_AXIS)); //This panel will stack horizontally.
		eventListHeaderPanel.setAlignmentX(LEFT_ALIGNMENT);
		eventListHeaderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22)); //Limit the vertical size of this panel so that it doesn't expand vertically.
		backwardsButton = new JButton("<");
		eventListHeaderPanel.add(backwardsButton);
		eventListHeaderPanel.add(Box.createRigidArea(new Dimension(DEFAULT_COMPONENT_SEPARATION, 0)));
		dateField = new JTextField(getCurrentDay());
		dateField.setHorizontalAlignment(JTextField.CENTER); //Sets the horizontal alignment of text field's TEXT, not the text field itself.
		eventListHeaderPanel.add(dateField);
		eventListHeaderPanel.add(Box.createRigidArea(new Dimension(DEFAULT_COMPONENT_SEPARATION, 0)));
		forwardsButton = new JButton(">");
		eventListHeaderPanel.add(forwardsButton);
		//Add this to the list panel
		listPanel.add(eventListHeaderPanel);
		listPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		
		//The event list will now be created. The event "list" is actually in the form of a table because tables are better than lists.
		//Tables need table models to control what they're displaying. A custom table model will be used because the default one is incompetent.
		//Tables also need a cell editor to control how their cells are edited. A custom cell editor will be used because ^.
		//Tables also need a cell renderer to control how their cells are displayed. Custom one will be used ^.
		
		//Customized table model that forces the user to select the row in order to edit it AND manages the changing of event titles
		eventsTableModel = new DefaultTableModel(0, 1) {

			private static final long serialVersionUID = 4086311500581840542L;
			
			//Forces the user to select the row to edit
			@Override
			public boolean isCellEditable(int row, int column) {
				
				//If the cell's row is the selected row, it can be edited. Otherwise, it can't be edited.
				if(row == eventsTable.getSelectedRow()) {
					
					return true;
					
				} else {
					
					return false;
					
				}
				
			}
			
			//Manages the internal changing of event titles
			@Override
			public void setValueAt(Object value, int row, int column) {
				
				//Check that this column is for Event objects.
				if(getColumnClass(column).equals(Event.class)) {
					
					//This column is for event objects, so we can get the Event object that is being displayed at this row and column.
					//Since the Event objects are shallow copied from FileIO to the table model, we can directly modify the Event object.
					Event currentEvent = (Event) getValueAt(row, column);
					if(value instanceof String) {
						
						//The value that we are receiving is a String.
						
						currentEvent.setEventTitle((String) value);
						
						//However, if the user edits the event title so that it is blank, we will try to remove it.
						if((value == null || ((String) value).isEmpty()) && FileIO.removeEvent(currentEvent)) {
							
							eventsTableModel.removeRow(row);
							
						}
						
					} else if(value instanceof Event) {
						
						//The value that we are receiving is an Event. This shouldn't happen (as of UI version 1.3), 
						//but we'll implement code for it anyways because it's easy.
						
						Event event = (Event) value;
						currentEvent.setBeginDate(event.getBeginDate());
						currentEvent.setEndDate(event.getEndDate());
						currentEvent.setFinishedDate(event.getFinishedDate());
						currentEvent.setEventTitle(event.getEventTitle());
						
					} else {
						
						//The value that we are receiving is not a String or an Event, which is really weird.
						System.err.println("peepee poopoo");
						
					}
					
				}
				
			}
			
			//Returns the class of the objects that a column displays. 
			//For some reason, the default implementation of it just returns Object no matter the actual class, so we need to implement it properly ourselves.
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				
				return getValueAt(0, columnIndex).getClass();
				
			}
			
		};
		//Now that we have our customized table model, we can actually create the table.
		eventsTable = new JTable(eventsTableModel);
		//Customized editor that only permits editing when the user double clicks (the default editor is different and weird)
		eventsTable.setDefaultEditor(Event.class, new DefaultCellEditor(new JTextField()) {
			
			private static final long serialVersionUID = 9201822669109923579L;

			@Override
			public boolean isCellEditable(EventObject e) {
				
				if(e instanceof MouseEvent) {
					
	                return ((MouseEvent) e).getClickCount() >= clickCountToStart;
	                
	            }
				
				return false;
				
			}
			
		});
		eventsTable.setDefaultRenderer(Event.class, new EventCellRenderer()); //Customized renderer will be used for columns of class Event
		eventsTable.setDragEnabled(true); //Allows the user to drag events to other text boxes (like the event input text box). DOESN'T ALLOW EVENT REORDERING
		eventsTable.putClientProperty("terminateEditOnFocusLost", true); //Forces the table to stop editing when it loses focus (when the user clicks off of it)
		eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eventsTable.setTableHeader(null); //Remove the table's header (we have eventListHeaderPanel outside of the table)
		
		//This scroll pane will contain the table, and it will allow for the user to scroll up and down the table.
		eventsScrollPane = new JScrollPane(eventsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		eventsScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		listPanel.add(eventsScrollPane);
		listPanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		
		//Create the events pop up menu
		eventsPopupMenu = new JPopupMenu();
		deletePopupMenuItem = new JMenuItem("Delete");
		eventsPopupMenu.add(deletePopupMenuItem);
		finishPopupMenuItem = new JMenuItem("Mark as Done");
		eventsPopupMenu.add(finishPopupMenuItem);
		unfinishPopupMenuItem = new JMenuItem("Unmark as Done");
		eventsPopupMenu.add(unfinishPopupMenuItem);
		
		//Create the event input panel, which will contain a label and the event field editable text box.
		JPanel eventInputPanel = new JPanel();
		eventInputPanel.setLayout(new BoxLayout(eventInputPanel, BoxLayout.X_AXIS));
		eventInputPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		eventField = new JTextField();
		eventField.setAlignmentX(LEFT_ALIGNMENT);
		eventField.setMaximumSize(new Dimension(10000, 22));
		eventInputPanel.add(new JLabel("New Event: "));
		eventInputPanel.add(eventField);
		
		listPanel.add(eventInputPanel);
		
		//Add the list panel to the center panel, and the indent as specified by CENTER_PANEL_RIGHT_INDENT.
		centerPanel.add(listPanel);
		centerPanel.add(Box.createRigidArea(new Dimension(CENTER_PANEL_RIGHT_INDENT, 0)));
		
		
		//The bottom panel contains the begin and end date text boxes and the light/dark mode button.
		//The begin and end date text boxes are in their own panel, and to the right of it is the light/dark mode button
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS)); //This panel will stack horizontally
		bottomPanel.setAlignmentX(LEFT_ALIGNMENT);
		bottomPanel.setAlignmentY(TOP_ALIGNMENT);
		
		//The panel that the begin and end date text boxes will be put in
		JPanel beginEndDatePanel = new JPanel();
		beginEndDatePanel.setLayout(new BoxLayout(beginEndDatePanel, BoxLayout.Y_AXIS)); //This panel will stack vertically
		
		//The panel that wraps together a label and beginDateField
		JPanel beginDatePanel = new JPanel();
		beginDatePanel.setLayout(new BoxLayout(beginDatePanel, BoxLayout.X_AXIS));
		beginDateField = new JTextField(getCurrentDay());
		beginDateField.setAlignmentX(LEFT_ALIGNMENT);
		beginDateField.setMaximumSize(new Dimension(10000, 22));
		beginDatePanel.add(new JLabel("Begin Date: "));
		beginDatePanel.add(beginDateField);
		beginDatePanel.add(Box.createHorizontalGlue());
		
		//The panel that wraps together a label and endDateField
		JPanel endDatePanel = new JPanel();
		endDatePanel.setLayout(new BoxLayout(endDatePanel, BoxLayout.X_AXIS));
		endDateField = new JTextField(getCurrentDay());
		endDateField.setAlignmentX(LEFT_ALIGNMENT);
		endDateField.setMaximumSize(new Dimension(10000, 22));
		endDatePanel.add(new JLabel("End Date: "));
		endDatePanel.add(endDateField);
		endDatePanel.add(Box.createHorizontalGlue());
		
		//Add the begin and end date panels to the larger combined panel.
		beginEndDatePanel.add(beginDatePanel);
		beginEndDatePanel.add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		beginEndDatePanel.add(endDatePanel);
		
		//Add the larger combined panel to the even larger bottom panel.
		bottomPanel.add(beginEndDatePanel);
		bottomPanel.add(Box.createHorizontalGlue());
		
		//Create the light/dark mode button.
		appearanceButton = new JButton();
		//Set the button's icon depending if the program is in light mode or dark mode.
		if(UIManager.getLookAndFeel().getClass().equals(FlatLightLaf.class)) {
			
			appearanceButton.setIcon(darkModeIcon);
			
		} else {
			
			appearanceButton.setIcon(lightModeIcon);
			
		}
		//Add the appearance button to the bottom panel.
		bottomPanel.add(appearanceButton);
		
		
		
		//Add the three major panels
		add(Box.createRigidArea(new Dimension(0, WINDOW_TOP_TITLE_GAP)));
		add(titlePanel);
		add(Box.createRigidArea(new Dimension(0, 15)));
		add(centerPanel);
		add(Box.createRigidArea(new Dimension(0, DEFAULT_COMPONENT_SEPARATION)));
		add(bottomPanel);
		add(Box.createRigidArea(new Dimension(0, 15)));
		
		//Add listeners to the components. These listeners provide functionality.
		addListeners();
		//Load events to the list
		loadEvents();
		
	}
	
	/**
	 * Adds listeners to some components, which provides functionality to them.
	 */
	private void addListeners() {
		
		//Roll back the current viewing date and then reload the events
		backwardsButton.addActionListener((e) -> {
			
			//Roll backwards the day of year. Note that this will not change the year
			calendar.roll(Calendar.DAY_OF_YEAR, false);
			
			//Change the year if the user rolled from January 1st to December 31st
			if(calendar.get(Calendar.DAY_OF_MONTH) == 31 && calendar.get(Calendar.MONTH) == 11) {
				
				calendar.roll(Calendar.YEAR, false);
				
			}
			
			String currentDayStr = getCurrentDay();
			dateField.setText(currentDayStr);
			beginDateField.setText(currentDayStr);
			endDateField.setText(currentDayStr);
			
			loadEvents();
			
		});
		
		//Roll forwards the current viewing date and then reload the events
		forwardsButton.addActionListener((e) -> {
			
			//Roll forwards the day of year. Note that this will not change the year
			calendar.roll(Calendar.DAY_OF_YEAR, true);
			
			//Change the year if the user rolled from December 31st to January 1st
			if(calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.MONTH) == 0) {
				
				calendar.roll(Calendar.YEAR, true);
				
			}
			
			String currentDayStr = getCurrentDay();
			dateField.setText(currentDayStr);
			beginDateField.setText(currentDayStr);
			endDateField.setText(currentDayStr);
			
			loadEvents();
			
		});
		
		//Upon hitting enter, cause dateField to loseFocus, activating its focus listener
		dateField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
								
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
										
					//When this code is executed, dateField loses focus, which triggers the focusLost method below
					eventField.requestFocus();
					
				}
				
			}
			
		});
		
		//Upon focus lost, set the date whose events should be displayed as the date in the dateField.
		//Upon focus gain, reset dateField's border
		dateField.addFocusListener(new FocusAdapter() {
		
			@Override
			public void focusGained(FocusEvent e) {
				
				dateField.putClientProperty("JComponent.outline", null);
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				moveToDateFieldDate();
				
			}
		
		});
		
		//Allows the user to see the begin and end dates of selected events
		eventsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			//This is called whenever the selected row of the table has changed
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int selectedRow = eventsTable.getSelectedRow();
				if(selectedRow != -1) {
					
					//The user has selected a row
					
					beginDateField.setText(((Event) eventsTableModel.getValueAt(selectedRow, 0)).getBeginDate().toString(FileIO.getDateFormat()));
					endDateField.setText(((Event) eventsTableModel.getValueAt(selectedRow, 0)).getEndDate().toString(FileIO.getDateFormat()));
					beginDateField.setEditable(false);
					endDateField.setEditable(false);
					
				} else {
					
					//The user has unselected a row
					
					beginDateField.setText(getCurrentDay());
					endDateField.setText(getCurrentDay());
					beginDateField.setEditable(true);
					endDateField.setEditable(true);
					
				}
				
			}
			
		});
		
		//Delete the selected event, or allow user to move an event up or down the list
		eventsTable.addKeyListener(new KeyAdapter() {
		
			@Override
			public void keyPressed(KeyEvent e) {
								
				int selectedRow = eventsTable.getSelectedRow();
				//If a row is selected
				if(selectedRow != -1) {
					
					if(e.getKeyCode() == KeyEvent.VK_DELETE) {
						
						//Delete selected event
						deleteSelectedEvent();
						e.consume();
						
					} else if(e.isControlDown()) {
						
						//Find the row that the user wants to move the event to
						int targetRow = selectedRow;
						if(e.getKeyCode() == KeyEvent.VK_UP) {
							
							targetRow--;
							
						} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
							
							targetRow++;
							
						} else {
							
							//If the user did not press the up or down arrow key, then the "target row" is invalid
							targetRow = -1;
							
						}
						
						//If the row that the user wants to move the event to is out of bounds, then the target row is invalid
						if(targetRow >= eventsTableModel.getRowCount() || targetRow < 0) {
							
							targetRow = -1;
							
						}
						
						if(targetRow != -1) {
							
							//The target row is valid
							swapRows(targetRow, selectedRow);
							
						}
						
					}
					
				}
				
			}
		
		});
		
		//Pop up menu allowing to delete event
		//It should be noted that the JTable doesn't extend the entirety of the JScrollPane for some reason
		//Also allows user to hold press an event to mark it as done
		MouseInputAdapter tableMouseInputAdapter = new MouseInputAdapter() {
		
			//The thread used for the hold press event functionality
			private Thread mouseHoldThread = null;
			
			//Pop up menu
			@Override
			public void mouseClicked(MouseEvent e) {
				
				int row = eventsTable.rowAtPoint(e.getPoint());
				int column = eventsTable.columnAtPoint(e.getPoint());
				//Change the selected row to where the user clicked
				eventsTable.changeSelection(row, column, false, false);
				
				if(SwingUtilities.isRightMouseButton(e)) {
					
					Object value = eventsTableModel.getValueAt(row, column);
					
					if(value.getClass().equals(Event.class)) {
						
						//Always give the user the option to delete an event
						deletePopupMenuItem.setEnabled(true);
						
						Event eventVal = (Event) value;
						
						if(eventVal.getFinishedDate() == null) {
							
							//The selected event is unfinished, so give the finish option
							finishPopupMenuItem.setVisible(true);
							unfinishPopupMenuItem.setVisible(false);
							
						} else {
							
							//The selected event is finished, so give the unfinish option
							finishPopupMenuItem.setVisible(false);
							unfinishPopupMenuItem.setVisible(true);
							
						}
						
					} else {
						
						//If the selection is not an Event
						deletePopupMenuItem.setVisible(false);
						finishPopupMenuItem.setVisible(false);
						unfinishPopupMenuItem.setVisible(false);
						
					}
					
					eventsPopupMenu.show(e.getComponent(), e.getX(), e.getY());
					
				}
				
			}
			
			//Long press event to mark as done 
			@Override
			public void mousePressed(MouseEvent e) {
				
				super.mousePressed(e);
				
				if(SwingUtilities.isLeftMouseButton(e)) {
					
					//This thread is a timer that counts the amount of time between when the user starts pressing and when the user releases/drags the mouse
					mouseHoldThread = new Thread(() -> {
						
						long startPressTime = System.currentTimeMillis();
						int longPressTimeRequirement = FileIO.getLongPressMilliseconds();
						//Infinite loop
						while(true) {
							
							//Mark the selected event as done, update the list, and exit the loop as soon as the user has held down long enough 
							if(System.currentTimeMillis() - startPressTime >= longPressTimeRequirement) {
								
								finishSelectedEvent();
								eventsTableModel.fireTableCellUpdated(eventsTable.getSelectedRow(), eventsTable.getSelectedColumn());
								break;
								
							}
							
							//Exit the loop if the user releases or drags the mouse
							if(mouseHoldThread.isInterrupted()) {
								
								break;
								
							}
							
						}
						
					});
					
					mouseHoldThread.start();
										
				}
				
			}
			
			//Long press event to mark as done
			@Override
			public void mouseReleased(MouseEvent e) {
				
				super.mouseReleased(e);
				
				if(mouseHoldThread != null) {
					
					mouseHoldThread.interrupt();
					
				}
				
			}
			
			//Long press event to mark as done
			@Override
			public void mouseDragged(MouseEvent e) {
				
				super.mouseDragged(e);
				
				if(mouseHoldThread != null) {
					
					mouseHoldThread.interrupt();
					
				}
				
			}
		
		};
		
		eventsTable.addMouseListener(tableMouseInputAdapter);
		eventsTable.addMouseMotionListener(tableMouseInputAdapter);
		
		//Allows user to de-select an event by clicking outside of a table cell (but still in JScrollPane)
		//It should be noted that the JTable doesn't extend the entirety of the JScrollPane for some reason
		eventsScrollPane.addMouseListener(new MouseAdapter() {
		
			//This is called when the user clicks on the table scroll pane but not on an event
			@Override
			public void mouseClicked(MouseEvent e) {
				
				//Clear the table selection.
				eventsTable.clearSelection();
				
				//If a cell is being edited, cancel the editing.
				if(eventsTable.getCellEditor() != null) {
					
					eventsTable.getCellEditor().stopCellEditing();
					
				}
				
				//If the user was trying to show the pop up menu for events, show a pop up menu but signify that they clicked in the wrong spot
				if(SwingUtilities.isRightMouseButton(e)) {
					
					deletePopupMenuItem.setEnabled(false);
					finishPopupMenuItem.setVisible(false);
					unfinishPopupMenuItem.setVisible(false);
					eventsPopupMenu.show(e.getComponent(), e.getX(), e.getY());
					
				}
				
			}
		
		});
		
		//If the user presses enter, add the field text as an event and then clear the field
		eventField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER && !eventField.getText().trim().isEmpty()) {
					
					addInputFieldEvent();
					beginDateField.setText(getCurrentDay());
					endDateField.setText(getCurrentDay());
					
				}
				
			}
			
		});
		
		//When the user presses enter, make the text box lose focus, activating the focus listener
		beginDateField.addKeyListener(new KeyAdapter() {
		
			@Override
			public void keyPressed(KeyEvent e) {
								
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
										
					//When this code is executed, dateField loses focus, which calls on its focusLost method below
					eventField.requestFocus();
					
				}
				
			}
		
		});
		
		//Make sure that the dates that the user inputs are valid
		beginDateField.addFocusListener(new FocusAdapter() {
		
			//Stores the original text before the user starts editing
			private String originalTextOnFocusStart;
			
			//When user clicks on this text box, store the text and reset its outline
			@Override
			public void focusGained(FocusEvent e) {
				
				originalTextOnFocusStart = beginDateField.getText();
				beginDateField.putClientProperty("JComponent.outline", null);
				
			}
			
			//When the user clicks out of this text box, check if the newly inputed date is valid.
			@Override
			public void focusLost(FocusEvent e) {
				
				Date inputDate = Date.parse(beginDateField.getText(), FileIO.getDateFormat());
				
				//If the date is unreadable, reverse the user's change
				if(inputDate == null) {
					
					beginDateField.setText(originalTextOnFocusStart);
					beginDateField.putClientProperty("JComponent.outline", "error");
					
				} else if(inputDate.compareTo(Date.parse(endDateField.getText(), FileIO.getDateFormat())) > 0) {
					
					//If the beginning date is after the end date
					
					//Shift the end date by the same amount 
					Date originalDate = Date.parse(originalTextOnFocusStart, FileIO.getDateFormat());
					int difference = Date.daysBetween(originalDate, inputDate);
					Date newEndDate = Date.dateWithDaysSinceYear0(Date.daysSinceYear0(Date.parse(endDateField.getText(), FileIO.getDateFormat())) + difference);
					endDateField.setText(newEndDate.toString(FileIO.getDateFormat()));
					
				}
				
			}
		
		});
		
		//When the user presses enter, make the text box lose focus, activating the focus listener
		endDateField.addKeyListener(new KeyAdapter() {
		
			@Override
			public void keyPressed(KeyEvent e) {
								
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
										
					//When this code is executed, dateField loses focus, which calls on its focusLost method below
					eventField.requestFocus();
					
				}
				
			}
		
		});
		
		//Make sure that the dates that the user inputs are valid
		endDateField.addFocusListener(new FocusAdapter() {
			
			//Stores the original text before the user starts editing
			private String originalTextOnFocusStart;
			
			//When user clicks on this text box, store the text and reset its outline
			@Override
			public void focusGained(FocusEvent e) {
				
				originalTextOnFocusStart = endDateField.getText();
				endDateField.putClientProperty("JComponent.outline", null);
				
			}
			
			//When the user clicks out of this text box, check if the newly inputed date is valid.
			@Override
			public void focusLost(FocusEvent e) {
				
				Date inputDate = Date.parse(endDateField.getText(), FileIO.getDateFormat());
				
				//If the date is unreadable, reverse the user's change
				if(inputDate == null) {
					
					endDateField.setText(originalTextOnFocusStart);
					endDateField.putClientProperty("JComponent.outline", "error");
					
				} else if(inputDate.compareTo(Date.parse(beginDateField.getText(), FileIO.getDateFormat())) < 0) {
					
					//If the beginning date is after the end date
					
					//Shift the end date by the same amount 
					Date originalDate = Date.parse(originalTextOnFocusStart, FileIO.getDateFormat());
					int difference = Date.daysBetween(originalDate, inputDate);
					Date newEndDate = Date.dateWithDaysSinceYear0(Date.daysSinceYear0(Date.parse(beginDateField.getText(), FileIO.getDateFormat())) - difference);
					beginDateField.setText(newEndDate.toString(FileIO.getDateFormat()));
					
				}
				
			}
		
		});
		
		//Change appearance, change button icon, and save preference to file
		appearanceButton.addActionListener((e) -> {
			
			//Find out what the current look and feel is
			boolean isSwitchingToDarkMode = false;
			if(UIManager.getLookAndFeel().getClass().equals(FlatLightLaf.class)) {
				
				//Currently, it is light mode, so we're going to switch to dark mode
				isSwitchingToDarkMode = true;
				
			}
			
			//Switch the appearance button icon, and get the new look and feel
			LookAndFeel lookAndFeel = switchAppearanceButtonIcon(isSwitchingToDarkMode);
			prefWindow.switchAppearanceButtonIcon(isSwitchingToDarkMode);
			
			//Actually apply the new look and feel to the program
			try {
				
				//Change this setting in FileIO
				FileIO.setUseDarkMode(isSwitchingToDarkMode);
				UIManager.setLookAndFeel(lookAndFeel);
				
			} catch (UnsupportedLookAndFeelException ex) {

				ex.printStackTrace();

			}
			
			//Updating pop-up MENUS (not windows)
			SwingUtilities.updateComponentTreeUI(eventsPopupMenu);
			//Updating the windows
			((ToDoWindow) SwingUtilities.getWindowAncestor(this.getParent())).reloadWindows();
			
		});
		
		//When clicked, delete the event
		deletePopupMenuItem.addActionListener((e) -> {
			
			deleteSelectedEvent();
			
		});
		
		//When clicked, mark the event as done
		finishPopupMenuItem.addActionListener((e) -> {
			
			finishSelectedEvent();
			
		});
		
		//When clicked, unmark the event as done
		unfinishPopupMenuItem.addActionListener((e) -> {
			
			unfinishSelectedEvent();
			
		});
		
	}
	
	/**
	 * Get a String representation of the day whose events should be displayed<br>
	 * Will use the date format provided by <code>FileIO</code>
	 * @return A String representation of the day that <code>calendar</code> is currently on
	 */
	private String getCurrentDay() {
		
		switch(FileIO.getDateFormat()) {
		
		case DMY:
			return calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
		case MDY:
			return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
		case YMD:
			return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
		default:
			throw new UnsupportedOperationException();
		
		}
		
	}
	
	/**
	 * Get a String representation of the day whose events should be displayed<br>
	 * Will use longer names, for example "Wednesday, January 1, 2020"
	 * @return A longer String representation of the day that <code>calendar</code> is currently on
	 */
	private String getCurrentDayLong() {
		
		//Using the calendar object to produce the display name
		
		long originalTime = calendar.getTimeInMillis();
		calendar.clear();
		calendar.set(todayDate.getYear(), todayDate.getMonth() - 1, todayDate.getDay());
		String temp = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getLocale()) + ", " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getLocale()) + " " + calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR);
		calendar.setTimeInMillis(originalTime);
		return temp;
		
	}
	
	/**
	 * Get a Date object representation of the day whose events should be displayed<br>
	 * @return A Date object that represents the day that <code>calendar</code> is currently on
	 */
	private Date getCurrentDayObject() {
		
		return new Date(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
		
	}
	
	/**
	 * Load a date's events to the event list.<br> 
	 * Finished events will be shown between their begin and finish dates if the finish date is before the end date.<br>
	 * Otherwise, events are shown between the begin and end dates.
	 */
	private void loadEvents() {
		
		//Clear all events from the table model.
		while(eventsTableModel.getRowCount() != 0) {
			
			eventsTableModel.removeRow(0);
			
		}
		
		//Loop through all available events, and then find the ones that pertain to the date.
		ArrayList<Event> events = FileIO.getEvents();
		//The date whose events should be displayed
		Date viewingDate = getCurrentDayObject();
		//Every single event saved to the data file will be checked in this for-loop.
		for(int i = 0; i < events.size(); i++) {
			
			//Check if the viewing date is after the event's begin date. Only then will the event have a chance of being shown.
			if(viewingDate.compareTo(events.get(i).getBeginDate()) >= 0) {
				
				//The latest date at which this event will be shown in
				Date latestShowingDate;
				
				if(events.get(i).getFinishedDate() != null && events.get(i).getFinishedDate().compareTo(events.get(i).getEndDate()) <= 0) {
					
					//The event has a finish date, and the finish date is before the event's end date, so the latest showing date would be its finish date.
					latestShowingDate = events.get(i).getFinishedDate();
					
				} else {
					
					//The event has no finish date, so the latest showing date would be its end date.
					latestShowingDate = events.get(i).getEndDate();
					
				}
				
				//At last, if the viewing date is before the latest showing date (and it is already known that the viewing date comes after the begin date),
				//add the event to the table model.
				if(viewingDate.compareTo(latestShowingDate) <= 0) {
					
					eventsTableModel.addRow(new Event[] {events.get(i)});
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Add the event that is currently in the event input field, both to the table and to FileIO
	 */
	private void addInputFieldEvent() {
		
		//Try to create a new event with the begin date, end date, and title.
		//The begin and end dates should be valid, because invalid dates are prevented in their text fields' focus listeners.
		Event newEvent = new Event(Date.parse(beginDateField.getText(), FileIO.getDateFormat()), Date.parse(endDateField.getText(), FileIO.getDateFormat()), eventField.getText());
		//Add it FileIO
		FileIO.addEvent(newEvent);
		//Add it to the table
		eventsTableModel.addRow(new Event[] {newEvent});
		//Clear the text box
		eventField.setText("");
		
	}
	
	/**
	 * Delete the event that is currently selected in the table, both from the table and from FileIO
	 */
	private void deleteSelectedEvent() {
		
		if(eventsTable.getCellEditor() != null) {
			
			eventsTable.getCellEditor().stopCellEditing();
			
		}
		
		Event selectedEvent = (Event) eventsTableModel.getValueAt(eventsTable.getSelectedRow(), eventsTable.getSelectedColumn());
		//Try to remove event
		if(FileIO.removeEvent(selectedEvent)) {
			
			//If the event was successfully removed, then remove from display
			eventsTableModel.removeRow(eventsTable.getSelectedRow());
			
		}
		
	}
	
	/**
	 * Swap two rows
	 * @param row1 The index of row 1
	 * @param row2 The index of row 2
	 * @throws UnsupportedOperationException When there are more than 1 columns in the table
	 */
	private void swapRows(int row1, int row2) {
		
		//Go column by column, replacing the value at row 1 with the value at row 2, and then replacing the value at row 2 with the original row 1 value
		for(int col = 0; col < eventsTableModel.getColumnCount(); col++) {
			
			//Note that column 0 is the Event column (and as of UI version 1.3, this is the only column)
			if(col == 0) {
				
				//Create a new Event that was the original row 1 value. This copy is a deep copy so that it is insulated from the following change
				Event originalRow1Value = new Event((Event) eventsTableModel.getValueAt(row1, col));
				//Change the row 1 value to the row 2 value
				eventsTableModel.setValueAt(eventsTableModel.getValueAt(row2, col), row1, col);
				//Change the row 2 value to the original row 1 value
				eventsTableModel.setValueAt(originalRow1Value, row2, col);
				
			} else {
				
				//Since other columns aren't implemented yet, throw an UnsupportedOperationException
				throw new UnsupportedOperationException();
				
			}
			
		}
		
	}
	
	/**
	 * Set the finish date of the selected event as today's IRL date
	 */
	private void finishSelectedEvent() {
		
		Event selectedEvent = (Event) eventsTableModel.getValueAt(eventsTable.getSelectedRow(), eventsTable.getSelectedColumn());
		selectedEvent.setFinishedDate(todayDate);
		
	}
	
	/**
	 * Remove the finish date of the selected event
	 */
	private void unfinishSelectedEvent() {
		
		Event selectedEvent = (Event) eventsTableModel.getValueAt(eventsTable.getSelectedRow(), eventsTable.getSelectedColumn());
		selectedEvent.setFinishedDate(null);
		
	}
	
	/**
	 * Set the date whose events should be displayed as the date in the <code>dateField</code>
	 */
	private void moveToDateFieldDate() {
		
		//The date that is in dateField
		Date targetDate = Date.parse(dateField.getText(), FileIO.getDateFormat());
		if(targetDate != null) {
			
			//The date that was in dateField was valid.
			
			calendar.set(Calendar.YEAR, targetDate.getYear());
			calendar.set(Calendar.MONTH, targetDate.getMonth() - 1);
			calendar.set(Calendar.DAY_OF_MONTH, targetDate.getDay());
			loadEvents(); //Reload event display
			
			beginDateField.setText(dateField.getText());
			endDateField.setText(dateField.getText());
			
			//Put the focus on the event field, both to signify that the date has been changed and for user convenience
			eventField.requestFocus();
			
		} else {
			
			//The date that was in dateField was invalid. Reset the text and give an error outline
			dateField.putClientProperty("JComponent.outline", "error");
			dateField.setText(getCurrentDay());
			
		}
		
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
			appearanceButton.setIcon(new ImageIcon(getClass().getResource("/lightmode.png")));
			
		} else {
			
			//Switching to light mode
			
			//Set the new look and feel to FlatLightLaf (light mode)
			lookAndFeel = new FlatLightLaf();
			//Update the icon so that it looks like it allows the user to switch back to dark mode
			appearanceButton.setIcon(new ImageIcon(getClass().getResource("/darkmode.png")));
			
		}
		
		return lookAndFeel;
		
	}
	
	/**
	 * Request the focus to be transferred to the input event field
	 */
	public void requestEventFieldFocus() {
		
		eventField.requestFocusInWindow();
		
	}
	
	/**
	 * @return The help window connected to this pane
	 */
	public InfoWindow getHelpWindow() {
		
		return helpWindow;
		
	}
	
	/**
	 * @return The about window connected to this pane
	 */
	public InfoWindow getAboutWindow() {
		
		return aboutWindow;
		
	}
	
	/**
	 * @return The preference window connected to this pane
	 */
	public PrefWindow getPrefWindow() {
		
		return prefWindow;
		
	}
	
	/**
	 * Reloads the look and feel
	 */
	public void updateLookAndFeel() {
		
		//Updating pop-up MENUS (not windows)
		SwingUtilities.updateComponentTreeUI(eventsPopupMenu);
		//Updating the windows
		((ToDoWindow) SwingUtilities.getWindowAncestor(this.getParent())).reloadWindows();
		
	}
	
	public void updateColorsAndDateFormats() {
		
		//Updating the colors in the table
		eventsTableModel.fireTableDataChanged();
		//Reloading all of the date fields
		dateField.setText(getCurrentDay());
		beginDateField.setText(getCurrentDay());
		endDateField.setText(getCurrentDay());
		
	}
	
	/**
	 * A custom event cell renderer because the default one is bad 
	 * @author James
	 */
	private class EventCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 6605786845441897134L;

		//Note: the table calls this method to get the component that displays the cell as specified by row and column
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			//The RGB of the cell's background
			int[] bgRGB;
			//The cell renderer component itself
			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			//If the value isn't even an Event, then treat it like a regular value. Don't forget to reset the background, however.
			if(!(value instanceof Event)) {
				
				//If the value is selected, then use the background color for selected cells. Otherwise, just use the table's background.
				if(isSelected) {
					
					component.setBackground(table.getSelectionBackground());
					
				} else {
					
					component.setBackground(table.getBackground());
					
				}
				
				//Return the component now so the rest of the code in this method is skipped.
				//If the rest of the code isn't skipped, then the (Event) type cast will cause errors
				return component;
				
			}
			
			//If the code reaches here, then the value is an Event.
			//The component's background will be colored depending if it's overdue or finished or if the program uses light or dark mode.
			//The component will be modified in this big if-else statement, and after it the component (now modified) will be returned 
			
			if(((Event) value).isOverdue(todayDate)) {
				
				//The event is overdue today.
				
				if(FileIO.getUseDarkMode()) {
					
					bgRGB = FileIO.getDarkOverdueEventColorRGB();
					
				} else {
					
					bgRGB = FileIO.getLightOverdueEventColorRGB();
					
				}
				
				component.setBackground(new Color(bgRGB[0], bgRGB[1], bgRGB[2]));
				
			} else {
				
				//The event is not overdue today, meaning that 
				//the event is finished, OR its end date isn't before today
				
				if(((Event) value).getFinishedDate() == null) {
					
					//Since the event isn't finished, its end date must not have been before today. 
					//Therefore, the component will have no special coloring
					
					if(isSelected) {
						
						component.setBackground(table.getSelectionBackground());
						
					} else {
						
						component.setBackground(table.getBackground());
						
					}
					
				} else {
					
					//The event is finished. This means that the end date must have been before today, but that doesn't matter because the event is finished.
					if(FileIO.getUseDarkMode()) {
						
						bgRGB = FileIO.getDarkFinishedEventColorRGB();
						
					} else {
						
						bgRGB = FileIO.getLightFinishedEventColorRGB();
						
					}
					
					component.setBackground(new Color(bgRGB[0], bgRGB[1], bgRGB[2]));
					
				}
				
			}
			
			((JComponent) component).setToolTipText(((Event) value).getEventTitle());
			
			return component;

		}
		
	}
	
	/**
	 * A pop up window that provides information about the program
	 * @author James
	 */
	public class InfoWindow extends JFrame {
		
		private static final long serialVersionUID = -3787578821278356715L;
		
		private static final int GAP_FROM_WINDOW_EDGES = 15;
		private static final String DIRECTIONS_STR = 
				"<html>"
				+ "<b>Create</b> an event by typing it into the event text box and <u>hitting enter</u><br></br><br></br>"
				+ "<b>Delete</b> an event by <u>right clicking on it</u> and hitting \"Delete\", or selecting it and <u>hitting the delete key</u><br></br><br></br>"
				+ "<b>Set an event's end and begin dates</b> in their text boxes<br></br><br></br>"
				+ "<b>Edit</b> an event title by <u>double clicking</u> on it<br></br><br></br>"
				+ "<b>Move an event</b> up or down the list by selecting it, holding control, and then using the up and down arrow keys<br></br><br></br>"
				+ "<b>Mark an event as done</b> by <u>right clicking it</u> and hitting \"Mark as Done,\" or by <u>clicking and holding</u> on the event<br></br><br></br>"
				+ "<b>Unmark an event as done</b> by <u>right clicking it</u> and hitting \"Unmark as Done\"<br></br><br></br>"
				+ "<b>Roll forwards or backwards</b> the date with the arrow buttons<br></br><br></br>"
				+ "<b>Jump to a date</b> by directly typing the date into the text box<br></br><br></br>"
				+ "</html>";
		private final String ABOUT_STR = 
				"<html>"
				+ "<b>To-Do List</b> (using Java " + System.getProperty("java.version") + ")<br></br>"
				+ "UI version: " + ToDoWindow.UI_VERSION + "<br></br>"
				+ "IO version: " + FileIO.IO_VERSION + "<br></br>"
				+ "File version: " + FileIO.getOriginalFileVersion() + "<br></br>"
				+ "</html>";
		private final ImageIcon windowIcon = new ImageIcon(getClass().getResource("/todoicon.png"));
		private final Font titleFont = new Font("Segoe UI Semibold", Font.PLAIN, 24);
		
		/**
		 * Creates a pop up information window
		 * @param type The type of info window: <br>
		 * HELP gives a window for directions on how to use the program, <br>ABOUT gives a window for information on the program's version
		 */
		public InfoWindow(HelpWindowType type) {
			
			//Properties of the window
			int width;
			int height;
			String title;
			String content;
			Font contentFont;
			
			//Set the above window properties depending on the window type
			switch(type) {
			
			case ABOUT:
				width = 300;
				height = 215;
				title = "About";
				content = ABOUT_STR;
				contentFont = new Font("Segoe UI", Font.PLAIN, 14);
				break;
			case HELP:
				width = 750;
				height = 510;
				title = "Help";
				content = DIRECTIONS_STR;
				contentFont = new Font("Segoe UI", Font.PLAIN, 14);
				break;
			default:
				throw new UnsupportedOperationException();
			
			}
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(width, height);
			setTitle(title);
			setIconImage(windowIcon.getImage());
			setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
			
			//Contains nearly all of the components of this window
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.setAlignmentX(LEFT_ALIGNMENT);
			
			//Contains the title of this window
			JLabel titleLabel = new JLabel(title);
			titleLabel.setFont(titleFont);
			mainPanel.add(Box.createRigidArea(new Dimension(0, GAP_FROM_WINDOW_EDGES)));
			mainPanel.add(titleLabel);
			
			//Contains the main text of this window
			JLabel contentLabel = new JLabel(content);
			contentLabel.setFont(contentFont);
			
			//Contains the OK button
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
			JButton okayButton = new JButton("OK");
			okayButton.addActionListener((e) -> {dispose();}); //When the OK button is clicked, dispose the window.
			buttonPanel.add(Box.createHorizontalGlue()); //Push the OK button to the very right.
			buttonPanel.add(okayButton);
			
			mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			mainPanel.add(contentLabel);
			mainPanel.add(Box.createVerticalGlue());
			mainPanel.add(buttonPanel);
			mainPanel.add(Box.createRigidArea(new Dimension(0, GAP_FROM_WINDOW_EDGES)));
			
			add(Box.createRigidArea(new Dimension(GAP_FROM_WINDOW_EDGES, 0)));
			add(mainPanel);
			add(Box.createRigidArea(new Dimension(GAP_FROM_WINDOW_EDGES, 0)));
			
		}
		
	}
	
	public static void main(String[] args) {
		
		ToDoWindow.main(args);
		
	}

}
