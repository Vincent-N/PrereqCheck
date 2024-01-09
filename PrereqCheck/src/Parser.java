import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;

public class Parser {

	private Set<String> tempFieldOfStudySet;
	private List<StatementGroup> listOfStatements;
	private Map<String, Course> courseMap;
	
	private static final String NUMBER_REGULAR_EXPR = "([0-9][0-9][0-9][A-Za-z]?)";
	private static final String GRADE_REGULAR_EXPR = "(([B-Db-d][+-]?)|([Aa][-]?)|F)";
	private static final String END_PUNCTUATION = "[.,:;]?";
	
	private static final char VAR_INDICATOR = '=';
	private static final String FIELD_INDICATOR = VAR_INDICATOR + "\\[Field\\]";
	private static final String NUMBER_INDICATOR = VAR_INDICATOR + "\\[Number\\]";
	private static final String GRADE_INDICATOR = VAR_INDICATOR + "\\[Grade\\]";
	private static final String FIELD_INDICATOR_W_PUNC = FIELD_INDICATOR + END_PUNCTUATION;
	private static final String NUMBER_INDICATOR_W_PUNC = NUMBER_INDICATOR + END_PUNCTUATION;
	private static final String GRADE_INDICATOR_W_PUNC = GRADE_INDICATOR + END_PUNCTUATION;
	
	private static final String FIELD_OF_STUDY_XML_FILENAME = "smallFieldsOfStudy.xml";
	private static final String STATEMENT_TO_OUTPUT_FILENAME = "statement_to_output.xml";
	private static final String COURSENAME_TO_PREREQ_INPUT_FILENAME = "sample_input_file.xml";
	
	private static final String SENTINEL_VALUE = "MATCHES!"; // Used to differentiate parsingOutputList contents in the case it matches and the general statement has no variable indicators and in the case the statement does not match
	
	private static final String CREATED_XML_FILE_NAME = "test15.xml";
	private static final String ALL_COURSES_START = "<allCourses>";
	private static final String ALL_COURSES_END = "</allCourses>";
	
	public Parser() {
		tempFieldOfStudySet = new HashSet<>();
		listOfStatements = new ArrayList<>();
		courseMap = new HashMap<>(); // using hashmap because lot of accessing when we are creating courses
		initializeFieldOfStudySet();
		initializeListOfStatements();
		findMatchesForInput();
		createXMLFile();
	}
	
	private void createXMLFile() {
		// sort courses
		List<Course> courseList = new ArrayList<>();
		for (String currCourseName : courseMap.keySet()) {
			courseList.add(courseMap.get(currCourseName));
		}
		Collections.sort(courseList); // done by alphabetical order of courseName (see compareTo in Course object)
		
		// set the node id for all courses based on alphabetical order
		for (int i = 0; i < courseList.size(); i++) {
			courseList.get(i).setNodeID(i);
		}
		
		try {
			File createdFile = new File(CREATED_XML_FILE_NAME);
			if (createdFile.createNewFile()) {
				
				FileWriter fileWriter = new FileWriter(CREATED_XML_FILE_NAME);
				fileWriter.write("<?xml version=\"1.0\"?>" + "\n");
				fileWriter.write(ALL_COURSES_START + "\n");
				
				for (int i = 0; i < courseList.size(); i++) {
					courseList.get(i).makeXMLFileSection(fileWriter);
				}

				fileWriter.write(ALL_COURSES_END + "\n");
				fileWriter.close();
				
				System.out.println("Wrote to file");
			} else {
				System.out.println("new file not created.");
			}
		} catch (IOException e) {
			System.out.println("Error occured when creating file");
		}
		
	}
	
