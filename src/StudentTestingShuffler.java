import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class StudentTestingShuffler {
	
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
