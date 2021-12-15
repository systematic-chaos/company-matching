/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.CompanyMatcher.java
 */

package tech.company.matching;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class CompanyMatcher {
	
	private String entitiesPath;
	private String profilesPath;
	
	private Map<Company, List<Company>> profiles;
	
	public CompanyMatcher(String entitiesPath, String profilesPath) {
		this.entitiesPath = entitiesPath;
		this.profilesPath = profilesPath;
	}
	
	/**
	 * Read the companies data files and match entities to profiles
	 * @return Data structure binding profiles to their matching entities
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public Map<Company, List<Company>> match() throws URISyntaxException, IOException {
		
		/* Since the file of entities is assumed to fit into main memory,
		 * it is stored into a list, so that its contents do not need
		 * to be loaded from secondary storage multiple times. */
		System.out.println("Mapping entities");
		Path companyEntitiesPath = Paths.get(getClass().getClassLoader().getResource(entitiesPath).toURI());
		List<Company> entities = Files.lines(companyEntitiesPath)
				.map(CompanyMatcher::stringToCompany)
				.collect(Collectors.toList());
		
		System.out.println("Mapped entities");
		System.out.println("Matching profiles");
		
		this.profiles = new Hashtable<>(16384);
		
		Path companyProfilesPath = Paths.get(getClass().getClassLoader().getResource(profilesPath).toURI());
		Files.lines(companyProfilesPath).map(CompanyMatcher::stringToCompany).forEach((profile) -> {
			List<Company> entityIds = entities.parallelStream() // The entities are filtered and mapped in parallel
				// Determine whether an entity corresponds to a profile
				.filter((entity) -> companyMatch(profile, entity))
				.collect(Collectors.toList());
			profiles.put(profile, entityIds);
		});
		
		System.out.println("Matched profiles");
		
		return profiles;
	}
	
	/**
	 * Evaluate a previously performed company data matching in terms of a ground truth file
	 * Calculate the number of many entities were matched to the right company profile
	 * as well as how many entities were matched to incorrect profiles. Both data are displayed
	 * in absolute terms and as a ratio in terms of the total number of test cases.
	 * @param groundTruthPath Path for the ground truth file resource
	 * @return The ratio of correct classifications (how many entities were correctly classified
	 * divided by the total number of test cases)
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public double evaluate(String groundTruthPath) throws URISyntaxException, IOException {
		if (profiles == null || profiles.isEmpty()) {
			return 0f; // No matching phase was performed
		}
		
		System.out.println("Evaluating");
		
		// Transform the companies map into another map which just stores identifiers
		Map<Long, List<Long>> profilesIds = getCompanyIds();
		
		int totalEntities = 0;
		int correctClassifications = 0;
		int incorrectClassifications = 0;
		
		String[] gtLine;
		long[] gt = new long[2];
		
		Path groundTruth = Paths.get(getClass().getClassLoader().getResource(groundTruthPath).toURI());
		Iterator<String> gtIt = Files.lines(groundTruth).iterator();
		
		// Compute the total number of entities and how many of them were correctly classified 
		for (; gtIt.hasNext(); totalEntities++) {
			gtLine = gtIt.next().split("\t");
			gt[0] = Long.valueOf(gtLine[0]);
			gt[1] = Long.valueOf(gtLine[1]);
			
			// The profile exists in the map and its matching entities list contains the entity id provided
			if (profilesIds.containsKey(gt[0]) && profilesIds.get(gt[0]).contains(gt[1])) {
				correctClassifications++;
			}
			
			// Also check those entities which have been matched to profiles they do not belong to
			for (Map.Entry<Long, List<Long>> matchedEntity : profilesIds.entrySet()) {
				if (gt[0] != matchedEntity.getKey() && matchedEntity.getValue().contains(gt[1])) {
					incorrectClassifications++;
				}
			}
		}
		
		System.out.println("Correct classifications:\t" + correctClassifications);
		System.out.println("Incorrect classifications:\t" + incorrectClassifications);
		System.out.println("Number of cases tested:\t" + totalEntities);
		
		return (double) correctClassifications / totalEntities;
	}
	
	/**
	 * Checks whether a profile and an entity do match.
	 * This implementation performs some basic data curations and comparisons, however,
	 * it does not pretend to be a sophisticated and highly reliable classifier.
	 * @param profile
	 * @param entity
	 * @return Whether the profile and entity provided as parameters match
	 * 	according to some basic verification operations on their data.
	 */
	private boolean companyMatch(Company profile, Company entity) {
		String profileCompanyName = profile.getCompanyName();
		String entityName = entity.getCompanyName();
		boolean nameMatch = StringUtils.containsIgnoreCase(entityName, firstWord(profileCompanyName))
				|| StringUtils.containsIgnoreCase(profileCompanyName, firstWord(entityName));
		
		String profileSite = curateWebsiteUrl(profile.getWebsiteUrl());
		String entitySite = curateWebsiteUrl(entity.getWebsiteUrl());
		boolean websiteMatch = StringUtils.isNotBlank(profileSite) && StringUtils.isNotBlank(entitySite)
				&& (StringUtils.equalsIgnoreCase(entitySite, profileSite)
						|| StringUtils.equalsIgnoreCase(profileSite, entitySite));
		
		boolean countryMatch = StringUtils.equals(profile.getCountry(), entity.getCountry());
		
		return (nameMatch || websiteMatch) && countryMatch;
	}
	
	/**
	 * Transform the profiles and entities map into an equivalent map which only stores
	 * their identifiers, instead of the complete Company object
	 * @param companyMatches
	 * @return Identifiers matching map
	 */
	private Map<Long, List<Long>> getCompanyIds() {
		Map<Long, List<Long>> idMatches = new Hashtable<>(profiles.size(), 1f);
		for (Map.Entry<Company, List<Company>> cMatch : profiles.entrySet()) {
			final List<Long> entityIdsAux = new ArrayList<>(cMatch.getValue().size());
			cMatch.getValue().forEach((p) -> entityIdsAux.add(p.getId()));
			idMatches.put(cMatch.getKey().getId(), entityIdsAux);
		}
		return idMatches;
	}
	
	/**
	 * Utility method for parsing text from data files into Company objects
	 */
	private static Company stringToCompany(String line) {
		String[] companyLine = line.split("\t");
		long id = Long.valueOf(companyLine[0]);
		String companyName = companyLine[1];
		String websiteUrl = companyLine[2];
		Short foundationYear = StringUtils.isNumeric(companyLine[3]) ?
				Short.valueOf(companyLine[3]) : null;
		String city = companyLine.length > 4 ? companyLine[4] : null;
		String country = companyLine.length > 5 ? companyLine[5] : null;
		return new Company(id, companyName, websiteUrl, foundationYear, city, country);
	}

	private String firstWord(String line) {
		return line.split(" ")[0].toLowerCase();
	}
	
	private String curateWebsiteUrl(String websiteUrl) {
		if (StringUtils.contains(websiteUrl, "://")) {
			websiteUrl = StringUtils.substringAfter(websiteUrl, "://");
		}
		websiteUrl = StringUtils.removeStartIgnoreCase(websiteUrl, "www.");
		websiteUrl = StringUtils.removeEnd(websiteUrl, "/");
		return websiteUrl;
	}
}
