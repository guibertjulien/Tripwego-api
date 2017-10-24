package com.tripwego.api;

import java.util.Locale;

/**
 * Created by JG on 25/07/17.
 */
public class I18nUtils {

    public static String findCountryName(String language, String countryCode) {
        final Locale country = new Locale("", countryCode);
        return country.getDisplayCountry(new Locale(language));
    }

    public static String findEnglishCountryName(String countryCode) {
        return findCountryName("en", countryCode);
    }
}
