/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.CompanyMatchingMain.java
 */

package tech.company.matching;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.restlet.Component;
import org.restlet.data.Protocol;

import tech.company.matching.restapp.CompanyMatchingRestApplication;

public class CompanyMatchingMain {
	
	private static final String ENTITIES_PATH = "company_entities.tsv";
	private static final String PROFILES_PATH = "company_profiles.tsv";
	private static final String GROUND_TRUTH_PATH = "ground_truth.tsv";
	
	public static void main(String[] args) {
		CompanyMatcher matcher = new CompanyMatcher(ENTITIES_PATH, PROFILES_PATH);
		Map<Company, List<Company>> companyMatch = new Hashtable<>();
		
		// Match companies' profiles and entities and evaluate results
		try {
			companyMatch = matcher.match();
			
			double precision = matcher.evaluate(GROUND_TRUTH_PATH);
			System.out.println(String.format(
					"Precision in companies' profiles and entities matching:\t%.4f",
					precision));
		} catch (URISyntaxException | IOException e) {
			System.err.println("Problem retrieving resource files data: "
					+ e.getMessage());
		}
		
		// Display some additional statistical measures
		System.out.println("\nEntities per profile:");
		System.out.println(String.format("Mean:\t%.2f", mean(companyMatch)));
		System.out.println(String.format("Mode:\t%d", mode(companyMatch)));
		System.out.println(String.format("Median\t%.2f", median(companyMatch)));
		
		
		
		// Add a new HTTP server listening on port 8182
		Component restComponent = new Component();
		restComponent.getServers().add(Protocol.HTTP, 8182);
		restComponent.getDefaultHost().attach(new CompanyMatchingRestApplication(companyMatch));
		
		// Start the REST endpoint
		try {
			restComponent.start();
		} catch (Exception e) {
			System.err.println("HTTP REST server failed:\t" + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	
	private static double mean(Map<Company, List<Company>> companyMatches) {
		long total = 0;
		for (List<Company> entities : companyMatches.values()) {
			total += entities.size();
		}
		return (double) total / companyMatches.size();
	}
	
	private static int mode(Map<Company, List<Company>> companyMatches) {
		Map<Integer, Integer> occurrences = new Hashtable<>();
		int size;
		for (List<Company> entities : companyMatches.values()) {
			size = entities.size();
			occurrences.put(size, occurrences.containsKey(size) ?
					occurrences.get(size) + 1 : 1);
		}
		
		int modeValue = 0;
		int maxOccurrences = 0;
		for (Map.Entry<Integer, Integer> elem : occurrences.entrySet()) {
			if (elem.getValue() > maxOccurrences) {
				modeValue = elem.getKey();
				maxOccurrences = elem.getValue();
			}
		}
		
		return modeValue;
	}
	
	private static double median(Map<Company, List<Company>> companyMatches) {
		List<Integer> entitiesPerProfile = companyMatches.values().stream()
				.map((entities) -> entities.size()).sorted().collect(Collectors.toList());
		int nEntities = entitiesPerProfile.size();
		if (nEntities % 2 == 0) {
			return ((double) entitiesPerProfile.get(nEntities / 2 - 1) + (double) entitiesPerProfile.get(nEntities / 2)) / 2f;
		} else {
			return (double) entitiesPerProfile.get(nEntities / 2);
		}
	}
}
