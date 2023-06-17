import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Parser p = new Parser();
//		String[] test = {"Computer", "Science"};
//		System.out.println(p.determineVariableMatch("=[Field]", "Computer Science"));
		
		String testGen1 = "Prerequisite: =[Field] 50";
		String testGiven1 = "Prerequisite: Computer Science Computer 50";
		System.out.println(p.checkIfMatch(testGiven1, testGen1));
		
		String testGen2 = "Prerequisite: =[Field] note this is not the Field Communication";
		String testGiven2 = "Prerequisite: Communication and Leadership note this is not the Field Communication";
		System.out.println(p.checkIfMatch(testGiven2, testGen2));
		
		String testGen3 = "Prerequisite: =[Field] and also the Field =[Field] in addition";
		String testGiven3 = "Prerequisite: Communication and Leadership and also the Field Computer Science Computer in addition";
		System.out.println(p.checkIfMatch(testGiven3, testGen3));
		
		// TODO: FIX THIS
		String testGen4 = " ";
		String testGiven4 = " ";
		System.out.println(p.checkIfMatch(testGiven4, testGen4));
		
//		System.out.println(Arrays.toString("".split(" ")));
		
	}
}
