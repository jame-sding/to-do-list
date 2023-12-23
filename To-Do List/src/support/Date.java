package support;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Scanner;

/**
 * 
 * A very simple Date class that is merely a container for three integer fields: month, day and year. There are also a few extra methods, as well as enumerated types that represent date formats.<br>
 * <b>Date instances are immutable.</b>
 * 
 * @author James
 *
 */
public class Date implements Comparable<Date>, Serializable {

	public static enum DateFormat {
		MDY {@Override public String toString() {return "MONTH/DAY/YEAR";}}, 
		DMY {@Override public String toString() {return "DAY/MONTH/YEAR";}}, 
		YMD {@Override public String toString() {return "YEAR/MONTH/DAY";}}
	}
	
	private static final long serialVersionUID = -7612517206700521638L;
	
	private final int month;
	private final int day;
	private final int year;
	
	/**
	 * Constructs a date object.
	 * 
	 * @param month Month, 1 representing January and 12 representing December
	 * @param day Day
	 * @param year Year
	 * @throws InvalidParameterException If the parameters make an invalid date.
	 */
	public Date(int month, int day, int year) {

		this.month = month;
		this.day = day;
		this.year = year;
		
		if(!isValidDate(this)) {
			
			throw new InvalidParameterException();
			
		}

	}

	/**
	 * @return The month of this Date object. 1 represents January, and 12 represents December.
	 */
	public int getMonth() {

		return month;

	}

	/**
	 * @return The day of month of this Date object.
	 */
	public int getDay() {

		return day;

	}

	/**
	 * @return The year of this Date object. The year 2020 would be represented as 2020.
	 */
	public int getYear() {

		return year;

	}
	
	@Override
	public String toString() {
		
		return "Day " + day + "; Month " + month + "; Year " + year;
		
	}
	
