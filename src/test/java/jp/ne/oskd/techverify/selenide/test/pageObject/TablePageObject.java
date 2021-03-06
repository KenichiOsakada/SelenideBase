package jp.ne.oskd.techverify.selenide.test.pageObject;

import com.codeborne.selenide.SelenideElement;
import jp.ne.oskd.techverify.selenide.SelenideBasePageObject;
import jp.ne.oskd.techverify.selenide.test.pageObject.elementContainer.Table;
import lombok.Data;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

@Data
public class TablePageObject extends SelenideBasePageObject {

    @FindBy(how = How.TAG_NAME, using = "table")
    private Table table;

    @FindBy(how = How.NAME,using = "hiddenItem")
    private SelenideElement hiddenItem;
}
