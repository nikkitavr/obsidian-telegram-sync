package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public class ReplaceUnit implements TemplateUnit {
    private static final Pattern REPLACE_PATTERN = Pattern.compile("\\{\\{replace:([^=]+)=>([^}]*)}}", Pattern.CASE_INSENSITIVE);

    @Override
    public Pattern pattern() { return REPLACE_PATTERN; }

    @Override
    public String apply(Message m, Map<String, String> params) {
        // Не используется напрямую, применяется к итоговой строке
        throw new UnsupportedOperationException();
    }

    public String postProcess(String input) {
        // Сначала удаляем все replace-юниты и собираем замены
        StringBuilder sb = new StringBuilder();
        int last = 0;
        Matcher matcher = REPLACE_PATTERN.matcher(input);
        while (matcher.find()) {
            sb.append(input, last, matcher.start());
            last = matcher.end();
        }
        sb.append(input.substring(last));
        String result = sb.toString();
        
        // Теперь применяем замены
        matcher = REPLACE_PATTERN.matcher(input);
        while (matcher.find()) {
            String from = matcher.group(1);
            String to = matcher.group(2);
            result = result.replace(from, to);
        }
        return result;
    }
} 