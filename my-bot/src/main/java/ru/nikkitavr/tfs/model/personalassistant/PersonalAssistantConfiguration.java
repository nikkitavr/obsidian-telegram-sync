package ru.nikkitavr.tfs.model.personalassistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.nikkitavr.tfs.model.personalassistant.filter.MessageFilter;

@Getter
@Setter
public class PersonalAssistantConfiguration {
  private String vaultPath;
  private List<DistributionRule> distributionRules;
  private GitAction gitAction;

  @Getter
  @Setter
  public static class DistributionRule {
    @JsonProperty("messageFilterQuery")
    private MessageFilter messageFilter;
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

  @Getter
  @Setter
  public static class GitAction {
    //TODO: добавить конфиг для того, чтобы на его оснвании можно было делать git commit / git push. Те при обработки сообщения, если gitcAction != null -
    // 1) делаем git pull перед изменениями 2) делаем коммит и git push после изменений. Считаем, что репозиторий там же, где vaultPath.
    // Если git репозитория нет или возникла какая-то ошибка (мердж конфликт например) - логируем error.
  }
}
