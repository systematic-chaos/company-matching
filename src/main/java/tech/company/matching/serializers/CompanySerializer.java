/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.serializers.CompanySerializer.java
 */

package tech.company.matching.serializers;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import tech.company.matching.Company;

public interface CompanySerializer {
	JSONObject getAsJson(Map.Entry<Company, List<Company>> c);
}