	/**
	 * Goes through XML file names in COURSENAME_TO_PREREQ_INPUT_FILENAME (which contains a
	 * mapping from course name to its prerequisite) and looks for a match
	 * for all of them. If a course's prereq statement matches a general statement,
	 * a filled in course object is created and stored in the courseMap.
	 */
	private void findMatchesForInput() {
		try {
			File inputXMLFile = new File(COURSENAME_TO_PREREQ_INPUT_FILENAME);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document inputDoc = builder.parse(inputXMLFile);
			NodeList courseNodes = inputDoc.getElementsByTagName("course");
			for (int i = 0; i < courseNodes.getLength(); i++) {
				Node currCourseNode = courseNodes.item(i);
				if (currCourseNode.getNodeType() == Node.ELEMENT_NODE) {
					Element currCourseElement = (Element) currCourseNode;
					String currName = currCourseElement.getElementsByTagName("courseName").item(0).getTextContent();
					String prereqStatement = currCourseElement.getElementsByTagName("prereqStatement").item(0).getTextContent();
//					System.out.println(currName);
					List<Object> parsingOutputListAndMatchedGroup = findMatchFromAllGeneral(prereqStatement);
					// TODO: i think instead of making intermediate xml file, just make the course objects from here?
					createCourseObject(currName, prereqStatement, (List<String>) parsingOutputListAndMatchedGroup.get(0), (StatementGroup) parsingOutputListAndMatchedGroup.get(1));
				}
			}
//			System.out.println(courseMap);
//			System.out.println();
//			for (String courseName : courseMap.keySet()) {
//				courseMap.get(courseName).printOutValues();
//			}
//			courseMap.get("Computer Science 311").printOutCourseAndPrerequisites("");
//			courseMap.get("Mathematics 408C").printOutCourseAndPrerequisites("");
		} catch (Exception e) {
			System.out.println("Problem with reading XML file in findMatchesForInput(). This not working");
		}
	}
	
	// https://initialcommit.com/blog/how-to-read-xml-file-in-java used to help read through xml file
	/**
	 * Goes through XML file named in FIELD_OF_STUDY_XML_FILENAME and adds all
	 * fields in file to the tempFieldOfStudySet
	 */
	private void initializeFieldOfStudySet() {
		try {
			File fosXMLFile = new File(FIELD_OF_STUDY_XML_FILENAME);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document fosDoc = builder.parse(fosXMLFile);
			NodeList fosNodes = fosDoc.getElementsByTagName("field");
			for (int i = 0; i < fosNodes.getLength(); i++) {
				Node currFieldNode = fosNodes.item(i);
				if (currFieldNode.getNodeType() == Node.ELEMENT_NODE) {
					Element currFieldElement = (Element) currFieldNode;
					String currFieldName = currFieldElement.getTextContent();
					tempFieldOfStudySet.add(currFieldName);
				}
			}
		} catch (Exception e) {
			System.out.println("Problem with reading XML file. This not working");
		}
	}
	
	/**
	 * Goes through XML file named in STATEMENT_TO_OUTPUT_FILENAME and adds all
	 * mappings to listOfStatements.
	 */
	private void initializeListOfStatements() {
		try {
			File statementXMLFile = new File(STATEMENT_TO_OUTPUT_FILENAME);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document statementDoc = builder.parse(statementXMLFile);
			NodeList statementNodes = statementDoc.getElementsByTagName("mapping"); // List of all the "mapping" in xml file
			for (int i = 0; i < statementNodes.getLength(); i++) {
				Node currMappingNode = statementNodes.item(i); // get one of the "mapping"
				if (currMappingNode.getNodeType() == Node.ELEMENT_NODE) {
					Element currMappingElement = (Element) currMappingNode; // convert mapping to element
					String generalStatement = currMappingElement.getElementsByTagName("generalStatement").item(0).getTextContent();
//					System.out.println(prerequisiteOutputs);
//					String output = currMappingElement.getElementsByTagName("output").item(0).getTextContent();
					listOfStatements.add(new StatementGroup(generalStatement, currMappingElement));
				}
			}
		} catch (Exception e) {
			System.out.println("Problem with reading XML file. This not working");
		}
	}
	
