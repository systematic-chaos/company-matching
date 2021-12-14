package tech.company.matching.serializers;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import tech.company.matching.Company;

public interface CompanySerializer {
	JSONObject getAsJson(Map.Entry<Company, List<Company>> c);
}
