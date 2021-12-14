package tech.company.matching.resources;

import org.json.JSONException;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import tech.company.matching.serializers.CompanyIdsSerializer;

public class CompanyIdsRestResource extends CompanyRestResource {
	
	@Get
	public Representation getCompany() throws JSONException {
		return getCompany(CompanyIdsSerializer.getInstance());
	}
}
