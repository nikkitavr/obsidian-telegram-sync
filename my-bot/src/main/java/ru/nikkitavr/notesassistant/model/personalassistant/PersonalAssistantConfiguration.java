package ru.nikkitavr.notesassistant.model.personalassistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.nikkitavr.notesassistant.model.personalassistant.filter.MessageFilter;

@Getter
@Setter
public class PersonalAssistantConfiguration {

  private List<DistributionRule> distributionRules;

  @Getter
  @Setter
  public static class DistributionRule {
    @JsonProperty("messageFilterQuery")
    private MessageFilter messageFilter;
    private LinkedHashMap<String, OperationConfig> operations;

    /**
     * Путь файла шаблона (.md файл) относительно vaultPath.
     * В шаблоне будут указаны переменные-шаблонизаторы, которые будут доставать из сообщения Message соответвсующий контент
     * и создавать соотвествущий шаблону .md текст
     */
    private String templatePath;
    /**
     * Путь сохранения заметки, созданной по шаблону. Если файл уже есть - заметка добавляется в конец файла. Если нет - создается новый.
     * В имени пути так же могут содержатся переменные-шаблонизаторы, которые достанут из Message соответсвубщий контент.
     */
    private String notePathTemplate;
    /**
     * Путь сохранения файлов из Message. Т.к замтека - это md файл, то все файлы прикрепляются по md ссылке, а сами файлы сохраняются в указанную директорию.
     * В имени пути так же могут содержатся переменные-шаблонизаторы, которые достанут из Message соответсвубщий контент.
     */
    private String filePathTemplate;
  }
}
