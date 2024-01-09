import java.io.FileWriter;
import java.io.IOException;
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
	
	private static final String COURSE_START = "<course>";
	private static final String COURSE_END = "</course>";
	
	private static final String COURSE_NAME_START = "<courseName>";
	private static final String COURSE_NAME_END = "</courseName>";
	
	private static final String ENTIRE_OUTPUT_START = "<entireOutput>";
	private static final String ENTIRE_OUTPUT_END = "</entireOutput>";
	
	private static final String PREREQ_START = "<prereq>";
	private static final String PREREQ_END = "</prereq>";
	
	private static final String OUTPUT_START = "<output>";
	private static final String OUTPUT_END = "</output>";
	
	private static final String COURSE_ID_START = "<courseID>";
	private static final String COURSE_ID_END = "</courseID>";
	
	private static final String PREREQ_COURSES_START = "<prereqCourses>";
	private static final String PREREQ_COURSES_END = "</prereqCourses>";
	
	private static final String NAME_AND_ID_START = "<nameAndID>";
	private static final String NAME_AND_ID_END = "</nameAndID>";
	
	private static final String PREREQ_COURSE_NAME_START = "<prereqCourseName>";
	private static final String PREREQ_COURSE_NAME_END = "</prereqCourseName>";
	
	private static final String NODE_ID_START = "<nodeID>";
	private static final String NODE_ID_END = "</nodeID>";
	
	private static final String indent1 = "\t";
	private static final String indent2 = indent1 + "\t";
	private static final String indent3 = indent2 + "\t";
	private static final String indent4 = indent3 + "\t";
	private static final String indent5 = indent4 + "\t";
	private static final String indent6 = indent5 + "\t";
	private static final String indent7 = indent6 + "\t";
	
	
	public void makeXMLFileSection(FileWriter fileWriter) throws IOException {
		
		
		fileWriter.write(indent1 + COURSE_START + "\n");
			fileWriter.write(indent2 + COURSE_NAME_START + courseName + COURSE_NAME_END + "\n");
			fileWriter.write(indent2 + ENTIRE_OUTPUT_START + this.entirePrereqStatement + ENTIRE_OUTPUT_END + "\n");
			fileWriter.write(indent2 + COURSE_ID_START + this.nodeID + COURSE_ID_END + "\n");
			
			for (int j = 0; j < outputList.size(); j++) {
				String currOutput = outputList.get(j);
				List<Course> currCourseList = prereqCourseList.get(j);
				
				fileWriter.write(indent3 + PREREQ_START + "\n");
					fileWriter.write(indent4 + OUTPUT_START + currOutput  + OUTPUT_END + "\n");
					for (int i = 0; i < currCourseList.size(); i++) {
						Course currCourse = currCourseList.get(i);
						
					fileWriter.write(indent4 + PREREQ_COURSES_START + "\n");
						fileWriter.write(indent5 + NAME_AND_ID_START + "\n");
							fileWriter.write(indent6 + PREREQ_COURSE_NAME_START + currCourse.getCourseName() + PREREQ_COURSE_NAME_END + "\n");
							fileWriter.write(indent6 + NODE_ID_START + currCourse.getNodeID() + NODE_ID_END + "\n");
						fileWriter.write(indent5 + NAME_AND_ID_END + "\n");
					fileWriter.write(indent4 + PREREQ_COURSES_END + "\n");	
					}
				fileWriter.write(indent3 + PREREQ_END + "\n");
			}
		fileWriter.write(indent1 + COURSE_END + "\n");
	}
	
	private void helperFileWriter(FileWriter fileWriter, String start, String content, String end) throws IOException {
		fileWriter.write(start + content + end + "\n");
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
