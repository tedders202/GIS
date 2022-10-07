import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

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
 * The name index using hashtable implementations specified below.
 * 
 * Implements a generic chained hash table, using an ArrayList of LinkedLists
 * for the physical table.
 *
 * The ArrayList has a default size of 256 slots, configurable via the class
 * constructor.
 *
 * The size of the ArrayList is doubled when the load factor exceeds the load
 * limit (defaulting to 0.7, but configurable via the class constructor).
 *
 * Elements inserted to the table must implement the Hashable interface:
 *
 * public int Hash();
 *
 * This allows the user to choose an appropriate hash function, rather than
 * being tied to a fixed hash function selected by the table designer.
 */
public class HashTable<T extends Hashable<T>> {

	public ArrayList<LinkedList<T>> table; // physical basis for the hash table
	public Integer numElements = 0; // number of elements in all the chains
	public Double loadLimit = 1.0; // table resize trigger
	public Integer tableSize = 256; // default number of table slots
	public int maxSlotLength = 1; // Largest linked list

	/**
	 * Constructs an empty hash table with the following properties: Pre: - size is
	 * the user's desired number of slots; null for default - ldLimit is user's
	 * desired load factor limit for resizing the table; null for the default Post:
	 * - table is an ArrayList of size LinkedList objects, 256 slots if size == null
	 * - loadLimit is set to default (1) if ldLimit == null
	 */
	public HashTable(Integer size, Double ldLimit) {
		if (size == null)
		{
			table = new ArrayList<LinkedList<T>>(tableSize);
			// Add null so that the indexes can be accessed
			for (int i = 0; i < tableSize; i++)
			{
				table.add(null);
			}
		}
		else
		{
			tableSize = size;
			table = new ArrayList<LinkedList<T>>(tableSize);
			// Add null so that the indexes can be accessed
			for (int i = 0; i < tableSize; i++)
			{
				table.add(null);
			}
		}
		if (ldLimit != null)
		{
			loadLimit = ldLimit;
		}
	}

	/**
	 * Inserts elem at the front of the elem's home slot, unless that slot already
	 * contains a matching element (according to the equals() method for the user's
	 * data type. Pre: - elem is a valid user data object Post: - elem is inserted
	 * unless it is a duplicate - if the resulting load factor exceeds the load
	 * limit, the table is rehashed with the size doubled Returns: true iff elem has
	 * been inserted
	 */
	@SuppressWarnings({ "unused" })
	public boolean insert(T elem) {
		int index = elem.Hash() % tableSize;
		if (elem == null)
		{
			return false;
		}
		if (table.get(index) == null)
		{
			LinkedList<T> obj = new LinkedList<T>();
			obj.add(elem);
			table.set(index, obj);
			numElements++;
			return true;
		}
		else
		{
			LinkedList<T> tableObj = table.get(index);
			for (int i = 0; i < tableObj.size(); i++)
			{
				if (tableObj.get(i).equals(elem))
				{
					return false;
				}
			}
			tableObj.add(elem);
			numElements++;
			// Checks the load limit and rehashes if necessary
			double load = numElements / (double) tableSize;
			if (load > loadLimit)
			{
				reHash();
			}
			if (tableObj.size() > maxSlotLength)
			{
				maxSlotLength = tableObj.size();
			}
			return true;
		}
	}

	/**
	 * Searches the table for an element that matches elem (according to the
	 * equals() method for the user's data type). Pre: - elem is a valid user data
	 * object Returns: reference to the matching element; null if no match is found
	 */
	public T find(T elem) {
		int index = elem.Hash() % tableSize;
		if (table.get(index) == null)
		{
			return null;
		}
		else
		{
			LinkedList<T> tableObj = table.get(index);
			for (int i = 0; i < tableObj.size(); i++)
			{
				if (tableObj.get(i).equals(elem))
				{
					return tableObj.get(i);
				}
			}
			return null;
		}
	}

	/**
	 * Removes a matching element from the table (according to the equals() method
	 * for the user's data type). Pre: - elem is a valid user data object Returns:
	 * reference to the matching element; null if no match is found
	 */
	public T remove(T elem) {
		return elem;
	} // Not necessary for this assignment

	/**
	 * Writes a formatted display of the hash table contents. Pre: - fw is open on
	 * an output file
	 */
	public void display(FileWriter fw) throws IOException {
		fw.write("Number of elements: " + numElements + "\n");
		fw.write("Number of slots: " + table.size() + "\n");
		fw.write("Maximum elements in a slot: " + maxSlotLength + "\n");
		fw.write("Load limit: " + loadLimit + "\n");
		fw.write("\n");

		fw.write("Slot Contents\n");
		for (int idx = 0; idx < table.size(); idx++) {

			LinkedList<T> curr = table.get(idx);

			if (curr != null && !curr.isEmpty()) {

				fw.write(String.format("%5d: %s\n", idx, curr.toString()));
			}
		}
	}
	
	/**
	 * Rehashes the table when the number of total elements divided by the size of
	 * the table exceeds the load limit.
	 */
	private void reHash()
	{
		maxSlotLength = 1;
		int prevSize = tableSize;
		tableSize = tableSize * 2;
		ArrayList<LinkedList<T>> newTable = new ArrayList<LinkedList<T>>(tableSize);
		for (int h = 0; h < tableSize; h++)
		{
			newTable.add(null);
		}
		for (int i = 0; i < prevSize; i++)
		{
			LinkedList<T> slot = table.get(i);
			if (slot != null)
			{
				for (int j = 0; j < slot.size(); j++)
				{
					T elem = slot.get(j);
					if (elem != null)
					{
						int index = elem.Hash() % tableSize;
						if (newTable.get(index) == null)
						{
							LinkedList<T> obj = new LinkedList<T>();
							obj.add(elem);
							newTable.set(index, obj);
						}
						else
						{
							LinkedList<T> tableObj = newTable.get(index);
							tableObj.add(elem);
							if (tableObj.size() > maxSlotLength)
							{
								maxSlotLength = tableObj.size();
							}
						}
					}
				}
			}
		}
		table = newTable;
	}
	
	/**
	 * Helper function that determines whether to return "North" or "South".
	 * 
	 * @param a The substring that is either N or S
	 * @return Returns either South or North
	 */
	public String northOrSouth(String a)
	{
		if (a.equals("N"))
		{
			return "North";
		}
		else
		{
			return "South";
		}
	}
	
	/**
	 * Helper function that determines whether to return "West" or "East".
	 * 
	 * @param a The substring that is either W or E
	 * @return Returns either West or East
	 */
	public String westOrEast(String a)
	{
		if (a.equals("W"))
		{
			return "West";
		}
		else
		{
			return "East";
		}
	}
}