	/**
	 * Creates a Course object for the given course and any course that is a prerequisite to the given course.
	 * For the given course, the general prerequisite output (in matchedGroup's list of prerequisiteData) is filled in using the parsingOutputList.
	 * The course names are also filled in. 
	 * @param courseName
	 * @param prereqStatement
	 * @param parsingOutputList
	 * @param matchedGroup
	 */
	private void createCourseObject(String courseName, String prereqStatement, List<String> parsingOutputList, StatementGroup matchedGroup) {
		if (parsingOutputList == null) {
			System.out.println("Match wasn't found. Error");
			return;
		}
		
		Course currCourse = courseMap.getOrDefault(courseName, new Course(courseName)); // gets existing course object if it exists. otherwise, make a new one
		courseMap.put(courseName, currCourse); // put in if it wasn't there already
		
		List<String> outputList = new ArrayList<>();
		List<List<Course>> prereqCourseList = new ArrayList<>();
		
		// go through all prereqs and set up parallel array
		List<PrerequisiteData> prereqDataList = matchedGroup.getPrerequisites();
		for (int i = 0; i < prereqDataList.size(); i++) {
			PrerequisiteData currPrereqData = prereqDataList.get(i);
			
			List<String> generalCourseNames = currPrereqData.getNamesOfPrerequsiteCourses();
			List<Course> prereqCourseListForOnePrereq = new ArrayList<>();
			
			/* adds all the courses for one prerequisite into an list */
			for (int j = 0; j < generalCourseNames.size(); j++) {
				String currPrereqCourseName = fillInIndicators(generalCourseNames.get(j), parsingOutputList, matchedGroup.generalStatement);
				Course currPrereqCourse = courseMap.getOrDefault(currPrereqCourseName, new Course(currPrereqCourseName));
				courseMap.put(currPrereqCourseName, currPrereqCourse); // put in if it wasn't there already
				prereqCourseListForOnePrereq.add(currPrereqCourse);
			}
			
			String outputFilledIn = fillInIndicators(currPrereqData.getOutput(), parsingOutputList, matchedGroup.generalStatement);
			// parallel arrays. output matches with all the courses stated in the output
			outputList.add(outputFilledIn);
			prereqCourseList.add(prereqCourseListForOnePrereq);
		}
		
		currCourse.setRestOfValues(prereqStatement, outputList, prereqCourseList);
	}
	
	/**
	 * Given a string (this should be the specific prerequisite statement), it will look through
	 * all the general statements (which were provided in STATEMENT_TO_OUTPUT_FILENAME file) to
	 * find a match.
	 * 
	 * 
	 * @param givenString
	 * @return If a match is found, a list is returned where the 0th element is the parsingOutputList and 
	 * the 1th element is the StatementGroup object corresponding to the matched general statement.
	 * If no match is found, a list is still returned with both elements being null.
	 */
	public List<Object> findMatchFromAllGeneral(String givenString) {
		List<String> outputsWithIndicators = null; 
		List<String> parsingOutputList = null; /* ordered list thing */
		String genStatementForDebugMessage = null;
		StatementGroup matchedGroup = null;
		boolean matchFound = false;
		
		if (givenString.equals("Prerequisite: An appropriate score on the mathematics placement exam.")); {
			System.out.println("found");
		}
		
		for (int i = 0; i < listOfStatements.size(); i++) {
			StatementGroup currGroup = listOfStatements.get(i);
			String currGenStatement = currGroup.getGenStatement();
			List<String> tempParsingOutputList = checkIfStatementMatch(givenString, currGenStatement);
			if (tempParsingOutputList.size() > 0) {
//				outputsWithIndicators = currGroup.getPrereqOutputs();
				parsingOutputList = tempParsingOutputList;
				genStatementForDebugMessage = currGenStatement;
				matchedGroup = currGroup;
				matchFound = true;
				break;
			}
		}
		
		List<Object> returnList = new ArrayList<>();
		returnList.add(parsingOutputList);
		returnList.add(matchedGroup);
		return returnList;
//		if (matchFound) {
//			createCourseDataXML(parsingOutputList, genStatementForDebugMessage, matchedGroup);
//		} else {
//			workForMatchNotFound();
//		}
	}
	
