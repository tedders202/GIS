import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

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
 * Mainly used to try and make the GIS class look smaller.
 * 
 * @author Jae Won Shin
 *
 */
public class Worker {
	
	// Only used for printTree and printTreeHelper
	String pad;
	
	prQuadTree<Point> localPR;
	prQuadInternal Internal;
	prQuadLeaf<Point>    Leaf;
	
	public Worker()
	{
		pad = new String("---");
		// for type-checking in recursive descents
		localPR = new prQuadTree<Point>(0, 0, 0, 0);
		Internal = new prQuadInternal();
		Leaf     = new prQuadLeaf<Point>();
	}
	
	/**
	 * Converts given coordinates into total seconds.
	 * Ex. 
	 * 		1130000W -> -406800
	 * 		300000S  -> -108000
	 *  	1130000E -> 406800
	 * 		300000N  -> 108000
	 * @param coor
	 * @return
	 */
	public long convertToSec(String coor)
	{
		long totalSec = 0; long d = 0;
		long m = 0; long s = 0;
		// Longitudes are longer so different substring amount
		// for longitude and latitude.
		if (coor.endsWith("W") || coor.endsWith("E"))
		{
			d = Integer.valueOf(coor.substring(0, 3));
			m = Integer.valueOf(coor.substring(3, 5));
			s = Integer.valueOf(coor.substring(5, 7));
			totalSec = d*3600 + m*60 + s;
		}
		else if (coor.endsWith("S") || coor.endsWith("N"))
		{
			d = Integer.valueOf(coor.substring(0, 2));
			m = Integer.valueOf(coor.substring(2, 4));
			s = Integer.valueOf(coor.substring(4, 6));
			totalSec = d*3600 + m*60 + s;
		}
		// Checks if values should be negative or positive
		if (coor.endsWith("W") || coor.endsWith("S"))
		{
			return -totalSec;
		}
		else
		{
			return totalSec;
		}
	}
	
	/**
	 * Prints the tree for the debug quad command.
	 * 
	 * @param Out Where the output will be written
	 * @param Tree The tree it will be printing
	 */
	public void printTree(FileWriter Out, prQuadTree<Point> Tree) {
    	try {
           if ( Tree.root == null )
              Out.write("  Empty tree.\n" );
           else
              printTreeHelper(Out,  Tree.root, "");
    	}
    	catch ( IOException e ) {
    		return;
    	}
     }

	/**
	 * Helper function to the printTree function.
	 * @param Out Where the output will be written
	 * @param sRoot The node it will be printing
	 * @param Padding The spacing
	 */
	@SuppressWarnings("rawtypes")
	public void printTreeHelper(FileWriter Out, prQuadNode sRoot, String Padding) {

		try {
			// Check for empty leaf
			if ( sRoot == null ) {
				Out.write(" " + Padding + "*\n");
				return;
			}
			// Check for and process SW and SE subtrees
			if ( sRoot.getClass().equals(Internal.getClass()) ) {
				prQuadInternal p = (prQuadInternal) sRoot;
				printTreeHelper(Out, p.SW, Padding + pad);
				printTreeHelper(Out, p.SE, Padding + pad);
			}
			// Display indentation padding for current node
			//Out.write(Padding);

			// Determine if at leaf or internal and display accordingly
			if ( sRoot.getClass().equals(Leaf.getClass()) ) {
				prQuadLeaf p = (prQuadLeaf) sRoot;
				for (int pos = 0; pos < p.Elements.size(); pos++) {
					Out.write(" " + Padding + p.Elements.get(pos) + "\n" );
				}
			}
			else if ( sRoot.getClass().equals(Internal.getClass()) )
				Out.write(" " + Padding + "@\n" );
			else
				Out.write(" " + sRoot.getClass().getName() + "#\n");

			// Check for and process NE and NW subtrees
			if ( sRoot.getClass().equals(Internal.getClass()) ) {
				prQuadInternal p = (prQuadInternal) sRoot;
				printTreeHelper(Out, p.NE, Padding + pad);
				printTreeHelper(Out, p.NW, Padding + pad);
			}
		}
		catch ( IOException e ) {
			return;
		}
	}
	
