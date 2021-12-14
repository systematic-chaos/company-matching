package tech.company.matching.restapp;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import tech.company.matching.Company;
import tech.company.matching.resources.CompanyIdsRestResource;
import tech.company.matching.resources.CompanyProfileEntitiesRestResource;

public class CompanyMatchingRestApplication extends Application {
	
	public static final String PROFILE_ID_REQUEST_PARAMETER = "PROFILEID";
	
	protected Map<Company, List<Company>> companies;
	protected Map<Long, Map.Entry<Company, List<Company>>> profileIds;
	
	public CompanyMatchingRestApplication(Map<Company, List<Company>> companies) {
		this.companies = companies;
		
		/* Build a map of companies based on the profile's identifier,
		 * so that the profile itself and individual entities can be
		 * directly retrieved. */
		this.profileIds = buildProfileIdsMap(companies);
	}
	
	public Map<Company, List<Company>> getCompanies() {
		return companies;
	}
	
	public Map.Entry<Company, List<Company>> getCompanyFromProfileId(long profileId) {
		return profileIds.get(profileId);
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		// Define routers for company profiles
		router.attach("/companies/{PROFILEID}", CompanyProfileEntitiesRestResource.class);
		router.attach("/companies/ids/{PROFILEID}", CompanyIdsRestResource.class);
		return router;
	}
	
	private Map<Long, Map.Entry<Company, List<Company>>> buildProfileIdsMap(Map<Company, List<Company>> m) {
		Map<Long, Map.Entry<Company, List<Company>>> p = new Hashtable<>(m.size(), 1f);
		m.entrySet().forEach((e) -> p.put(e.getKey().getId(), e));
		return p;
	}	
}
