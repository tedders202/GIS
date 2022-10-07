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
 * Main class that handles the command prompt of the form...
 * 
 * java GIS <database file name> <command script file name> <log file name>
 * 
 * Class that does most of the writing in the log file.
 * 
 * @author Jae Won Shin
 *
 */
public class GIS {

	public static void main(String[] args) throws IOException {
		if (args.length != 3)
		{
			System.out.println("Invalid amount of files");
		}
		else
		{
			int amountOfCommands = 0;
			String dataBaseName = args[0];
			String scriptName = args[1];
			String logName = args[2];
			
			// The files given in the command prompt
			// Reason I made a FileWriter for dataBaseName is because the
			// writeBytes(String s) in RandomAccessFile created some extra lines.
			FileWriter dataWrite = new FileWriter(dataBaseName);
			RandomAccessFile database = new RandomAccessFile(dataBaseName, "r");
			RandomAccessFile script = new RandomAccessFile(scriptName, "r");
			dataWrite.write("FEATURE_ID|FEATURE_NAME|FEATURE_CLASS|STATE_ALPHA|"
					+ "STATE_NUMERIC|COUNTY_NAME|COUNTY_NUMERIC|PRIMARY_LAT_DMS|"
					+ "PRIM_LONG_DMS|PRIM_LAT_DEC|PRIM_LONG_DEC|SOURCE_LAT_DMS|"
					+ "SOURCE_LONG_DMS|SOURCE_LAT_DEC|SOURCE_LONG_DEC|ELEV_IN_M|"
					+ "ELEV_IN_FT|MAP_NAME|DATE_CREATED|DATE_EDITED\n");
			FileWriter log = new FileWriter(logName);
			
			//Initializes BufferPool
			BufferPool pool = new BufferPool();
			
			// Initializes HashTable
			HashTable<nameEntry> table = new HashTable<nameEntry>(null, null);
			String scptLine = script.readLine();
			
			// Finds the first command that builds the world
			long xMin = 0; long xMax = 0;
			long yMin = 0; long yMax = 0;
			
			// Creates the Worker object that has many functions designed to make
			// this class look smaller
			Worker work = new Worker();
			
			// Seeks out the first command
			while (!(scptLine.startsWith("world")))
			{
				log.write(scptLine + "\n");
				scptLine = script.readLine();
			}
			log.write(scptLine + "\n");
			
			// Grabs the specified boundaries
			Scanner scanner = new Scanner(scptLine);
			scanner.useDelimiter("\\t");
			scanner.next();
			
			// The longitudes in the form: 1130000W
			String wLong = scanner.next(); String eLong = scanner.next();
			// The latitudes in the form: 300000S
			String sLat = scanner.next(); String nLat = scanner.next();
			
			// The x-axis (longitudes)
			xMin = work.convertToSec(wLong); xMax = work.convertToSec(eLong);
			// The y-axis (latitudes)
			yMin = work.convertToSec(sLat); yMax = work.convertToSec(nLat);
			scanner.close();
			
			// Writes the GIS program's database name, script name, log name,
			// start time, and quadtree children print format into the log.txt
			work.writeGISProgram(log, dataBaseName, scriptName, logName,
					xMin, xMax, yMin, yMax);
			
			// Initializes QuadTree with the provided bounds
			prQuadTree<Point> tree = new prQuadTree<Point>(xMin, xMax, yMin, yMax);
			
			// Parses through the script file
			scptLine = script.readLine();
			while (scptLine != null)
			{
				// If comment just print onto log
				if (scptLine.startsWith(";"))
				{
					log.write(scptLine + "\n");
				}
				// If it is an import command
				if (scptLine.startsWith("import"))
				{
					amountOfCommands++;
					Scanner scanner2 = new Scanner(scptLine);
					scanner2.useDelimiter("\\t");
					scanner2.next();
					
					// Gets the data file name (ex. VA_Monterey.txt)
					String dataName = scanner2.next();
					log.write("Command " + amountOfCommands + ": " + scptLine + "\n");
					
					// Creates a data file
					RandomAccessFile data = new RandomAccessFile(dataName, "r");
					String dataLine = data.readLine();
					dataLine = data.readLine();
					
					int numOfFeat = 0; int numOfLoc = 0;
					String fName; long offset = 0;
					// Parses through the data file given in script
					while (dataLine != null)
					{
						// If within bounds then imports to database
						if (xMin <= work.returnX(dataLine) && work.returnX(dataLine) <= xMax
							&& yMin <= work.returnY(dataLine) && work.returnY(dataLine)
							<= yMax)
						{
							dataWrite.write(dataLine + "\n");
							numOfFeat++;
							numOfLoc++;
						}
						dataLine = data.readLine();
					}
					// Before reading database, MUST FLUSH
					dataWrite.flush();
					
					// Skips the format description in the database file
					String dbLine = database.readLine();
					offset = database.getFilePointer();
					
					// Starts at first record and begins to start building the
					// tree and hash table
					dbLine = database.readLine();
					while (dbLine != null)
					{
						fName = work.getName(dbLine);
						nameEntry n = new nameEntry(fName, offset);
						
						// Gets the longitude and latitude of the record in long
						long x = work.returnX(dbLine);
						long y = work.returnY(dbLine);
						Point p = new Point(x, y, offset);
						nameEntry existingName = table.find(n);
						Point existingPoint = tree.find(p);
						
						// Checks if it is already in the tree or table.
						// If so, then just adds the offset into the corresponding
						// data object
						if (existingName != null)
						{
							existingName.addLocation(offset);
						}
						else
						{
							table.insert(n);
						}
						if (existingPoint != null)
						{
							existingPoint.addLocation(offset);
						}
						else
						{
							tree.insert(p);
						}
						offset = database.getFilePointer();
						dbLine = database.readLine();
					}
					work.writeImport(log, numOfFeat, numOfLoc);
					scanner2.close();
					data.close();
				}
				// If it is a debug command
				else if (scptLine.startsWith("debug"))
				{
					amountOfCommands++;
					log.write("Command " + amountOfCommands + ": " + scptLine + "\n");
					log.write("\n");
					// Displays the table
					if (scptLine.equals("debug\thash"))
					{
						table.display(log);
					}
					// Displays the tree
					else if (scptLine.equals("debug\tquad"))
					{
						work.printTree(log, tree);
					}
					// Displays the buffer pool
					else if (scptLine.equals("debug\tpool"))
					{
						pool.display(log);
					}
					// else if (scptLine.equals("debug\tworld")  Not required
					log.write("-------------------------------------------------------------"
							+ "-------------------\n");
				}
				// Search by given coordinate
				else if (scptLine.startsWith("what_is_at"))
				{
					amountOfCommands++;
					log.write("Command " + amountOfCommands + ": " + scptLine + "\n");
					log.write("\n");
					Scanner scanner2 = new Scanner(scptLine);
					scanner2.useDelimiter("\\t");
					scanner2.next();
					
					// Gets the latitude and longitude from command line
					String latitude = scanner2.next();
					String longitude = scanner2.next();
					long lat = work.convertToSec(latitude);
					long lng = work.convertToSec(longitude);
					Point p = new Point(lng, lat);
					
					// Searches for the point in the tree
					Point point = tree.find(p);
					scanner2.close();
					
					// If inside the tree
					if (point != null)
					{
						log.write("\tThe following features were found at "
								+ work.getDMS(longitude, latitude));
						work.what_is_at(point, pool, log, database);
					}
					// If not in tree
					else
					{
						log.write("\tNothing was found at " + work.getDMS(longitude, latitude));
					}
					log.write("-------------------------------------------------------------"
							+ "-------------------\n");
				}
				// Search by given coordinates and range
				else if (scptLine.startsWith("what_is_in"))
				{
					amountOfCommands++;
					log.write("Command " + amountOfCommands + ": " + scptLine + "\n");
					log.write("\n");
					Scanner scanner2 = new Scanner(scptLine);
					scanner2.useDelimiter("\\t");
					scanner2.next();
					
					// Gets the latitude and longitude from command line
					String latitude = scanner2.next();
					String longitude = scanner2.next();
					
					// Grabs the x and y increments
					long yInc = scanner2.nextLong();
					long xInc = scanner2.nextLong();
					
					// Converts into total seconds
					long lat = work.convertToSec(latitude);
					long lng = work.convertToSec(longitude);
					
					// Finds the x and y bounds for the search region
					long xsMin = lng - xInc; long xsMax = lng + xInc;
					long ysMin = lat - yInc; long ysMax = lat + yInc;
					ArrayList<Point> targets = tree.find(xsMin, xsMax, ysMin, ysMax);
					int features = 0;
					
					// Counts how many features were in the ArrayList of Points
					for (int i = 0; i < targets.size(); i++)
					{
						for (int j = 0; j < targets.get(i).locations().size(); j++)
						{
							features++;
						}
					}
					// Checks if the return ArrayList is empty
					if (targets.size() != 0)
					{
						log.write("\tThe following features " + features + " were found at "
								+ work.getDMSWithInc(longitude, latitude, xInc, yInc));
						work.what_is_in(targets, pool, log, database);
					}
					else
					{
						log.write("\tNothing was found in " + 
					work.getDMSWithInc(longitude, latitude, xInc, yInc));
					}
					scanner2.close();
					log.write("-------------------------------------------------------------"
							+ "-------------------\n");
				}
				// Search with name and state abbreviation
				else if (scptLine.startsWith("what_is"))
				{
					amountOfCommands++;
					log.write("Command " + amountOfCommands + ": " + scptLine + "\n");
					log.write("\n");
					Scanner scanner2 = new Scanner(scptLine);
					scanner2.useDelimiter("\\t");
					scanner2.next();
					String name = scanner2.next();
					String abb = scanner2.next();
					nameEntry obj = new nameEntry(name + "\t" + abb);
					
					// Searches the HashTable
					nameEntry target = table.find(obj);
					if (target != null)
					{
						work.what_is(target, pool, log, database);
					}
					// If not found in the table
					else
					{
						log.write("\tNo records match " + name + " and " + abb + "\n");
					}
					log.write("-------------------------------------------------------------"
							+ "-------------------\n");
					scanner2.close();
				}
				// If it is not the quit command, continue the loop
				if (!(scptLine.startsWith("quit")))
				{
					scptLine = script.readLine();
				}
				// Exits the loop thus stopping the program and writes that
				// the program is ending.
				else
				{
					amountOfCommands++;
					work.writeQuit(log, amountOfCommands, scptLine);
					scptLine = null;
				}
			}
			dataWrite.close();
			database.close();
			script.close();
			log.close();
		}
	}
}