	/**
	 * Writes the program details in this format...
	 * 
	 * GIS Program
	 * dbFile:     db.txt
	 * script:     Script08.txt
	 * log:        refLog08.txt
	 * Start time: Sat Mar 20 21:07:26 EDT 2021
	 * Quadtree children are printed in the order SW  SE  NE  NW
	 * 
	 * @param fw the reference log file
	 * @param db the database name
	 * @param script the script name
	 * @param log the log name
	 * @throws IOException
	 */
	public void writeGISProgram(FileWriter fw, String db, String script, String log,
			long xMin, long xMax, long yMin, long yMax) throws IOException
	{
		long millis = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(millis);
		fw.write("\n");
		fw.write("GIS Program\n");
		fw.write("\n");
		fw.write("dbFile:\t" + db + "\n");
		fw.write("script:\t" + script + "\n");
		fw.write("log:\t" + log + "\n");
		fw.write("Start time: " + date + "\n");
		fw.write("Quadtree children are printed in the order SW SE NE NW \n");
		fw.write("-------------------------------------------------------------"
				+ "-------------------\n");
		fw.write("\n");
		fw.write("Latitude/longitude values in index entries are"
				+ " shown as signed integers, in total seconds.\n");
		fw.write("\n");
		fw.write("World boundaries are set to:\n");
		fw.write("              " + yMax + "\n");
		fw.write("   " + xMin + "                " + xMax + "\n");
		fw.write("              " + yMin + "\n");
		fw.write("-------------------------------------------------------------"
				+ "-------------------\n");
	}
	
	/**
	 * Returns the longitude of a given record
	 * @param record GIS record
	 * @return
	 */
	public long returnX(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		for (int i = 0; i < 8; i++)
		{
			scanner.next();
		}
		String longitude = scanner.next();
		long lng = 0;
		if (!(longitude.equals("Unknown")))
		{
			lng = convertToSec(longitude);
		}
		scanner.close();
		return lng;
	}
	
	/**
	 * Returns the latitude of a given record
	 * @param record GIS record
	 * @return
	 */
	public long returnY(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		for (int i = 0; i < 7; i++)
		{
			scanner.next();
		}
		String latitude = scanner.next();
		long lat = 0;
		if (!(latitude.equals("Unknown")))
		{
			lat = convertToSec(latitude);
		}
		scanner.close();
		return lat;
	}
	
	/**
	 * Writes how many records have been successfully indexed.
	 * 
	 * @param fw log file
	 * @param numOfFeat number of features 
	 * @param numOfLoc
	 * @param aveNameLen
	 * @throws IOException
	 */
	public void writeImport(FileWriter fw, int numOfFeat, int numOfLoc) throws IOException
	{
		fw.write("\n");
		fw.write("Imported Features by name: " + numOfFeat + "\n");
		fw.write("Imported Locations:        " + numOfLoc + "\n");
		//fw.write("Average name length: ");		Not required
		fw.write("-------------------------------------------------------------"
				+ "-------------------\n");
	}

	/**
	 * Takes the longitude and latitude, then returns them in DMS format
	 * Ex.
	 * 		382812N	0793156W -> (79d 31m 56s West, 38d 28m 12s North)
	 * @param lng
	 * @param lat
	 * @return
	 */
	public String getDMS(String lng, String lat)
	{
		int d = Integer.valueOf(lng.substring(0, 3));
		int m = Integer.valueOf(lng.substring(3, 5));
		int s = Integer.valueOf(lng.substring(5, 7));
		int d2 = Integer.valueOf(lat.substring(0, 2));
		int m2 = Integer.valueOf(lat.substring(2, 4));
		int s2 = Integer.valueOf(lat.substring(4, 6));
		
		return "(" + d + "d " + m + "m " + s + "s " + 
		westOrEast(lng.substring(7)) + ", " + d2 + "d "
		+ m2 + "m " + s2 + "s " + 
		northOrSouth(lat.substring(6)) + ")" + "\n";
	}
	