	private void createCourseDataXML(List<String> parsingOutputList, String genStatementForDebugMessage, StatementGroup matchedGroup) {
		List<String> outputsFilledIn = new ArrayList<>();
		
		System.out.println("General Statement is: " + matchedGroup.generalStatement);
		
		List<PrerequisiteData> prereqDataList = matchedGroup.getPrerequisites();
		for (int i = 0; i < prereqDataList.size(); i++) {
			System.out.println("Prerequisite " + i);
			PrerequisiteData currPrereqData = prereqDataList.get(i);
			System.out.println("\tNot Filled In:");
			System.out.println("\t"+currPrereqData.getOutput());
			
			List<String> courseNames = currPrereqData.getNamesOfPrerequsiteCourses();
			for (int j = 0; j < courseNames.size(); j++) {
				System.out.println("\t\t" + courseNames.get(j));
			}
			
			System.out.println();
			
			System.out.println("\tFilled In:");
			System.out.println("\t"+fillInIndicators(currPrereqData.getOutput(), parsingOutputList, matchedGroup.generalStatement));
			
			for (int j = 0; j < courseNames.size(); j++) {
				System.out.println("\t\t" + fillInIndicators(courseNames.get(j), parsingOutputList, matchedGroup.generalStatement));
			}
			System.out.println();
		}
		
//		List<String> outputsWithIndicators = new ArrayList<>();
//		List<List<String>> random = new ArrayList();
//		
//		for (int i = 0; i < prereqDataList.size(); i++) {
//			PrerequisiteData currPrereqData = prereqDataList.get(i);
//			
//			outputsWithIndicators.add(currPrereqData.getOutput());
//			random.add(currPrereqData.getNamesOfPrerequsiteCourses());
//			
//		}
//		
//		// do work to replace outputsWithIndicators with actual words
//		for (int i = 0; i < outputsWithIndicators.size(); i++) {
//			outputsFilledIn.add(fillInIndicators(outputsWithIndicators.get(i), parsingOutputList, genStatementForDebugMessage));
//		}
//			
//
//		System.out.println("Match is found");
//		System.out.println(parsingOutputList);
//		System.out.println(outputsWithIndicators);
//		System.out.println(outputsFilledIn);
//		
		System.out.println();
	}
	
	private void workForMatchNotFound() {
		System.out.println("No match found.");
		System.out.println();
	}
	
	/**
	 * Takes a string with indicators and returns a string that replaces the indicators with the 
	 * corresponding values in the parsingOutputList. All the indices shown in the indicators must
	 * be inbounds of the parsingOutpuList. If this is not the case, it is an error with the "rule"
	 * made in the XML file which maps the general statement to the output. Furthermore, this function
	 * assumes that VAR_INDICATOR is not a character that would show up in the output, otherwise the
	 * function wouldn't work as intended. Again, this is up to whoever makes the output "rule". If
	 * that person (me) uses the VAR_INDICATOR character for anything besides its intended purpose,
	 * the errors are their fault.
	 * @param outputWithIndicators
	 * @param parsingOutputList
	 * @return
	 */
	private String fillInIndicators(String outputWithIndicators, List<String> parsingOutputList, String genStatementForDebugMessage) {
		StringBuilder sb = new StringBuilder(); // So that this method is O(n), where n is the length of outputWithIndicators
		for (int i = 0; i < outputWithIndicators.length(); i++) {
			char currChar = outputWithIndicators.charAt(i);
			if (currChar == VAR_INDICATOR) {
				// Then we know we have something of the form =[#], where # is a number
				// Go through number characters and convert to int to get index
				int totalNumber = 0;
				int numberIndex = i + 2; // start 2 after the = because we know that's where the number starts
				char currNumberChar = outputWithIndicators.charAt(numberIndex);
				while (currNumberChar != ']') {
					totalNumber = totalNumber * 10 + (currNumberChar - '0');
					numberIndex++;
					currNumberChar = outputWithIndicators.charAt(numberIndex);
				}
				i = numberIndex; // let for loop start after =[#]
				// try catch just to help figure out what rule isn't working
				try {
					sb.append(parsingOutputList.get(totalNumber));
				} catch (Exception e) {
					System.out.println("Out of bounds error occured when working with:");
					System.out.println("General Statement: " + genStatementForDebugMessage);
					System.out.println("Output: " + outputWithIndicators);
				}
			} else {
				sb.append(currChar);
			}
		}
		return sb.toString();
	}
	
//	 NOTE WHITE SPACE IN FRONT AND BACK DON'T MATTER??? BECAUSE OF .TRIM() BUT SHOULD THIS BE A THING?
	
