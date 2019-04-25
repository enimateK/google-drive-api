package org.miage.isiForm.google.docs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilWord {
    private static final String ANYTHING       = "[^\\{:}]*";

    private static final String KEYWORD_S      = "{{";
    private static final String R_KEYWORD_S    = "\\{\\{";
    private static final String KEYWORD_E      = "}}";
    private static final String R_KEYWORD_E    = "}}";

    private static final String VAR_S          = KEYWORD_S   + "var:";
    private static final String R_VAR_S        = R_KEYWORD_S + "var:";
    private static final String R_VAR          = R_VAR_S + ANYTHING + R_KEYWORD_E;

    private static final String LOOP_START_S   = KEYWORD_S   + "start:";
    private static final String R_LOOP_START_S = R_KEYWORD_S + "start:";
    private static final String R_LOOP_START   = R_LOOP_START_S + ANYTHING + R_KEYWORD_E;
    private static final String LOOP_END_S     = KEYWORD_S   + "end:";
    private static final String R_LOOP_END_S   = R_KEYWORD_S + "end:";
    private static final String R_LOOP_END     = R_LOOP_END_S + ANYTHING + R_KEYWORD_E;

    private static final String TABLE_LOOP_S   = KEYWORD_S   + "row:";
    private static final String R_TABLE_LOOP_S = R_KEYWORD_S + "row:";
    private static final String R_TABLE_LOOP   = R_TABLE_LOOP_S + ANYTHING + R_KEYWORD_E;

    private static final String TABLE_ELEM_S   = KEYWORD_S   + "elem:";
    private static final String R_TABLE_ELEM_S = R_KEYWORD_S + "elem:";
    private static final String R_TABLE_ELEM   = R_TABLE_ELEM_S + ANYTHING + R_KEYWORD_E;

    public static final int VAR        = 1;
    public static final int LOOP_START = 2;
    public static final int LOOP_END   = 3;
    public static final int TABLE_LOOP = 4;
    public static final int TABLE_ELEM = 5;

    private UtilWord() { }

    private static Matcher getMatcher(String string, int REGEX_ID) {
        Pattern pattern = Pattern.compile(getFullRegex(REGEX_ID));
        return pattern.matcher(string);
    }

    private static String getFullRegex(int REGEX_ID) {
        switch(REGEX_ID) {
            case VAR:
                return R_VAR;
            case LOOP_START:
                return R_LOOP_START;
            case LOOP_END:
                return R_LOOP_END;
            case TABLE_LOOP:
                return R_TABLE_LOOP;
            case TABLE_ELEM:
                return R_TABLE_ELEM;
            default:
                return "";
        }
    }

    private static String getStartRegex(int REGEX_ID) {
        switch(REGEX_ID) {
            case VAR:
                return R_VAR_S;
            case LOOP_START:
                return R_LOOP_START_S;
            case LOOP_END:
                return R_LOOP_END_S;
            case TABLE_LOOP:
                return R_TABLE_LOOP_S;
            case TABLE_ELEM:
                return R_TABLE_ELEM_S;
            default:
                return "";
        }
    }

    private static String getEndRegex(int REGEX_ID) {
        switch(REGEX_ID) {
            case VAR:
            case LOOP_START:
            case LOOP_END:
            case TABLE_LOOP:
            case TABLE_ELEM:
                return R_KEYWORD_E;
            default:
                return "";
        }
    }

    private static String getTrimStart(int REGEX_ID) {
        switch(REGEX_ID) {
            case VAR:
                return VAR_S;
            case LOOP_START:
                return LOOP_START_S;
            case LOOP_END:
                return LOOP_END_S;
            case TABLE_LOOP:
                return TABLE_LOOP_S;
            case TABLE_ELEM:
                return TABLE_ELEM_S;
            default:
                return "";
        }
    }

    private static String getTrimEnd(int REGEX_ID) {
        switch(REGEX_ID) {
            case VAR:
            case LOOP_START:
            case LOOP_END:
            case TABLE_LOOP:
            case TABLE_ELEM:
                return KEYWORD_E;
            default:
                return "";
        }
    }

    private static String trim(String string, int REGEX_ID) {
        String trimStart = getTrimStart(REGEX_ID);
        String trimEnd   = getTrimEnd(REGEX_ID);
        if(string.startsWith(trimStart))
            string = string.substring(trimStart.length());
        if(string.endsWith(trimEnd))
            string = string.substring(0, string.length() - trimEnd.length());
        return string;
    }

    public static boolean canFind(String stringToSearch, int REGEX_ID) {
        return getMatcher(stringToSearch, REGEX_ID).find();
    }

    public static String findFirst(String stringToSearch, int REGEX_ID) {
        Matcher matcher = getMatcher(stringToSearch, REGEX_ID);
        if(matcher.find())
            return trim(matcher.group(), REGEX_ID);
        return "";
    }

    public static Collection<String> findAll(String stringToSearch, int REGEX_ID) {
        List<String> results = new ArrayList<>();
        Matcher matcher = getMatcher(stringToSearch, REGEX_ID);
        int start = 0;
        while(matcher.find(start)) {
            String result = trim(matcher.group(), REGEX_ID);
            results.add(result);
            start += result.length();
        }
        return results;
    }

    public static String replace(String string, int REGEX_ID, String replaceKey, String replaceVal) {
        replaceVal = replaceVal.replaceAll("\\$", "\\\\\\$");
        replaceVal = replaceVal.replaceAll("\\n", "\n\r");
        replaceKey = replaceKey.replaceAll("\\|", "\\\\\\|");
        replaceKey = replaceKey.replaceAll("\\.", "\\\\\\.");
        return string.replaceAll(getStartRegex(REGEX_ID) + replaceKey + getEndRegex(REGEX_ID), replaceVal);
    }

    public static String getFullVar(String var) {
        return var.split("\\|")[0];
    }

    public static String getVarSheet(String var) {
        String fullVar = getFullVar(var);
        if(fullVar.contains("."))
            return fullVar.split("\\.")[0];
        return "";
    }

    public static String getVar(String var) {
        String fullVar = getFullVar(var);
        if(fullVar.contains("."))
            return fullVar.split("\\.")[1];
        return fullVar;
    }

    public static boolean isClause(String var) {
        return var.contains("|");
    }

    public static String getFullClause(String var) {
        if(!isClause(var))
            return "";
        return var.split("\\|")[1];
    }

    public static String getVarClause(String var) {
        if(!isClause(var))
            return "";
        return getFullClause(var).split("=")[0];
    }

    public static String getVarSheetClause(String var) {
        String varClause = getVarClause(var);
        if(varClause.contains("."))
            return varClause.split("\\.")[0];
        return "";
    }

    public static String getVarVarClause(String var) {
        String varClause = getVarClause(var);
        if(varClause.contains("."))
            return varClause.split("\\.")[1];
        return varClause;
    }

    public static String getValueClause(String var) {
        if(!isClause(var))
            return "";
        return getFullClause(var).split("=")[1];
    }

    public static void writeToFile(InputStream stream, String outputFile) throws IOException {
        OutputStream output = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
    }
}
