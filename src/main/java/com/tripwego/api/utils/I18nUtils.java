package com.tripwego.api.utils;

import java.util.Locale;

/**
 * Created by JG on 25/07/17.
 */
public class I18nUtils {

    public static final String DEFAULT_LANGUAGE_CODE = "en";

    public static String findCountryName(String language, String countryCode) {
        final Locale country = new Locale("", countryCode);
        return country.getDisplayCountry(new Locale(language));
    }

    public static String findDefaultCountryName(String countryCode) {
        return findCountryName(DEFAULT_LANGUAGE_CODE, countryCode);
    }
}
