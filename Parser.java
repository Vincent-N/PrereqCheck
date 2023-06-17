import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {

	private Set<String> tempFieldOfStudySet;
	private List<StatementGroup> listOfStatements;
	private static final char VAR_INDICATOR = '=';
	private static final String FIELD_INDICATOR = "=\\[Field\\]";
	private static final String END_PUNCTUATION = "[.,]*";
	
	public Parser() {
		tempFieldOfStudySet = new HashSet<>();
		tempFieldOfStudySet.add("Computer Science");
		tempFieldOfStudySet.add("Computer Science Computer");
		tempFieldOfStudySet.add("Communication");
		tempFieldOfStudySet.add("Communication and Leadership");
		listOfStatements = new ArrayList<>();
		listOfStatements.add(new StatementGroup("Prerequisite: =[Field] =[Number1] or =[Number2] with a grade of at least =[Grade].", "=[0] =[1] with at least a =[3], =[0] =[2] with at least a =[3]"));
		
	}
	
	private void initializeFieldOfStudySet() {
		
	}
	
//	 NOTE WHITE SPACE IN FRONT AND BACK DON'T MATTER??? BECAUSE OF .TRIM() BUT SHOULD THIS BE A THING?
	
	/**
	 * Checks if a given string matches to a general statement
	 * @param givenString
	 * @param generalStatement
	 * @return true if the given string does match. False otherwise.
	 */
	public boolean checkIfMatch(String givenString, String generalStatement) {
		if (givenString.length() == 0 && generalStatement.length() == 0) {
			return true;
		} else if (givenString.length() == 0 || generalStatement.length() == 0) {
			return false;
		}
		List<String> parsingOutputList = new ArrayList<>();
		boolean result = checkIfMatchHelper(givenString.trim().split(" "), generalStatement.trim().split(" "), 0, 1, 0, parsingOutputList);
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
			// Means it is a "variable" field. In this case, givenWords must match an item in the list for the variable field
			if (determineVariableMatch(currWordGen, givenWords)) {
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
	
	public boolean determineVariableMatch(String typeVar, String givenWords) {
		if (typeVar.matches(FIELD_INDICATOR + END_PUNCTUATION)) {
			if (tempFieldOfStudySet.contains(givenWords)) {
//				System.out.println(typeVar + " " + givenWords);
				return true;
			}
		}
//		System.out.println(typeVar + " " + givenWords);
		return false;
		
	}
	
	private boolean isAFieldOfStudy(String givenString) {
		return tempFieldOfStudySet.contains(givenString);
	}
	
	private class StatementGroup {
		private String generalStatement;
		private String prerequisiteOutput;
		
		public StatementGroup(String generalStatement, String prerequisiteOutput) {
			this.generalStatement = generalStatement;
			this.prerequisiteOutput = prerequisiteOutput;
		}
		
		public String getGenStatement() {
			return generalStatement;
		}
		
		public String getPrereqOutput() {
			return prerequisiteOutput;
		}
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

// 