	/**
	 * Takes the longitude and latitude, then returns them in DMS format
	 * Ex.
	 * 		382812N	0793156W -> (79d 31m 56s West, 38d 28m 12s North)
	 * @param lng
	 * @param lat
	 * @return
	 */
	public String getDMSWithInc(String lng, String lat, long xInc, long yInc)
	{
		int d = Integer.valueOf(lng.substring(0, 3));
		int m = Integer.valueOf(lng.substring(3, 5));
		int s = Integer.valueOf(lng.substring(5, 7));
		int d2 = Integer.valueOf(lat.substring(0, 2));
		int m2 = Integer.valueOf(lat.substring(2, 4));
		int s2 = Integer.valueOf(lat.substring(4, 6));
		
		return "(" + d + "d " + m + "m " + s + "s " + 
		westOrEast(lng.substring(7)) + " +/- " + xInc + ", " + d2 + "d "
		+ m2 + "m " + s2 + "s " + 
		northOrSouth(lat.substring(6)) + " +/- " + yInc + ")" + "\n";
	}
	
	/**
	 * 	Returns a concatenated string of the form...
	 * 	Blacksburg	VA
	 * 
	 * @param record The GIS record line
	 * @return
	 */
	public String getName(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		scanner.nextInt();
		String name = scanner.next();
		scanner.next();
		String state = scanner.next();
		scanner.close();
		return name + "\t" + state;
	}
	
	/**
	 * Returns the feature name, county name, and state abbreviation for the
	 * what_is_at command.
	 * Ex.
	 * 		100000|Blacksburg|...|VA|...|Montgomery|... -> Blacksburg VA
	 * 
	 * @param record GIS record
	 * @return
	 */
	public String nameState(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		scanner.next();
		String name = scanner.next();
		scanner.next();
		String alph = scanner.next();
		scanner.close();
		return name + "\t" + alph;
	}
	
	/**
	 * Returns the feature name, county name, and state abbreviation for the
	 * what_is_at command.
	 * Ex.
	 * 		100000|Blacksburg|...|VA|...|Montgomery|... -> Blacksburg Montgomery VA
	 * 
	 * @param record GIS record
	 * @return
	 */
	public String nameCountyState(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		scanner.next();
		String name = scanner.next();
		scanner.next();
		String alph = scanner.next();
		scanner.next();
		String county = scanner.next();
		scanner.close();
		return name + "\t" + county + "\t" + alph;
	}
	
	/**
	 * Returns county name for the
	 * what_is command.
	 * Ex.
	 * 		100000|Blacksburg|...|VA|...|Montgomery|... -> Montgomery
	 * 
	 * @param record GIS record
	 * @return
	 */
	public String county(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		String county = scanner.next();
		scanner.close();
		return county;
	}
	
	/**
	 * Gets the DMS format from the record using the regular
	 * getDMS() and scanners.
	 * @param record GIS record.
	 * @return
	 */
	public String getDMSFromRecord(String record)
	{
		Scanner scanner = new Scanner(record);
		scanner.useDelimiter("\\|");
		for (int i = 0; i < 7; i++)
		{
			scanner.next();
		}
		String lat = scanner.next();
		String lng = scanner.next();
		scanner.close();
		return getDMS(lng, lat);
	}
	
	/**
	 * Writes the quit command
	 * @param fw log file
	 * @param amountOfCommands How many commands were done before the quit
	 * @param scptLine Script command line
	 * @throws IOException
	 */
	public void writeQuit(FileWriter fw, int amountOfCommands, String scptLine)
			throws IOException
	{
		long millis = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(millis);
		fw.write("Command " + amountOfCommands + ": " + scptLine + "\n");
		fw.write("\n");
		fw.write("Terminating execution of commands.\n");
		fw.write("End Time: " + date + "\n");
		fw.write("-------------------------------------------------------------"
				+ "-------------------\n");
	}
	