	/**
	 * Checks if a given string matches to a general statement
	 * @param givenString
	 * @param generalStatement
	 * @return true if the given string does match. False otherwise.
	 * 
	 * TODO: this is same as checkIfStatementMatchBoolen but returns the array instead.
	 * Can infer if match or not by checking length of array (length == 0 means no match)
	 */
	public List<String> checkIfStatementMatch(String givenString, String generalStatement) {
		// Case of one or both is empty string
		if (givenString.length() == 0 && generalStatement.length() == 0) {
			return Arrays.asList(SENTINEL_VALUE);
		} else if (givenString.length() == 0 || generalStatement.length() == 0) {
			return new ArrayList<>();
		}

		// Case of all white space
		String givenStringTrim = givenString.trim();
		String generalStatementTrim = generalStatement.trim();
		if (givenStringTrim.equals(generalStatementTrim)) {
			return Arrays.asList(SENTINEL_VALUE);
		}
		// TODO: previous things do not do anything with parsingOutputList, do we need to?

		
		// General Case
		List<String> parsingOutputList = new ArrayList<>();
		boolean result = checkIfMatchHelper(givenStringTrim.split(" "), generalStatementTrim.split(" "), 0, 1, 0, parsingOutputList);
		if (result) {
			parsingOutputList.add(SENTINEL_VALUE);
		}
		/* At this point, if the givenString matches the generalStatement, parsingOutputList
		 * holds all the words that matched to the variable indicators in the generalStatement,
		 * in the order that the variable indicators appear. It also holds the SENTINEL_VALUE
		 * at the end of the list so that we can separate matching sentences (with no variable indicators)
		 * and non-matching sentences.
		 * If the givenString does NOT match the generalStatement, parsingOutputList is empty.
		 */
//		System.out.println(parsingOutputList);
		return parsingOutputList;
	}
	
	/**
	 * Checks if a given string matches to a general statement
	 * @param givenString
	 * @param generalStatement
	 * @return true if the given string does match. False otherwise.
	 */
	public boolean checkIfStatementMatchBoolean(String givenString, String generalStatement) {
		// Case of one or both is empty string
		if (givenString.length() == 0 && generalStatement.length() == 0) {
			return true;
		} else if (givenString.length() == 0 || generalStatement.length() == 0) {
			return false;
		}

		// Case of all white space
		String givenStringTrim = givenString.trim();
		String generalStatementTrim = generalStatement.trim();
		if (givenStringTrim.equals(generalStatementTrim)) {
			return true;
		}
		// TODO: previous things do not do anything with parsingOutputList, do we need to?

		
		// General Case
		List<String> parsingOutputList = new ArrayList<>();
		boolean result = checkIfMatchHelper(givenStringTrim.split(" "), generalStatementTrim.split(" "), 0, 1, 0, parsingOutputList);
		if (result) {
			parsingOutputList.add(SENTINEL_VALUE);
		}
		/* At this point, if the givenString matches the generalStatement, parsingOutputList
		 * holds all the words that matched to the variable indicators in the generalStatement,
		 * in the order that the variable indicators appear. It also holds the SENTINEL_VALUE
		 * at the end of the list so that we can separate matching sentences (with no variable indicators)
		 * and non-matching sentences.
		 * If the givenString does NOT match the generalStatement, parsingOutputList is empty.
		 */
		System.out.println(parsingOutputList);
		return result;
	}
	
