import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Parser p = new Parser();
//		String[] test = {"Computer", "Science"};
//		System.out.println(p.determineVariableMatch("=[Field]", "Computer Science"));
//		System.out.println(p.determineVariableMatch("=[Number]", "375"));
//		System.out.println(p.determineVariableMatch("=[Grade]", "F+"));
		
		/*
		String testGen1 = "Prerequisite: =[Field] 50";
		String testGiven1 = "Prerequisite: Computer Science Computer 50";
		System.out.println(p.checkIfStatementMatch(testGiven1, testGen1));
		
		String testGen2 = "Prerequisite: =[Field] note this is not the Field Communication";
		String testGiven2 = "Prerequisite: Communication and Leadership note this is not the Field Communication";
		System.out.println(p.checkIfStatementMatch(testGiven2, testGen2));
		
		String testGen3 = "Prerequisite: =[Field] and also the Field =[Field] in addition";
		String testGiven3 = "Prerequisite: Communication and Leadership and also the Field Computer Science Computer in addition";
		System.out.println(p.checkIfStatementMatch(testGiven3, testGen3));
		
		// TODO: FIX THIS
		String testGen4 = " ";
		String testGiven4 = " ";
		System.out.println(p.checkIfStatementMatch(testGiven4, testGen4));
		
		String testGen5 = "This is a test to see if we can match statements without variable indicators.";
		String testGiven5 = "This is a test to see if we can match statements without variable indicators.";
		System.out.println(p.checkIfStatementMatch(testGiven5, testGen5));
		*/
		
//		p.findMatchFromAllGeneral("Prerequisite: Computer Science Computer 50");
//		p.findMatchFromAllGeneral("Prerequisite: Communication and Leadership note this is not the Field Communication");
//		p.findMatchFromAllGeneral("Prerequisite: Communication and Leadership and also the Field Computer Science Computer in addition");
//		p.findMatchFromAllGeneral("Prerequisite: Computer Science 429 or 429H with a grade of at least C-.");
//		p.findMatchFromAllGeneral("Testing with end punctuation. Computer Science, Communication and Leadership. 375, 123H. B+, D-.");
//		p.findMatchFromAllGeneral("Computer Science Communication and Leadership");
//		System.out.println(Arrays.toString("".split(" ")));
		
		p.findMatchFromAllGeneral("Test1");
		p.findMatchFromAllGeneral("Test2: Computer Science 375 C-");
		p.findMatchFromAllGeneral("Test3: field Communication and Leadership, course number 123S, with the grade D.");
		p.findMatchFromAllGeneral("Test4: givenStatement should be missing end period Computer Science");
		p.findMatchFromAllGeneral("Test5: generalStatement should be missing end period Computer Science.");
		
		
		
		/*
		String outputWithIndicators = "=[0] =[2] with at least a =[3]";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < outputWithIndicators.length(); i++) {
			char currChar = outputWithIndicators.charAt(i);
			if (currChar == '=') {
				// Then we know we have something of the form =[#], where # is a number
				int totalNumber = 0;
				int numberIndex = i + 2; // start 2 after the = because we know that's where the number starts
				char currNumberChar = outputWithIndicators.charAt(numberIndex);
				while (currNumberChar != ']') {
					totalNumber = totalNumber * 10 + (currNumberChar - '0');
					numberIndex++;
					currNumberChar = outputWithIndicators.charAt(numberIndex);
				}
				i = numberIndex; // let for loop start after =[#]
				sb.append(totalNumber);
			} else {
				sb.append(currChar);
			}
		}
		System.out.println(sb.toString());
		*/
	}
}
