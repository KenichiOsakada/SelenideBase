package jp.ne.oskd.techverify.selenide.test.pageObject.elementContainer;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ColumnData extends ElementsContainer {

    @Getter
    @FindBy(how = How.TAG_NAME, using = "a")
    private SelenideElement aTag;

    @Getter
    @FindBy(how = How.TAG_NAME, using = "input")
    private SelenideElement inputTag;
}
