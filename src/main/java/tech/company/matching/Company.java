package tech.company.matching;

public class Company {
	
	private long id;
	private String companyName;
	private String websiteUrl;
	private Short foundationYear;
	private String city;
	private String country;
	
	public Company(long id, String companyName, String websiteUrl,
			Short foundationYear, String city, String country) {
		setId(id);
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
	
}
