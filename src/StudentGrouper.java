import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class StudentGrouper {

	private static final int NUMBER_OF_GROUPS = 4;
	private static final int NUMBER_OF_SUBGROUPS = 3;
	private static final int NUMBER_OF_TEAMS = NUMBER_OF_GROUPS*NUMBER_OF_SUBGROUPS;

	private static List<String> loadTextListFromFile(String fileName) throws IOException {
		return Files.readAllLines(Paths.get(fileName),
				Charset.forName("UTF-8"));
	}

	private static <T> List<List<T>> splitList(List<T> list, int numParts) {
		List<List<T>> result = new ArrayList<List<T>>();
		int start = 0, end = 0, partSize, mod = list.size() % numParts, carry = 0;
		for (int i = 0; i < numParts; i++) {
			carry += mod;
			partSize = list.size() / numParts
					+ (carry >= numParts ? 1 : 0);
			carry = carry % numParts;
			end += partSize;
			result.add(list.subList(start, end));
			start = end;
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		Random generator = new Random("SI 15".hashCode());
		List<String> studentNames = loadTextListFromFile("studenti.txt");
		Map<String, Integer> teams = new HashMap<String, Integer>();
		int currentTeamNumber = 1;
		Collator collator = 
				Collator.getInstance(new Locale("hr")); // "ba" nije podržan
		Collections.sort(studentNames, collator);
		for (List<String> group : splitList(studentNames, NUMBER_OF_GROUPS)) {
			Collections.shuffle(group, generator);
			for (List<String> team : splitList(group, NUMBER_OF_SUBGROUPS)) {
				Collections.sort(team, collator);
				System.out.println("\nTim " + currentTeamNumber + ":");
				for (String teamMember : team) {
					System.out.println(teamMember);
					teams.put(teamMember, currentTeamNumber);
				}
				currentTeamNumber++;
			}
		}
		teams.put("Minela Mustafi", 7);
		
		List<String> projects = loadTextListFromFile("projekti.txt");
		Collections.sort(projects, collator);
		currentTeamNumber = 1;
		Collections.shuffle(projects, generator); 
		System.out.println("\nProjekti: ");
		for (String project : projects) {
			System.out.println("Tim " + currentTeamNumber++ + ": " + project);
		}

		List<String> luckyWinners = new ArrayList<>();
		for(String student: teams.keySet()) {
			Integer studentTeam = teams.get(student);
			if(studentTeam.equals(7) || studentTeam.equals(12)) {
				luckyWinners.add(student);
			}
		}
		Collections.sort(luckyWinners, collator);
		int luckyWinnerIndex = generator.nextInt(luckyWinners.size());
		String luckyWinner = luckyWinners.get(luckyWinnerIndex);
		System.out.println("Student: " + luckyWinner + " se dodjeljuje timu 4!");

		

	}

}