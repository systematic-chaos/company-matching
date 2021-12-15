/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.serializers.CompanyIdsSerializer.java
 */

package tech.company.matching.serializers;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.company.matching.Company;

public class CompanyIdsSerializer implements CompanySerializer {
	
	private CompanyIdsSerializer() {
	}
	
	private static CompanyIdsSerializer instance = null;
	
	public static CompanyIdsSerializer getInstance() {
		if (instance == null) {
			instance = new CompanyIdsSerializer();
		}
		return instance;
	}
	
	public JSONObject getAsJson(Map.Entry<Company, List<Company>> c) {
		JSONObject jsonCompany = new JSONObject();
		
		try {
			// Profile identifier
			jsonCompany.put("id", c.getKey().getId());
			
			// Entities data
			JSONArray jsonEntities = new JSONArray();
			jsonCompany.put("entities", jsonEntities);
			for (Company entity : c.getValue()) {
				jsonEntities.put(entity.getId());
			}
		} catch (JSONException jsone) {
			System.err.println(String.format("%s:\t%s",
					CompanyIdsSerializer.class, jsone.getMessage()));
		}
		
		return jsonCompany;
	}
}
