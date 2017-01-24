package nighttimedriver.chat.message;

import java.util.Calendar;

/**
 * An immutable object that holds a number of milliseconds since the epoch, that
 * is, midnight on January 1, 1970.
 * 
 * @author NighttimeDriver50000
 */
public class Timestamp {
	/**
	 * The epoch, that is, midnight on January 1, 1970.
	 */
	public static final Timestamp EPOCH = create(0);

	/**
	 * The internal Calendar that holds the timestamp's value.
	 */
	private final long time;

	/**
	 * Constructs a new Timestamp with the given milliseconds from the epoch.
	 * 
	 * @param time
	 *            the milliseconds since the epoch to use for this timestamp
	 */
	private Timestamp(final long time) {
		this.time = time;
	}

	/**
	 * Creates a new Timestamp with the current time.
	 * 
	 * @return the new Timestamp
	 */
	public static Timestamp create() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Creates a new Timestamp with the time from the given Calendar.
	 * 
	 * @param time
	 *            the Calendar to use for this timestamp
	 * @return the new Timestamp
	 */
	public static Timestamp create(final Calendar time) {
		return new Timestamp(time.getTimeInMillis());
	}

	/**
	 * Creates a new Timestamp with the given milliseconds from the epoch.
	 * 
	 * @param time
	 *            the milliseconds since the epoch to use for this timestamp
	 * 
	 * @return the new Timestamp
	 */
	public static Timestamp create(final long time) {
		return new Timestamp(time);
	}

	/**
	 * Returns the number of milliseconds since the epoch that this timestamp
	 * represents.
	 * 
	 * @return the milliseconds since the epoch for this timestamp
	 */
	public long getMillisTime() {
		return time;
	}

	/**
	 * Creates a new Calendar that holds the same millisecond time as this
	 * timestamp. The locale and time zone are dependent on the system.
	 * 
	 * @param time
	 *            the milliseconds since the epoch to use for this Calendar
	 * @return the new Calendar
	 */
	public Calendar createCalendar() {
		return createCalendar(time);
	}

	/**
	 * Creates a new Calendar with the given milliseconds from the epoch. The
	 * locale and time zone are dependent on the system.
	 * 
	 * @param time
	 *            the milliseconds since the epoch to use for this Calendar
	 * @return the new Calendar
	 */
	public static Calendar createCalendar(final long time) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}
}
