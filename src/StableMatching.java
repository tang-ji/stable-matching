import java.util.*;

// Author: Ji TANG
// Promotion: X2016
// E-mail: ji.tang@polytechnique.edu

public class StableMatching implements StableMatchingInterface {
	
	// The final return matrix.
	int[][] engaged;
	int[][] menPrefs;
	
	// The start points of this group of men's preferences.
	int[] menPrefsStart;
	int[] menSingleCount;
	int[] womenSingleCount;
	WomenGroup[] womenGroups;
	
	// A class of women group.
	class WomenGroup {
		Integer womenGroup;
		// A sequence array of men engaged to this group of women.
		PriorityQueue<Integer> engagedToWomen;
		
		// Initialization of this class.
		WomenGroup(Integer i, int[] preference) {
			engagedToWomen = new PriorityQueue<Integer>(1, new Comparator<Integer>(){
				public int compare(Integer I1, Integer I2) {
					return preference[I2] - preference[I1];
				}
			});
			womenGroup = i;
		}
	}

	// A function realize the engagement for the "menGroup" group of men.
	private void engage(Integer menGroup) {
		
		// Get the number of the women group in the first valid preference position of this man. 
		Integer womenGroup = menPrefs[menGroup][menPrefsStart[menGroup]];
		
		// The engagements for the whole group of men.
		engaged[menGroup][womenGroup] += menSingleCount[menGroup];
		womenSingleCount[womenGroup] -= menSingleCount[menGroup];
		menSingleCount[menGroup] = 0;
		
		// Add this group of men into the engagement list of that group of women.
		if(!womenGroups[womenGroup].engagedToWomen.contains(menGroup)) womenGroups[womenGroup].engagedToWomen.offer(menGroup);
		
		// If the women group is overload.
		while(womenSingleCount[womenGroup] < 0) {
			Integer lastMenGroup = womenGroups[womenGroup].engagedToWomen.peek();
			// The group of men prefers this group of women permanently discard group of women.
			if(menPrefs[lastMenGroup][menPrefsStart[lastMenGroup]] == womenGroup) menPrefsStart[lastMenGroup]++;

			if(engaged[lastMenGroup][womenGroup] + womenSingleCount[womenGroup] > 0) {
				engaged[lastMenGroup][womenGroup] += womenSingleCount[womenGroup];
				menSingleCount[lastMenGroup] = menSingleCount[lastMenGroup] - womenSingleCount[womenGroup];
				womenSingleCount[womenGroup] = 0;
			}
			else {
				menSingleCount[lastMenGroup] += engaged[lastMenGroup][womenGroup];
				womenSingleCount[womenGroup] += engaged[lastMenGroup][womenGroup];
				engaged[lastMenGroup][womenGroup] = 0;
				womenGroups[womenGroup].engagedToWomen.remove(lastMenGroup);
			}
		}
	}
	
	// This matrix describe a stable matching.
	public int[][] constructStableMatching(int[] menGroupCount, int[] womenGroupCount, int[][] menPrefs,
			int[][] womenPrefs) {

		// Initialization of the main class.
		this.menPrefs = menPrefs;
		engaged = new int[menGroupCount.length][womenGroupCount.length];
		menPrefsStart = new int[menGroupCount.length];
		menSingleCount = menGroupCount.clone();
		womenSingleCount = womenGroupCount.clone();
		
		// For the null situation.
		if(menGroupCount == null || menGroupCount.length == 0) return engaged;

		// For the simple model.
		for(int i = 0; i < menGroupCount.length; i++) {
			menSingleCount[i] = menGroupCount[i];
		}
		
		// Initialize the women group classes.
		womenGroups = new WomenGroup[womenGroupCount.length];
		for(int i = 0; i < womenGroupCount.length; i++) {
			int[] MensPreferences = new int[menGroupCount.length];
			for(int j = 0; j < menGroupCount.length; j++) MensPreferences[womenPrefs[i][j]] = j;
			womenGroups[i] = new WomenGroup(i, MensPreferences);
		}
		
		// Do the engagements for each group of single men.
		boolean finish = false;
		while(!finish) {
			finish = true;
			for(int i = 0; i < menGroupCount.length; i++) {
				if(menSingleCount[i] > 0) {
					engage(i);
					finish = false;
				}
			}
		}
		
		// Return the result matrix.
		return engaged;
	}
	

}