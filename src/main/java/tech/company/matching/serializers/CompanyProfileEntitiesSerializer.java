/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.serializers.CompanyProfileEntitiesSerializer.java
 */

package tech.company.matching.serializers;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.company.matching.Company;

public final class CompanyProfileEntitiesSerializer implements CompanySerializer {
	
	private CompanyProfileEntitiesSerializer() {
	}
	
	private static CompanyProfileEntitiesSerializer instance = null;
	
	public static CompanyProfileEntitiesSerializer getInstance() {
		if (instance == null) {
			instance = new CompanyProfileEntitiesSerializer();
		}
		return instance;
	}
	
	public JSONObject getAsJson(Map.Entry<Company, List<Company>> c) {
		JSONObject jsonCompany = new JSONObject();
		
		try {
			// Profile data
			jsonCompany = getAsJson(c.getKey());
			
			// Entities data
			JSONArray jsonEntities = new JSONArray();
			jsonCompany.put("entities", jsonEntities);
			for (Company entity : c.getValue()) {
				jsonEntities.put(getAsJson(entity));
			}
		} catch (JSONException jsone) {
			System.err.println(String.format("%s:\t%s",
					CompanyProfileEntitiesSerializer.class, jsone.getMessage()));
		}
		
		return jsonCompany;
	}
	
	public JSONObject getAsJson(Company company) {
		JSONObject jsonCompany = new JSONObject();
		jsonCompany.put("id", company.getId());
		jsonCompany.put("companyName", company.getCompanyName());
		jsonCompany.put("websiteUrl", company.getWebsiteUrl());
		jsonCompany.put("foundationYear", company.getFoundationYear());
		jsonCompany.put("city", company.getCity());
		jsonCompany.put("country", company.getCountry());
		return jsonCompany;
	}
}
