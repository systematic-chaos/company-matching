/**
 * Matching company entities with company profiles
 * @author Javier Fernández-Bravo Peñuela
 * 
 * tech.company.matching.Company.java
 */

package tech.company.matching;

public class Company {
	
	private long id;
	private String companyName;
	private String websiteUrl;
	private Short foundationYear;
	private String city;
	private String country;
	
	public Company(long id) {
		setId(id);
	}
	
	public Company(long id, String companyName, String websiteUrl,
			Short foundationYear, String city, String country) {
		this(id);
		setCompanyName(companyName);
		setWebsiteUrl(websiteUrl);
		setFoundationYear(foundationYear);
		setCity(city);
		setCountry(country);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public Short getFoundationYear() {
		return foundationYear;
	}
	
	public void setFoundationYear(Short foundationYear) {
		this.foundationYear = foundationYear;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Company && ((Company) obj).getId() == this.getId();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
}
