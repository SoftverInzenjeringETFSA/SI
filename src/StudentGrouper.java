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
		// TODO Auto-generated method stub
		Random generator = new Random("2014 SI".hashCode());
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
		List<String> projects = loadTextListFromFile("projekti.txt");
		Collections.sort(projects, collator);
		currentTeamNumber = 1;
		Collections.shuffle(projects, generator); 
		System.out.println("\nProjekti: ");
		for (String project : projects) {
			System.out.println("Tim " + currentTeamNumber++ + ": " + project);
		}
		StudentTestingShuffler studentTestingShuffler = new StudentTestingShuffler(teams, generator);
		Map<String, Integer> studentTestingTeam = studentTestingShuffler.shuffle();
		System.out.println("Testiranje:");
		for (String student : studentNames) {
			System.out.println(student + " (tim " + teams.get(student) +")" + " => projekt od tima " + studentTestingTeam.get(student));
		}

	}

}
class StudentTestingShuffler {
	
	private final Map<String, Integer> studentTeams;
	private final Random generator;

	public StudentTestingShuffler(Map<String, Integer> studentTeams, Random generator) {
		this.studentTeams = studentTeams;
		this.generator = generator;
	}
	
	public Map<String, Integer> shuffle() {
		List<Integer> teamNumbers = new ArrayList<Integer>(studentTeams.values());
		Collections.shuffle(teamNumbers, generator);
		Integer maxTeamNumber = Collections.max(studentTeams.values());
		int[] teamSizes = new int[maxTeamNumber+1];
		for(Integer n: teamNumbers) {
			teamSizes[n]++;
		}
		Map<Integer, Set<Integer>> assignedTeams = initAssignedTeams(maxTeamNumber);
		int currentTeam = 1;
		while(teamNumbers.size() > 0) {
			int teamNumbersSize = teamNumbers.size();
			for(int i = teamNumbers.size() - 1; i >= 0 ; i--) {
				Integer n = teamNumbers.get(i);
				if(n != currentTeam) {
					if(!assignedTeams.get(currentTeam).contains(n)) {
						if(assignedTeams.get(currentTeam).size() < teamSizes[currentTeam]) {
							assignedTeams.get(currentTeam).add(n);
							teamNumbers.remove(i);
						}
					}
				}
			}
			if(teamNumbers.size() == teamNumbersSize) {
				int teamToRemove = generator.nextInt(maxTeamNumber) + 1;
				int memberToRemove = assignedTeams.get(teamToRemove).iterator().next();
				assignedTeams.get(teamToRemove).remove(memberToRemove);
				teamNumbers.add(memberToRemove);
			}
			currentTeam++;
			if(currentTeam > maxTeamNumber) 
				currentTeam = 1;
		}
		Map<String, Integer> result = new HashMap<String, Integer>();
		for(String student: studentTeams.keySet()) {
			Integer team = studentTeams.get(student);
			Integer assignedTeam = assignedTeams.get(team).iterator().next();
			result.put(student, assignedTeam);
			assignedTeams.get(team).remove(assignedTeam);
		}
		return result;
	}

	private Map<Integer, Set<Integer>> initAssignedTeams(Integer maxTeamNumber) {
		Map<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();
		for(int i = 1; i <= maxTeamNumber; i++) {
			result.put(i, new HashSet<Integer>());
		}
		return result;
	}
}