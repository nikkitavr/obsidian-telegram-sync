package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParser {
    private static final Pattern REPLACE_PARAMS_PATTERN = Pattern.compile("\\{\\{replace:([^=]+)=>([^}]*)}}", Pattern.CASE_INSENSITIVE);
    
    public List<Token> parse(String template, TemplateUnitScope scope) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < template.length()) {
            int start = template.indexOf("{{", pos);
            if (start < 0) {
                tokens.add(new LiteralToken(template.substring(pos)));
                break;
            }
            if (start > pos) {
                tokens.add(new LiteralToken(template.substring(pos, start)));
            }
            int end = template.indexOf("}}", start);
            if (end < 0) {
                tokens.add(new LiteralToken(template.substring(start)));
                break;
            }
            String expr = template.substring(start + 2, end).trim();
            // ReplaceUnit
            if (expr.startsWith("replace:")) {
                Map<String, String> params = parseReplaceParams(expr);
                tokens.add(new TemplateUnitToken(new ReplaceUnit(), params));
            } else {
                NoteUnit nunit = NoteUnit.match(expr);
                if (nunit != null && scope == TemplateUnitScope.NOTE_BODY) {
                    Map<String, String> params = NoteUnit.parseParams(expr);
                    tokens.add(new TemplateUnitToken(nunit, params));
                } else {
                    GenericUnit gunit = GenericUnit.match(expr);
                    if (gunit != null && (scope == TemplateUnitScope.GENERIC || scope == TemplateUnitScope.NOTE_BODY)) {
                        Map<String, String> params = GenericUnit.parseParams(expr);
                        tokens.add(new TemplateUnitToken(gunit, params));
                    } else {
                        throw new UnknownTemplateUnitException(expr);
                    }
                }
            }
            pos = end + 2;
        }
        return tokens;
    }
    
    private Map<String, String> parseReplaceParams(String expr) {
        Map<String, String> params = new HashMap<>();
        Matcher matcher = REPLACE_PARAMS_PATTERN.matcher("{{" + expr + "}}");
        if (matcher.find()) {
            params.put("from", matcher.group(1));
            params.put("to", matcher.group(2));
        }
        return params;
    }
} 