package tech.company.matching;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Company {

    private int id;
    private String name;
    private double revenue;
    private String ceo;

    public Company(int id, String name, double revenue, String ceo) {
        this.setId(id);
        this.setName(name);
        this.setRevenue(revenue);
        this.setCeo(ceo);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRevenue() {
        return this.revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public String getCeo() {
        return this.ceo;
    }

    public void setCeo(String ceo) {
        this.ceo = ceo;
    }

    public static void main(String[] args) {
        List<Company> companies = new ArrayList<>(5);
        companies.add(new Company(42, "Xing SE", 200, "Thomas Vollmoeller"));
        companies.add(new Company(222, "Zalando SE", 1234.3, "Mr. Samwer"));
        companies.add(new Company(345, "Bosch AG", 1234.5, "Ms X"));
        companies.add(new Company(345, "BMW AG", 0.23, "Mr Y"));
        companies.add(new Company(404, "Lost Inc", 0.23, "Dr. No"));

        Map<BigDecimal, Integer> revenues = revenueOccurrences(companies);
        revenues.entrySet().forEach(entry ->
            System.out.println(String.format("%s\t%d", entry.getKey().toPlainString(), entry.getValue())));
        System.out.println();

        Map<Long, Integer> rangeRevenues = revenueRangeOccurrences(companies);
        rangeRevenues.entrySet().forEach(entry ->
            System.out.println(String.format("%d\t%s", entry.getKey(), entry.getValue())));
    }

    public static Map<BigDecimal, Integer> revenueOccurrences(List<Company> companies) {
        final Map<BigDecimal, Integer> revenues = new HashMap<>();

        companies
            .stream()
            .map(c -> BigDecimal.valueOf(c.getRevenue()))
            .forEach(r -> {
                Integer count = revenues.get(r);
                revenues.put(r, count == null ? 1 : count + 1);
            });

        return revenues;
    }

    public static Map<Long, Integer> revenueRangeOccurrences(List<Company> companies) {
        final long interval = 200l;
        final Map<Long, Integer> rangeRevenues = new HashMap<>();

        companies
            .stream()
            .map(c -> Double.valueOf(Math.floor(c.getRevenue() / interval)).longValue())
            .forEach(r -> {
                Integer count = rangeRevenues.get(r);
                rangeRevenues.put(r, count == null ? 1 : count + 1);
            });

        return rangeRevenues;
    }
}
