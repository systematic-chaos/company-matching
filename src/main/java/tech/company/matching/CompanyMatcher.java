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
	
	public Map<Company, List<Company>> match() throws URISyntaxException, IOException {
		
		/* Since the file of entities is assumed to fit into main memory,
		 * it is stored into a list, so that its contents do not need
		 * to be loaded from secondary storage multiple times. */
		Path companyEntitiesPath = Paths.get(getClass().getClassLoader().getResource(entitiesPath).toURI());
		List<Company> entities = Files.lines(companyEntitiesPath)
				.map(CompanyMatcher::stringToCompany)
				.collect(Collectors.toList());
		
		System.out.println("Mapped entities");
		
		this.profiles = new Hashtable<>(16384);
		
		Path companyProfilesPath = Paths.get(getClass().getClassLoader().getResource(profilesPath).toURI());
		Files.lines(companyProfilesPath).map(CompanyMatcher::stringToCompany).forEach((profile) -> {
			List<Company> entityIds = entities.parallelStream() // The entities are filtered and mapped in parallel
				// A entity is considered to correspond to a profile if its name contains the profile's company name
				.filter((entity) -> companyMatch(profile, entity))
				.collect(Collectors.toList());
			profiles.put(profile, entityIds);
		});
		
		System.out.println("Matched profiles");
		
		return profiles;
	}
	
	public double evaluate(String groundTruthPath) throws URISyntaxException, IOException {
		if (profiles == null || profiles.isEmpty()) {
			return 0f; // No matching phase was performed
		}
		
		// Transform the companies map into another map which just stores identifiers
		Map<Long, List<Long>> profilesIds = getCompanyIds(profiles);
		
		int totalEntities;
		int correctClassifications;
		
		String[] gtLine;
		long[] gt = new long[2];
		
		Path groundTruth = Paths.get(getClass().getClassLoader().getResource(groundTruthPath).toURI());
		Iterator<String> gtIt = Files.lines(groundTruth).iterator();
		
		// Compute the total number of entities and how many of them were correctly classified 
		for (totalEntities = 0, correctClassifications = 0; gtIt.hasNext(); totalEntities++) {
			gtLine = gtIt.next().split("\t");
			gt[0] = Long.valueOf(gtLine[0]);
			gt[1] = Long.valueOf(gtLine[1]);
			
			// The profile exists in the map and its matching entities list contains the entity id provided
			if (profilesIds.containsKey(gt[0]) && profilesIds.get(gt[0]).contains(gt[1])) {
				correctClassifications++;
			}
		}
		
		System.out.println("Correct classifications:\t" + correctClassifications);
		System.out.println("Total entities:\t" + totalEntities);
		
		return (double) correctClassifications / totalEntities;
	}
	
	private boolean companyMatch(Company profile, Company entity) {
		String profileName = profile.getCompanyName();
		String entityName = entity.getCompanyName();
		
		String profileSite = curateWebsiteUrl(profile.getWebsiteUrl());
		String entitySite = curateWebsiteUrl(entity.getWebsiteUrl());
		
		boolean nameMatch = StringUtils.equalsIgnoreCase(profileName, entityName)
				|| StringUtils.equalsIgnoreCase(entityName, profileName);
		boolean websiteMatch = StringUtils.isNotBlank(profileSite) && StringUtils.isNotBlank(entitySite)
				&& (StringUtils.equalsIgnoreCase(entitySite, profileSite)
						|| StringUtils.equalsIgnoreCase(profileSite, entitySite));
		boolean countryMatch = StringUtils.equals(profile.getCountry(), entity.getCountry());
		
		return (nameMatch || websiteMatch) && countryMatch;
	}
	
	private static Map<Long, List<Long>> getCompanyIds(Map<Company, List<Company>> companyMatches) {
		Map<Long, List<Long>> idMatches = new Hashtable<>(companyMatches.size(), 1f);
		for (Map.Entry<Company, List<Company>> cMatch : companyMatches.entrySet()) {
			final List<Long> entityIdsAux = new ArrayList<>(cMatch.getValue().size());
			cMatch.getValue().forEach((p) -> entityIdsAux.add(p.getId()));
			idMatches.put(cMatch.getKey().getId(), entityIdsAux);
		}
		return idMatches;
	}
	
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
	
	private String curateWebsiteUrl(String websiteUrl) {
		if (StringUtils.contains(websiteUrl, "://")) {
			websiteUrl = StringUtils.substringAfter(websiteUrl, "://");
		}
		websiteUrl = StringUtils.removeStartIgnoreCase(websiteUrl, "www.");
		websiteUrl = StringUtils.removeEnd(websiteUrl, "/");
		return websiteUrl;
	}
}
