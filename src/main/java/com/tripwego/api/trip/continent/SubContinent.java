package com.tripwego.api.trip.continent;

public enum SubContinent {

    /*
     * AFRICA
     */
    NORTHERN_AFRICA(Continent.AFRICA, "015", "Northern Africa"), WESTERN_AFRICA(
            Continent.AFRICA, "011", " Western Africa"), MIDDLE_AFRICA(
            Continent.AFRICA, "017", "Middle Africa"), EASTERN_AFRICA(
            Continent.AFRICA, "014", "Eastern Africa"), SOUTHERN_AFRICA(
            Continent.AFRICA, "018", "Southern Africa"),

    /*
     * EUROPE
     */
    NORTHERN_EUROPE(Continent.EUROPE, "154", "Northern Europe"), WESTERN_EUROPE(
            Continent.EUROPE, "155", "Western Europe"), EASTERN_EUROPE(
            Continent.EUROPE, "151", "Eastern Europe"), SOUTHERN_EUROPE(
            Continent.EUROPE, "039", "Southern Europe"),
    /*
     * AMERICAS
     */
    NORTHERN_AMERICA(Continent.AMERICAS, "021", "Northern America"), CARIBBEAN(
            Continent.AMERICAS, "029", "Caribbean"), CENTRAL_AMERICA(
            Continent.AMERICAS, "013", "Central America"), SOUTH_AMERICA(
            Continent.AMERICAS, "005", "South America"),
    /*
     * ASIA
     */
    CENTRAL_ASIA(Continent.ASIA, "143", "Central Asia"), EASTERN_ASIA(
            Continent.ASIA, "030", "Eastern Asia"), SOUTHERN_ASIA(
            Continent.ASIA, "034", "Southern Asia"), SOUTH_EASTERN_ASIA(
            Continent.ASIA, "035", "South-Eastern Asia"), WESTERN_ASIA(
            Continent.ASIA, "145", "Western Asia"),

    /*
     * OCEANIA
     */
    AUSTRALIA_AND_NEW_ZEALAND(Continent.OCEANIA, "053", "Australia and New Zealand"), MELANESIA(
            Continent.OCEANIA, "054", "Melanesia"), MICRONESIA(
            Continent.OCEANIA, "057", "Micronesia"), POLYNESIA(
            Continent.OCEANIA, "061", "Polynesia");

    private final Continent continent;
    private final String code;
    private final String name;

    private SubContinent(Continent continent, String code, String name) {
        this.continent = continent;
        this.code = code;
        this.name = name;
    }

    public Continent getContinent() {
        return continent;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
