package org.miage.isiForm.model.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Language {
    static {
        languages = new ArrayList<>();
        FRENCH = new Language("fr");
        //ENGLISH = new Language("en");
    }

    public static final Language FRENCH;
    //public static final Language ENGLISH;

    public static Language getDefault() {
        return FRENCH;
    }

    private final static List<String> languages;
    public static List<String> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    public final String value;
    private Language(String value) {
        this.value = value;
        if(!languages.contains(value))
            languages.add(value);
    }
}
