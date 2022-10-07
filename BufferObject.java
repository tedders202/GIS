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
//interfere with the normal operation of the grading code.
//
//<Jae Won Shin>
//<jaewons 9061-34703>
/**
 * The object that will be used in the BufferPool implementation.
 * @author Jae Won Shin
 *
 */
public class BufferObject {
	
	private long offset;
	private String record;
	
	/**
	 * Constructor without parameter (will not be used in most cases)
	 */
	public BufferObject()
	{
		offset = 0;
	}
	
	/**
	 * Constructor with parameters (will be used most of the time)
	 * @param off Given offset
	 * @param r Given GIS record
	 */
	public BufferObject(long off, String r)
	{
		offset = off;
		record = r;
	}
	
	/**
	 * Gets the offset of the GIS record line
	 * @return long offset
	 */
	public long getOffset()
	{
		return offset;
	}
	
	/**
	 * Gets the record of the buffer object
	 * @return String GIS record line
	 */
	public String getRecord()
	{
		return record;
	}
	
	/**
	 * Sets the offset of the data object
	 * @param off given offset of GIS record
	 */
	public void setOffset(long off)
	{
		offset = off;
	}
	
	/**
	 * Sets the record of the data object
	 * @param r given GIS record
	 */
	public void setRecord(String r)
	{
		record = r;
	}
	
	/**
	 * Compares the two objects.
	 * Returns true iff the GIS records match
	 */
	public boolean equals(Object other)
	{
		if (other == null)
		{
			return false;
		}
		if (this == other)
		{
			return true;
		}
		if (this.getClass() == other.getClass())
		{
			BufferObject obj = (BufferObject) other;
			// If the string records are matching
			if (this.getRecord().equals(obj.getRecord()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a to string version of the BufferObject
	 */
	public String toString()
	{
		return String.valueOf(offset) + ":  " + record;
	}
}