	/**
	 * Writes the correct output for what_is_at, handles the pool searches.
	 * Assumption: point is not null and it is inside the tree
	 * 
	 * @param point The point that we are searching for in the tree
	 * @param pool The BufferPool object that is initialized in GIS class
	 * @param fw The log file
	 * @param database The database file
	 * @throws IOException
	 */
	public void what_is_at(Point point, BufferPool pool, FileWriter fw,
			RandomAccessFile database) throws IOException
	{
		BufferObject target = null;
		// Iterate through all the available locations
		// Searches through the BufferPool first
		ArrayList<Long> loc = point.locations();
		for (int i = 0; i < loc.size(); i++)
		{
			target = pool.find(loc.get(i));
		}
		// If it was found in the BufferPool
		if (target != null)
		{
			String record = target.getRecord();
			fw.write("\t" + target.getOffset() + ":\t" +
					nameCountyState(record) + "\n");
		}
		// If it is not in the pool, then search through the disk
		// using fseek()
		else
		{
			for (int j = 0; j < loc.size(); j++)
			{
				database.seek(loc.get(j));
				String record = database.readLine();
				BufferObject targets = new BufferObject(loc.get(j), record);
				fw.write("\t" + loc.get(j) +
						":\t" + nameCountyState(record) + "\n");
				pool.insert(targets);
			}
		}
	}
	
	/**
	 * Writes the correct output for what_is_in, handles the pool searches.
	 * Assumption: targets is not null and it has at least one element.
	 * 
	 * @param targets An ArrayList of Points
	 * @param pool The BufferPool object that is initialized in GIS class
	 * @param fw The log file
	 * @param database The database file
	 * @throws IOException
	 */
	public void what_is_in(ArrayList<Point> targets, BufferPool pool, FileWriter fw,
			RandomAccessFile database) throws IOException
	{
		for (int i = 0; i < targets.size(); i++)
		{
			BufferObject target = null;
			// Iterate through all the available locations
			// Searches through the BufferPool first
			ArrayList<Long> loc = targets.get(i).locations();
			for (int j = 0; j < loc.size(); j++)
			{
				target = pool.find(loc.get(j));
				// If it was found in the BufferPool
				if (target != null)
				{
					String record = target.getRecord();
					fw.write("\t" + target.getOffset() + ":\t" +
							nameState(record) + "\t" + getDMSFromRecord(record));
				}
				// If it is not in the pool, then search through the disk
				// using fseek()
				else
				{
					database.seek(loc.get(j));
					String record = database.readLine();
					BufferObject buffers = new BufferObject(loc.get(j), record);
					fw.write("\t" + loc.get(j) +
							":\t" + nameState(record) + "\t" + getDMSFromRecord(record));
					pool.insert(buffers);
				}
			}
		}
	}
	
	/**
	 * Writes the correct output for what_is, handles the pool search.
	 * Assumption: target is not null and it is inside the HashTable.
	 * 
	 * @param target The nameEntry that we are searching for
	 * @param pool The BufferPool object that is initialized in GIS class
	 * @param fw The log file
	 * @param database the database file
	 * @throws IOException
	 */
	public void what_is(nameEntry target, BufferPool pool, FileWriter fw,
			RandomAccessFile database) throws IOException
	{
		BufferObject buffer = null;
		// Iterate through all the available locations
		// Searches through the BufferPool first
		ArrayList<Long> loc = target.locations();
		for (int i = 0; i < loc.size(); i++)
		{
			buffer = pool.find(loc.get(i));
		}
		// If it was found in the BufferPool
		if (buffer != null)
		{
			String record = buffer.getRecord();
			fw.write("\t" + buffer.getOffset() + ":\t"
					+ county(record) + "\t" +
					getDMSFromRecord(record));
		}
		// If it is not in the pool, then search through the disk
		// using fseek()
		else
		{
			for (int j = 0; j < loc.size(); j++)
			{
				database.seek(loc.get(j));
				String record = database.readLine();
				BufferObject buffers = new BufferObject(loc.get(j), record);
				fw.write("\t" + loc.get(j) +
						":\t" + county(record) + "\t" +
						getDMSFromRecord(record));
				pool.insert(buffers);
			}
		}
	}
	
	/**
	 * Helper function that determines whether to return "North" or "South".
	 * 
	 * @param a The substring that is either N or S
	 * @return Returns either South or North
	 */
	private String northOrSouth(String a)
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
	private String westOrEast(String a)
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
