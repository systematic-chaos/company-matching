# company-matching

## Matching company entities with company profiles
--------------------------------------------------

This assignment consists in **analyzing** samples from two tables that provide different types of information about companies:

- `company_entities.tsv`: lists the legal company entities
- `company_profiles.tsv`: lists the company profiles

Both tables have the following structure:

| Column | Description |
| :----- | :---------- |
| `id`   | ID of the company entity or company profile. Careful: the entity IDs are different from the profile IDs, e.g. ID 4 in `company_entities` may refer to _LinkedIn_ while ID 4 in `company_profiles` may refer to _Siemens_ |
| `company_name` | name of the company |
| `website_url` | URL that may link to the company website |
| `foundation_year` | year when the company was founded |
| `city` | name of the city where the company is located |
| `country` | code of the country where the company is located |

It is assumed that there is a 1:n relationship between company profiles and company entities, i.e. one company profile may refer to zero or more company entities.

1. Design an algorithm that allows for matching company entities and company profiles.
2. Use the `groud_truth.tsv` file to validate the algorithm.
3. Provide a small, basic REST API to wrap the matching algorithm. It should return the matched companies given company profile data.
