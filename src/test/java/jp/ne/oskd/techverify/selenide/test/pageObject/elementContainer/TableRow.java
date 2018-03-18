package jp.ne.oskd.techverify.selenide.test.pageObject.elementContainer;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import lombok.Data;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

@Data
public class TableRow extends ElementsContainer {
    /**
     * TableHeader
     */
    @Getter
    @FindBy(how = How.TAG_NAME, using = "th")
    private ElementsCollection headers;

    /**
     * TableData
     */
    @Getter
    @FindBy(how = How.TAG_NAME, using = "td")
    private List<ColumnData> datas;

}
