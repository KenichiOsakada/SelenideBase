package jp.ne.oskd.techverify.selenide.test.pageObject.elementContainer;

import com.codeborne.selenide.ElementsContainer;
import lombok.Data;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

/**
 * "table"に相応するSelenideElement
 * <p>
 * "th"要素と
 * "td"の一覧を保持する
 */
@Data
public class Table extends ElementsContainer {
    /**
     * TableHeader
     */
    @FindBy(how = How.TAG_NAME, using = "tr")
    @Getter
    private List<TableRow> tableRows;
}