	/**
	 * When trying to get a user-friendly representation of this Date object, this method should be used instead of the regular <code>toString()</code> method.
	 * 
	 * @param dateFormat The date formatting style to be used
	 * @return A String representation of this object with format specified by <code>dateFormat</code>. Forward slashes "/" are used as separators.
	 */
	public String toString(DateFormat dateFormat) {
		
		switch(dateFormat) {
		
		case DMY:
			return day + "/" + month + "/" + year;
		case MDY:
			return month + "/" + day + "/" + year;
		case YMD:
			return year + "/" + month + "/" + day;
		default:
			throw new UnsupportedOperationException();
		
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj != null && obj.getClass().equals(this.getClass()) && ((Date) obj).month == this.month && ((Date) obj).day == this.day && ((Date) obj).year == this.year) {
			
			//The other object is not null, the other object is an Event, and the other object's month, day, and year are the same
			//Note that the order of the boolean expressions in this if statement matters, for example:
			//if the object is null, it would fail the first boolean expression and stop computing the rest of the boolean expressions
			//because the expressions are connected with &&.
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	@Override
	public Object clone() {
		
		return new Date(month, day, year);
		
	}
	
	@Override
	public int compareTo(Date o) {
		
		//Note: this method returns 1 if this Date is after the other Date, 0 if this Date is the same as the other Date, and -1 if this Date is before.
		
		if(this.year > o.year) {
			
			//If the year of this Date is larger than the other Date's year, then this Date is definitely after.
			return 1;
			
		} else if(this.year == o.year) {
			
			//The dates share a year.
			
			if(this.month > o.month) {
				
				//If the month of this Date is larger, then this Date is definitely after.
				return 1;
				
			} else if(this.month == o.month) {
				
				//The dates share a month.
				
				if(this.day > o.day) {
					
					//If the day of this Date is larger, then this Date is after.
					return 1;
					
				} else if(this.day == o.day) {
					
					//This Date shares the same day, month, and year with the other Date. They are the same.
					return 0;
					
				}
				
			}
			
		}
		
		//The other Date did not qualify as being after this Date, nor did it qualify for being the same.
		return -1;
		
	}
	
	/**
	 * Reads a String and tries to create a Date object given the intended date format and separators.<br>
	 * The String must express the month, day, and year as integers. Months are from 1-12.
	 * 
	 * @param str A string of month, day, year form separated by spaces and any other specified separator
	 * @param format The intended date format of <code>str</code>
	 * @param separators A list of characters that are intended to separate the month, day and years
	 * @return A Date object parsed from <code>str</code>, null if a date cannot be parsed or if the parsed date was invalid
	 */
	public static Date parse(String str, DateFormat format, char...separators) {
		
		//If there was not a list of separators provided, use the forwards slash and hyphen
		if(separators.length == 0) {
			
			separators = new char[] {'/', '-'};
			
		}
		
		//Replacing the separators with a space (for consistency)
		String dateStr = str;
		for(int i = 0; i < separators.length; i++) {
			
			dateStr = dateStr.replace(separators[i], ' ');
			
		}
		
		//Scanner for dateStr
		Scanner scanner = new Scanner(dateStr);
		
		try {
			
			int month;
			int day;
			int year;
			Date newDate = null;
			
			//The way str will be read depends on the provided date format.
			switch(format) {
			
			//If there are any weird characters in str (ex.: a letter), scanner.nextInt() will throw an exception that will be caught
			
			case DMY:
				day = scanner.nextInt();
				month = scanner.nextInt();
				year = scanner.nextInt();
				newDate = new Date(month, day, year);
				break;
				
			case MDY:
				month = scanner.nextInt();
				day = scanner.nextInt();
				year = scanner.nextInt();
				newDate = new Date(month, day, year);
				break;
				
			case YMD:
				year = scanner.nextInt();
				month = scanner.nextInt();
				day = scanner.nextInt();
				break;
				
			default: //The provided Date Format was not DMY, MDY, or YMD (this can happen if I added a new date format and forgot to implement this for it).
				scanner.close();
				throw new UnsupportedOperationException();
			
			}
			
			//If the code reaches here, then there are no weird characters in str.
			
			if(scanner.hasNext()) {
				
				//There is extra text in str. Such an error wouldn't be caught by the nextInt() methods.
				scanner.close();
				return null;
				
			} else {
				
				//There is no extra text, and there are no weird characters.
				
				scanner.close();
				
				//Finally, if the parsed date is valid, return it. If it isn't valid, return null.
				if(isValidDate(newDate)) {
					
					return newDate;
					
				} else {
					
					return null;
					
				}
				
			}
			
		} catch (Exception e) {
			
			//There was some kind of error when the scanner was doing nextInt().
			//(str might've had extra characters)
			scanner.close();
			return null;
			
		}
		
	}
	
	/**
	 * Checks if a date is valid (exists on the calendar)
	 * @param date The date to check for
	 * @return True if the date is valid
	 */
	public static boolean isValidDate(Date date) {
		
		//If the month isn't between 1 and 12, then the date is invalid (I will still allow negative years though because funny and it still somewhat makes sense)
		if(date.month > 12 || date.month < 1) {
			
			return false;
			
		}
		
		if(date.month == 1 || date.month == 3 || date.month == 5 || date.month == 7 || date.month == 8 || date.month == 10 || date.month == 12) {
			
			//The month is one of the months that have 31 days. Check if the day is in between 1 and 31.
			if(date.day >=1 && date.day <= 31) {
				
				return true;
				
			}
			
		} else {
			
			//The month is one of the months that have less than 31 days. All of these months except for February have 30 days.
			if(date.month != 2) {
				
				//The month is not February, so if the day is in between 1 and 30 it is valid.
				if(date.day >= 1 && date.day <= 30) {
					
					return true;
					
				}
				
			} else {
				
				//The month is February, so check if it is a leap year or not (leap year Februaries have an extra 29th day)
				if(isLeapYear(date.year)) {
					
					//It is a leap year, so if the day is in between 1 and 29 it is valid.
					if(date.day >= 1 && date.day <= 29) {
						
						return true;
						
					}
					
				} else if(date.day >= 1 && date.day <= 28) {
					
					//It wasn't a leap year, but the day is between 1 and 28. It is valid.
					return true;
					
				}
				
			}
			
		}
		
		//The date did not meet any of the qualifications above.
		return false;
		
	}
	
	/**
	 * Checks if a year is a leap year
	 * @param year The year to check for
	 * @return True if <code>year</code> is a leap year
	 */
	public static boolean isLeapYear(int year) {
		
		//Check if the year is divisible by 4
		if(year % 4 != 0) {
			
			//If the year isn't divisible by 4, it is definitely not a leap year.
			return false;
			
		} else if(year % 100 == 0) {
			
			//If the code reaches here, then the year is divisible by 100. This has complications.
			
			//If the year is divisible by 400, it is a leap year. If it is divisible by 100 but not 400, it isn't a leap year.
			//For example, the years 1200, 1600, and 2000 are leap years. 900, 1800, and 1900 aren't.
			if(year % 400 == 0) {
				
				return true;
				
			} else {
				
				return false;
				
			}
			
		} else {
			
			//The year is divisible by 4 but not 100, so it is a leap year.
			return true;
			
		}
		
	}
	
	/**
	 * 
	 * @param date1
	 * @param date2
	 * @return The number of days between <code>date1</code> and <code>date2</code>
	 */
	public static int daysBetween(Date date1, Date date2) {
		
		return Math.abs(daysSinceYear0(date1) - daysSinceYear0(date2));
		
	}
	
	/**
	 * 
	 * @param date 
	 * @return The number of days between January 1st, 0 and <code>date</code>
	 */
	public static int daysSinceYear0(Date date) {
		
		int days = 0;
		
		//Add 365 days for each COMPLETE year BEFORE the parameter date
		days += date.year * 365;
		//A year is a leap year if it is divisible by 4 but not 100, and it is a leap year if it is divisible by 400
		//Add an extra day for each year BEFORE THIS YEAR that is divisible by 4
		days += (date.year - 1) / 4;
		//Then take back the extra day for each year BEFORE THIS YEAR that is divisible by 100 
		days -= (date.year - 1) / 100;
		//Then re-add the extra day that was taken back for each year BEFORE THIS YEAR that is divisible by 400
		days += (date.year - 1) / 400;
				
		//Add the days for each COMPLETE month WITHIN the parameter date's year before the parameter date
		int daysInYear = 0;
		for(int month = 1; month < date.month; month++) {
			
			daysInYear += getNumberOfDaysInMonth(month, date.year);
			
		}
		
		//Add the days WITHIN the parameter's month before the parameter date
		daysInYear += date.day;
		
		return days + daysInYear;
		
	}
	
	/**
	 * 
	 * @param daysSinceYear0 An <code>int</code> representing a number of days since year 0
	 * @return The Date that corresponds with <code>daysSinceYear0</code>
	 */
	public static Date dateWithDaysSinceYear0(int daysSinceYear0) {
		
		int daysRemaining = daysSinceYear0;
		
		int yearCount = 0;
		
		//We will find which year this lies in by dividing time in terms of 400 year, 100 year, 4 year, and 1 year periods
		//This is because 400 year periods have a set amount of days (365*400 + 400/4 - 400/100 + 400/400 = 146097 days), 
		//100 year periods have 365*100 + 100/4 - 100/100 = 36524 days (unless they're a 4n 100-year period, in which then they have an extra leap day)
		//4 year periods have 365*4 + 4/4 = 1461 days (unless they're a 25n 4-year period, in which then they have one less leap day)
		//1 year periods have 365 days (unless they're a 4n 1-year period, in which then the year is a leap year and has a leap day).
		
		//Find how many 400 year periods are covered in daysSinceYear0. ALL 400 YEAR PERIODS have 146097 days
		int num400YearPeriods = (daysRemaining - 1) / 146097;
		yearCount += num400YearPeriods * 400;
		daysRemaining -= num400YearPeriods * 146097;
		
		//Now, we have one to three 100 year periods remaining (we can't have four or over because four 100 year periods = one 400 year period, which would've already been counted)
		int numRemainder100YearPeriods = (daysRemaining - 1) / 36524;
		yearCount += numRemainder100YearPeriods * 100;
		daysRemaining -= numRemainder100YearPeriods * 36524;
		
		//Now, we have one to twenty-four 4 year periods remaining (we can't have twenty-five or over because twenty-five 4 year periods = one 100 year period, which would've already been counted)
		int numRemainder4YearPeriods = (daysRemaining - 1) / 1461;
		yearCount += numRemainder4YearPeriods * 4;
		daysRemaining -= numRemainder4YearPeriods * 1461;
		
		//Now, we have one to three 1 year periods remaining (we can't have four or over because four sets of 1 year periods = one 4 year period, which would've already been counted)
		int numRemainder1YearPeriods = (daysRemaining - 1) / 365;
		yearCount += numRemainder1YearPeriods * 1;
		daysRemaining -= numRemainder1YearPeriods * 365;
		
		//Now, daysRemaining is just the amount of days in the year that daysSinceYear0 is in
		//We have to find the month and day
		
		//Go through every month of the year until there are no more days remaining
		int month;
		for(month = 1; month <= 12; month++) {
			
			daysRemaining -= getNumberOfDaysInMonth(month, yearCount);
			
			if(daysRemaining <= 0) {
				
				//There's no more days remaining. However, we have subtracted too much from daysRemaining.
				daysRemaining += getNumberOfDaysInMonth(month, yearCount);
				
				//Furthermore, if it is a leap year, we must re-add a leap day.
				//This is because we then would have taken the leap day into consideration twice: once when we were counting years and again when getNumberOfDaysInMonth was called for month 2
				if(isLeapYear(yearCount)) {
					
					daysRemaining++;
					
				}
				
				break;
				
			}
			
		}
		
		return new Date(month, daysRemaining, yearCount);
		
	}
	
	public static int getNumberOfDaysInMonth(int month, int year) {
		
		switch(month) {
		
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 2:
			if(Date.isLeapYear(year)) {
				
				return 29;
				
			} else {
				
				return 28;
				
			}
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		default:
			throw new UnsupportedOperationException();
			
		}
		
	}

}
