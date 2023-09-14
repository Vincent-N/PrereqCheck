import java.io.FileWriter;
import java.util.List;

public class Course implements Comparable<Course> {
	
	private final static String DEFAULT_STATEMENT = "No prerequisite statement put in yet.";
	private String courseName;
	private String entirePrereqStatement;
	private List<String> outputList; // parallel with prereqCourseList
	private List<List<Course>> prereqCourseList; // each inside list holds the prereq courses for one output in outputList	
	private int nodeID;
	private boolean valuesFilled;
	
	/**
	 * Create Course object when only the name of the course is known.
	 * This situation occurs whenever we see that another course has this
	 * course as a prerequisite, but this course hasn't been created yet.
	 * @param courseName
	 */
	public Course(String courseName) {
		this.courseName = courseName;
		entirePrereqStatement = DEFAULT_STATEMENT;
	}
	
	public void printOutValues() {
		System.out.println(courseName);
		if (valuesFilled) {
			System.out.println(entirePrereqStatement);
			for (int i = 0; i < prereqCourseList.size(); i++) {
				System.out.println(outputList.get(i));
				System.out.println(prereqCourseList.get(i));
			}
		}
		
		System.out.println(nodeID);
		System.out.println();
	}
	
	/**
	 * Create Course object when all variables (except for nodeID) is known (since nodeID can only be determined once all Course objects are made and sorted)
	 * @param courseName
	 * @param entirePrereqStatement
	 * @param output
	 * @param listOfPrereqCourses
	 */
	public Course(String courseName, String entirePrereqStatement, String output, List<Course> listOfPrereqCourses) {
		// figure out the constructor, thinking that we can do something like store all the course in a hashmap (course name to course object)
		// and when filling out listOfPrereqcourses, we check if the 
		// course is already in the hashmap and if so, we just add the corresponding course object
		// if not, we create a new object, put in hashmap, and then add the corresponding course object
		// (handle this work of making the listOfPrereqCourses outside of the class, wherever the hashmap is)
	}
	
	public void setRestOfValues(String entirePrereqStatement, List<String> outputList, List<List<Course>> prereqCourseList) {
		this.entirePrereqStatement = entirePrereqStatement;
		// shallow copy because I will make a new array for each case and won't reuse anything.
		this.outputList = outputList;
		this.prereqCourseList = prereqCourseList;
		valuesFilled = true;
	}
	
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID; 
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public List<String> getOutput() {
		return outputList;
	}
	
	public List<List<Course>> getListOfPrereqCourses() {
		return prereqCourseList;
	}
	
	public int getNodeID() {
		return nodeID;
	}
	
	public void makeXMLFileSection(FileWriter fileWriter) {
		
	}
	
	public String toString() {
		return courseName;
	}
	
	/**
	 * Prints out course name, its prerequisite statement, and then prints
	 * out all its prerequisite course names and their prerequisite statements (through a recursive call).
	 * 
	 * Mainly this is used for testing
	 * @param indent is the amount of space we indent the print by. used so that we can tell apart a course and its prerequisites
	 */
	public void printOutCourseAndPrerequisites(String indent) {
		System.out.println(indent + courseName + " - " + entirePrereqStatement);
		indent += "\t";

		if (valuesFilled) {
			for (int i = 0; i < outputList.size(); i++) {
				String currOutput = outputList.get(i);
					List<Course> currPrereqCourses = prereqCourseList.get(i);
					System.out.println(indent + (i + 1) + ". " + currOutput);
					for (int j = 0; j < currPrereqCourses.size(); j++) {
						Course currCourse = currPrereqCourses.get(j);
						currCourse.printOutCourseAndPrerequisites(indent);
					}
			}
		}
	}

	/**
	 * Sort by alphabetical order of course name
	 */
	public int compareTo(Course other) {
		return courseName.compareTo(other.courseName);
	}
	
}
