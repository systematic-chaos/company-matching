package tech.company.matching;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
	
	private static final String ENTITIES_PATH = "company_entities.tsv";
	private static final String PROFILES_PATH = "company_profiles.tsv";
	private static final String GROUND_TRUTH_PATH = "ground_truth.tsv";
	
	public static void main(String[] args) {
		CompanyMatcher matcher = new CompanyMatcher(ENTITIES_PATH, PROFILES_PATH);
		
		try {
			matcher.match();
			
			double precision = matcher.evaluate(GROUND_TRUTH_PATH);
			System.out.println(String.format(
					"Precision in companies' profiles and entities matching:\t%.4f",
					precision));
		} catch (URISyntaxException | IOException e) {
			System.err.println("Problem retrieving resource files data: "
					+ e.getMessage());
		}
	}
}
