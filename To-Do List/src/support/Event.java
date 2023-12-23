package support;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * 
 * @author James
 * 
 * An object of this class represents an activity that the user plans to do for over the course of at least one day.
 * 
 */
public class Event implements Serializable {

	private static final long serialVersionUID = 1275174527668218125L;
	
	private Date beginDate;
	private Date endDate;
	private Date finishedDate;
	private String eventTitle;
	
	/**
	 * Constructs a new Event with a begin date, end date, and title. The finished date is automatically set to <code>null</code>, representing an unfinished event. <br>
	 * Precondition: <code>beginDate</code> must not come after <code>endDate</code> (they can the same day though)
	 * @param beginDate
	 * @param endDate
	 * @param eventTitle
	 * @throws InvalidParameterException If precondition isn't met
	 */
	public Event(Date beginDate, Date endDate, String eventTitle) {

		if(beginDate.compareTo(endDate) > 0) {

			throw new InvalidParameterException();
			
		}
		
		this.beginDate = (Date) beginDate.clone();
		this.endDate = (Date) endDate.clone();
		this.finishedDate = null;
		this.eventTitle = eventTitle;
		
	}
	
	/**
	 * Constructs a new Event that copies the data of the parameter <code>event</code> <br>
	 * Precondition: <code>event</code> cannot be null
	 * @param event Another Event object
	 */
	public Event(Event event) {
		
		this.beginDate = event.beginDate;
		this.endDate = event.endDate;
		this.finishedDate = event.finishedDate;
		this.eventTitle = event.eventTitle;
		
	}

	public Date getBeginDate() {

		return beginDate;

	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;

	}

	public Date getEndDate() {

		return endDate;

	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;

	}

	public String getEventTitle() {

		return eventTitle;

	}

	public void setEventTitle(String eventTitle) {

		this.eventTitle = eventTitle;

	}

	public Date getFinishedDate() {

		return finishedDate;

	}

	public void setFinishedDate(Date finishedDate) {

		this.finishedDate = finishedDate;

	}

	@Override
	public String toString() {
		
		return eventTitle;
		
	}
	
	public String completeToString() {
		
		return eventTitle + ": begins " + beginDate + ", ends " + endDate + ", finished " + finishedDate;
		
	}
	
	@Override
	public Object clone() {
		
		return new Event((Date) beginDate.clone(), (Date) endDate.clone(), eventTitle);
		
	}
	
	/**
	 * An Event is overdue with respect to <code>date</code> if it isn't finished and its end date is before <code>dueDate</code>
	 * @param date The date to use to determine if <code>this</code> Event is overdue. Usually, <code>date</code> is today's date
	 * @return True if <code>this</code> Event is overdue
	 */
	public boolean isOverdue(Date date) {
		
		return date.compareTo(endDate) > 0 && finishedDate == null;
		
	}

}
