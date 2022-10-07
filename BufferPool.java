import java.io.FileWriter;
import java.io.IOException;
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
//interfere with the normal operation of the grading code.
//
//<Jae Won Shin>
//<jaewons 9061-34703>
/**
 * Note: This Buffer Pool is specifically designed to work with the
 * 		 BufferObject class.
 * 		 Reason for this choice is because it made it easier to use
 * 		 getter functions like getOffset().
 * @author Jae Won Shin
 *
 */
public class BufferPool {
	
	public ArrayList<BufferObject> buffpool;
	// Maximum amount of buffer objects allowed in the pool
	public int buffPoolMax = 15;
	// Amount of objects in the pool
	// Note: Reason for buffSize is because since we are
	//		 adding the ArrayList with null when BufferPool
	//		 is first initialized, the ArrayList.size() will
	//		 not return 0.
	private int buffSize;
	
	public BufferPool()
	{
		buffpool = new ArrayList<BufferObject>(buffPoolMax);
		buffSize = 0;
		// Adding null into the BufferPool so that we can access
		// the indices if needed.
		for (int i = 0; i < buffPoolMax; i++)
		{
			buffpool.add(null);
		}
	}
	
	/**
	 * MAINLY USED FOR TESTING
	 * Constructor with specified buffer pool size
	 * @param size
	 */
	public BufferPool(int size)
	{
		buffPoolMax = size;
		buffSize = 0;
		buffpool = new ArrayList<BufferObject>(buffPoolMax);
		for (int i = 0; i < buffPoolMax; i++)
		{
			buffpool.add(null);
		}
	}
	
	/**
	 * Returns the size of the BufferPool
	 * @return
	 */
	public int getSize()
	{
		return buffSize;
	}
	
	/**
	 * Returns true/false if the buffer pool is full or not
	 * @return
	 */
	public boolean isFull()
	{
		if (buffSize == buffPoolMax)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Inserts buffer pool object at the beginning.
	 * It first removes the LRU object (which is at the last index),
	 * then inserts the specified object to the front (the first index). 
	 * Note: We would only insert if it is not in the buffer pool.
	 * 		 Therefore, we do not need to search the pool for the
	 * 		 object before inserting.
	 * @return If it was successfully added.
	 */
	public boolean insert(BufferObject b)
	{
		if (b == null)
		{
			return false;
		}
		buffpool.remove(buffPoolMax - 1);
		buffpool.add(0, b);
		if (!(isFull()))
		{
			buffSize++;
		}
		return true;
	}
	
	/**
	 * Searches for the record in the buffer pool based on offset
	 * If it finds the buffer object, it moves the object to the front
	 * of the ArrayList.
	 * @param off given offset
	 * @return returns the BufferObject associated with the offset
	 */
	public BufferObject find(long off)
	{
		for (int i = 0; i < buffSize; i++)
		{
			if (buffpool.get(i).getOffset() == off)
			{
				BufferObject target = buffpool.get(i);
				buffpool.remove(i);
				buffpool.add(0, target);
				return target;
			}
		}
		return null;
	}
	
	/**
	 * Displays the contents in buffer pool
	 * Ex.
	 * 		MRU
	 * 			1000:  1000000|Blacksburg|......
	 * 			2000:  2000000|Christiansburg|......
	 * 		LRU
	 * 
	 * @param fw
	 * @throws IOException
	 */
	public void display(FileWriter fw) throws IOException
	{
		fw.write("MRU\n");
		for (int i = 0; i < getSize(); i++)
		{
			fw.write("\t" + buffpool.get(i).toString() + "\n");
		}
		fw.write("LRU\n");
	}
}
