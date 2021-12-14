package tech.company.matching;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.restlet.Component;
import org.restlet.data.Protocol;

import tech.company.matching.restapp.CompanyMatchingRestApplication;

public class CompanyMatchingMain {
	
	// TODO LIST
	// Implement equals and hashCode methods in the Company class
	// API REST exposing two endpoints, for full companies and for their identifiers
	/* Provide basic statistic measures that may be useful:
	 * mean, median and mode for the amount of entities per profile. */
	
	private static final String ENTITIES_PATH = "company_entities.tsv";
	private static final String PROFILES_PATH = "company_profiles.tsv";
	private static final String GROUND_TRUTH_PATH = "ground_truth.tsv";
	
	public static void main(String[] args) {
		CompanyMatcher matcher = new CompanyMatcher(ENTITIES_PATH, PROFILES_PATH);
		Map<Company, List<Company>> companyMatch = new Hashtable<>();
		
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
}