	/**
	 * Helper method for checkIfMatch. Uses recursive backtracking
	 * @param givenStringArr givenString split into an array using " " as the divider
	 * @param generalStatementArr generalStatment split into an array using " " as the divider
	 * @param startIndexGiven The starting index (inclusive) of the words in the given string. Will try to use these words to match the current word in the general statement
	 * @param endIndexGiven The ending index (exclusive) of the words in the given string. Will try to use these words to match the current word in the general statement
	 * @param currIndexGen The index of the word in the general statement that we are trying to match to.
	 * @param parsingOutputList list consisting of the word(s) which matched to the variable fields in the general statement. 
	 * In the same order as how the variable fields appear in the general statement. Only valid if this method overall returns true.
	 * @return true if a matching was found between the givenString and the generalStatement. False otherwise
	 */
	private boolean checkIfMatchHelper(String[] givenStringArr, String[] generalStatementArr, int startIndexGiven, int endIndexGiven, int currIndexGen, List<String> parsingOutputList) {
		// This means that all elements of the general statement have been found and there are no extra words in the given string.
		if (currIndexGen == generalStatementArr.length && startIndexGiven == givenStringArr.length) {
			return true;
		} else if (currIndexGen == generalStatementArr.length || startIndexGiven == givenStringArr.length || endIndexGiven > givenStringArr.length) {
			// Mean we ran out of words in one, but the other still has more (since we earlier checked if both ran out of words). So doesn't match
			return false;
		}
		String currWordGen = generalStatementArr[currIndexGen]; // The current word in the general statement we are trying to find a match for
		String givenWords; // The word(s) we are trying to match to the currWordGen
		if (endIndexGiven - startIndexGiven == 1) {
			// Only looking at one word
			givenWords = givenStringArr[startIndexGiven];
		} else {
			// Looking at multiple words to try to match to currWordGen
			givenWords = combineWords(givenStringArr, startIndexGiven, endIndexGiven);
		}
		if (currWordGen.charAt(0) == VAR_INDICATOR) { 
			boolean knowItFails = false;
			// Means it is a "variable" field. In this case, givenWords must match an item in the list for the variable field
			String lastCharOfGiven = givenWords.charAt(givenWords.length() - 1) + "";
			String lastCharOfGen = currWordGen.charAt(currWordGen.length() - 1) + "";
			if (lastCharOfGiven.matches(END_PUNCTUATION) || lastCharOfGen.matches(END_PUNCTUATION)) {
				// Handle case where there is end punctuation after the variable indicator and the 
				// givenWords. What we want is for determineVariableMatch to return true only if 
				// the end punctuation is the same and givenWords (minus one end punctuation
				// is of the type that the variable indicator denotes.
				if (lastCharOfGiven.equals(lastCharOfGen)) {
					// Check if the punctuation is the same, if so, we shorten givenWords to only
					// remove the last punctuation (only need to remove one since variable indicator
					// will only have at most one punctuation on the end - this is assuming good rule making) and pass this
					// new givenWords into determineVariableMatch. Also, we don't shorten currWordGen
					// here because it would have to make a new string which would be slower. Instead,
					// we just deal with that in the regular expression we match to in determineVariableMatch.
					givenWords = givenWords.substring(0, givenWords.length() - 1);
					// Now givenWords is the words which don't consider the one end punctuation that 
					// the variable indicator and the givenWords had in common. determineVariableMatch
					// will return true if givenWords (minus one end punctuation)
					// is of the type that the variable indicator denotes and false otherwise, which
					// is what we want
				} else {
					// end punctuation doesn't match so we know these shouldn't match, try adding more words
					knowItFails = true;
				}
				// TODO: is there a way to do this without making new string for givenWords?
			}
			if (!knowItFails && determineVariableMatch(currWordGen, givenWords)) {
				// Found a match
				parsingOutputList.add(givenWords);
				if (checkIfMatchHelper(givenStringArr, generalStatementArr, endIndexGiven, endIndexGiven + 1, currIndexGen + 1, parsingOutputList)) { // Now trying matching the next word in the general statement
					return true;
				}
				parsingOutputList.remove(parsingOutputList.size() - 1); // Failure when trying to match rest of statement, backtrack
			}
			// TODO: maybe have limit for distance between startIndexGiven and endIndexGiven (maybe 4 words max)
			// Matching the givenWords to currWordGen didn't work (either because the givenWords in this recursive step didn't match to currWordGen or it did but failed in the future), 
			// so try matching with another word from the given statement
			return checkIfMatchHelper(givenStringArr, generalStatementArr, startIndexGiven, endIndexGiven + 1, currIndexGen, parsingOutputList);
		} else {
			// Then givenWords must match exactly to currWordGen
			assert endIndexGiven - startIndexGiven == 1; // Should never reach a point where givenWords is more than one word if we're matching exactly
			if (givenWords.equals(currWordGen)) {
				// Word matched exactly, check to see if the rest of the statement matches
				return checkIfMatchHelper(givenStringArr, generalStatementArr, endIndexGiven, endIndexGiven + 1, currIndexGen + 1, parsingOutputList);
			} else {
				// Fail
				return false;
			}
		}
	}

