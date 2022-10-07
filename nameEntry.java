import java.util.ArrayList;

//On my honor:
//
//- I have not discussed the Java language code in my program with
//anyone other than my instructor or the teaching assistants
//assigned to this course.
//
//- I have not used Java language code obtained from another student,
//or any other unauthorized source, including the Internet, either
//modified or unmodified.
//
//- If any Java language code or documentation used in my program
//was obtained from another source, such as a text book or course
//notes, that has been clearly noted with a proper citation in
//the comments of my program.
//
//- I have not designed this program in such a way as to defeat or
//interfere with the normal operation of the supplied grading code.
//
//<Jae Won Shin>
//<jaewons 9061-34703>
/**
 * The data objects that will be stored in the hashtable.
 * @author Jae Won Shin
 *
 */
public class nameEntry implements Hashable<nameEntry> {
	private String key; // GIS feature name and state abbreviation
	private ArrayList<Long> locations; // file offsets of matching records
	private int maxSize; // length of locations array list

	/**
	 * Initialize a new nameEntry object with the given feature name
	 */
	public nameEntry(String name) {
		key = name;
		maxSize = 10;
		locations = new ArrayList<Long>(maxSize);
	}
	
	/**
	 * Initialize a new nameEntry object with the given feature name and a single
	 * file offset.
	 */
	public nameEntry(String name, Long offset) {
		key = name;
		maxSize = 10;
		locations = new ArrayList<Long>(maxSize);
		locations.add(offset);
	}

	/**
	 * Return feature name.
	 */
	public String key() {
		return key;
	}

	/**
	 * Return list of file offsets.
	 */
	public ArrayList<Long> locations() {
		return locations;
	}

	/**
	 * Append a file offset to the existing list.
	 */
	public boolean addLocation(Long offset) {
		if (offset != null)
		{
			if (locations.size() == maxSize)
			{
				locations.ensureCapacity(maxSize + 10);
				maxSize = maxSize + 10;
			}
			locations.add(offset);
			return true;
		}
		return false;
	}

	/**
	 * Donald Knuth hash function for strings. You MUST use this.
	 */
	public int Hash() {
		int hashValue = key.length();
		for (int i = 0; i < key.length(); i++) {
			hashValue = ((hashValue << 5) ^ (hashValue >> 27)) ^ key.charAt(i);
		}
		return (hashValue & 0x0FFFFFFF);
	}

	/**
	 * Two nameEntry objects are considered equal iff they hold the same feature
	 * name.
	 */
	public boolean equals(Object other) {
		if (other == null)
		{
			return false;
		}
		if (this.getClass() == other.getClass())
		{
			nameEntry obj = (nameEntry) other;
			return this.key().equals(obj.key());
		}
		return false;
	}

	/**
	 * Return a String representation of the nameEntry object in the format needed
	 * for this assignment.
	 */
	public String toString() {
		return ("[" + key + ", " + locations.toString() + "]");
	}
}
