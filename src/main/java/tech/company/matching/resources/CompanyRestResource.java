/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.resources.CompanyRestResource.java
 */

package tech.company.matching.resources;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Options;
import org.restlet.resource.ServerResource;

import tech.company.matching.Company;
import tech.company.matching.restapp.CompanyMatchingRestApplication;
import tech.company.matching.serializers.CompanySerializer;

/**
 * Abstract class providing common functionality for company related REST resources
 */
public abstract class CompanyRestResource extends ServerResource {
	
	protected CompanyMatchingRestApplication app;
	
	protected CompanyRestResource() {
		app = (CompanyMatchingRestApplication) getApplication();
	}
	
	protected Map<Company, List<Company>> getCompanies() {
		return app.getCompanies();
	}
	
	protected Map.Entry<Company, List<Company>> getCompanyFromProfileId(long profileId) {
		return app.getCompanyFromProfileId(profileId);
	}
	
	public abstract Representation getCompany() throws JSONException;
	
	protected Representation getCompany(CompanySerializer serializer) {
		
		// Check the company profile identifier's validity
		long profileId;
		String profileIdStr = (String) getRequest().getAttributes().get(CompanyMatchingRestApplication.PROFILE_ID_REQUEST_PARAMETER);
		if (StringUtils.isNumeric(profileIdStr)) {
			profileId = Long.valueOf(profileIdStr);
		} else {
			return new StringRepresentation("Profile id must be an integer.", MediaType.TEXT_PLAIN);
		}
		
		// Check whether the company profile exists
		Map.Entry<Company, List<Company>> company = getCompanyFromProfileId(profileId);
		if (company == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(
					String.format("Company with profile id %d does not exist.", profileId),
					MediaType.TEXT_PLAIN);
		}
		
		/* In case everything is all right, retrieve and return the company profile
		 * along with its associated entities. */
		setStatus(Status.SUCCESS_OK);
		return new StringRepresentation(serializer.getAsJson(company).toString(), MediaType.APPLICATION_JSON);
	}
	
	public void headCompany() {
		setStatus(Status.SUCCESS_OK);
	}
	
	@Options
	public void describe() {
		Set<Method> meths = new HashSet<Method>();
		meths.add(Method.GET);
		meths.add(Method.HEAD);
		meths.add(Method.OPTIONS);
		getResponse().setAllowedMethods(meths);
	}
}