	/**
	 * Takes a string array and concatenates the words in indices startIndexGiven (inclusive) to endIndexGiven (exclusive),
	 * with a space in between each word
	 * @param givenStringArr The string array
	 * @param startIndexGiven Index of the starting word (inclusive)
	 * @param endIndexGiven Index of the last word (exclusive)
	 * @return resulting string of concatenation
	 */
	private String combineWords(String[] givenStringArr, int startIndexGiven, int endIndexGiven) {
		StringBuilder sb = new StringBuilder(givenStringArr[startIndexGiven]);
		for (int i = startIndexGiven + 1; i < endIndexGiven; i++) {
			sb.append(" " + givenStringArr[i]);
		}
		return sb.toString();
	}
	
	/**
	 * Checks if the givenWords would be considered the provided typeVar
	 * @param typeVar one indicator seen in the general statement
	 * @param givenWords the current set of words we are trying to match to the typeVar
	 * @return true if givenWords is considered the provided typeVar. False otherwise.
	 */
	public boolean determineVariableMatch(String typeVar, String givenWords) {
		if (typeVar.matches(FIELD_INDICATOR_W_PUNC)) {
			return tempFieldOfStudySet.contains(givenWords);
		} else if (typeVar.matches(NUMBER_INDICATOR_W_PUNC)) {
			// Matches 3 numbers and optional letter at the end
			return givenWords.matches(NUMBER_REGULAR_EXPR);
		} else if (typeVar.matches(GRADE_INDICATOR_W_PUNC)) {
			return givenWords.matches(GRADE_REGULAR_EXPR);
		}
//		System.out.println(typeVar + " " + givenWords);
		return false;
	}
	
	private boolean isAFieldOfStudy(String givenString) {
		return tempFieldOfStudySet.contains(givenString);
	}
	
	private class PrerequisiteData {
		private String output;
		private List<String> prerequisiteClassNames; // general version
		
		public PrerequisiteData(Element currPrereqElement) {
			output = currPrereqElement.getElementsByTagName("output").item(0).getTextContent();
			prerequisiteClassNames = new ArrayList<>();
			
			Element prereqCourseElement = (Element) currPrereqElement.getElementsByTagName("prereqCourse").item(0); // know there is only one prereqCourse per prereq eleemnt
			// go through all output in XML and add to list
			NodeList courseNode = prereqCourseElement.getElementsByTagName("course");
			for (int i = 0; i < courseNode.getLength(); i++) {
				Node currCourseNode = courseNode.item(i);
				if (currCourseNode.getNodeType() == Node.ELEMENT_NODE) {
					Element currCourseElement = (Element) currCourseNode;
					String currCourseText = currCourseElement.getTextContent();
					prerequisiteClassNames.add(currCourseText);
				}
			}
		}
		
		public String getOutput() {
			return output;
		}
		
		public List<String> getNamesOfPrerequsiteCourses() {
			return prerequisiteClassNames;
		}
	}
	
	private class StatementGroup {
		private String generalStatement;
		private List<PrerequisiteData> prerequisites;
		
		/**
		 * 
		 * @param generalStatement general statement string from xml file 
		 * @param prereqNode list of prereq nodes associated with the given generalStatement
		 */
		public StatementGroup(String generalStatement, Element currMappingElement) {
			this.generalStatement = generalStatement;
			prerequisites = new ArrayList<>();

			NodeList prereqNode = currMappingElement.getElementsByTagName("prereq");
			// go through all prereq nodes and create a prereq data object for each
			for (int j = 0; j < prereqNode.getLength(); j++) {
				Node currPrereqNode = prereqNode.item(j);
				if (currPrereqNode.getNodeType() == Node.ELEMENT_NODE) {
					Element currPrereqElement = (Element) currPrereqNode;
					prerequisites.add(new PrerequisiteData(currPrereqElement));
				}
			}
			
			// Note that we are doing a shallow copy since I am the only person using this code and
			// I expect myself to not change the contents of the array using the other variable, so
			// if this somehow happens, it's not my fault.
		}
		
		public String getGenStatement() {
			return generalStatement;
		}
		
		public List<PrerequisiteData> getPrerequisites() {
			return prerequisites;
		}
		
//		public List<String> getPrereqOutputs() {
//			return prerequisiteOutputs;
//		}
	}
}


// Prerequisite: Computer Science 429 or 429H with a grade of at least C-.
// C S 429, C S 429H -> 

// **General Statement
// Prerequisite: [Field] [Number1] or [Number2] with a grade of at least [Grade].
// **Prerequisite Output
// [Field] [Number1] with at least a [Grade], [Field] [Number2] with at least a [Grade]

// **When parsing, we would get things in this order
// [Field] [Number1] [Number2] [Grade]
// **Need to get from parsing output to the prerequisite output
// **So I think when we store the general statement, we also need to store the prerequisite output (but with the indices rather than the subject thing) 
// Note: Number1 and Number2 are just to differentiate the numbers for this example. In reality, both tags would just be Number.

// Process:
/*
 * We go through all general statements to see if our given statement matches the general statement
 * 		This would consist of going through the general statement and, whenever we see [], we see if we can find a word(s) that satisfy the []. If so, we add them to array
 * When we find a match, we will end up with the parsing array with the actual values
 * Then we need to go from this parsing array with actual values to prerequisite output
 * 		Think we can do this by having just numbering the parsing array. Then, for the prerequisite statement, we just put the index into the appropriate [] places.
 * 
 * 
 */

// TODO: add try catch to print out specifically which mapping went wrong | fix problem with matching punctuations | 

/*
 * figure out how to create graph from output:
 * 
 * was thinking that in XML file, we also write out the course name that is the 
 * prerequisite (using variable indicators). Then we run through all the courses and generate
 * an output file which has the course name, the prerequisite outputs, and the courses that are
 * its prerequisites. Then, once that is done, we go through all the courses we have, creating
 * a course object for each one then look through files again, but this time filling in the pointers
 * to the prerequisites
 */