package jp.ne.oskd.techverify.selenide.test.pageObject;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import jp.ne.oskd.techverify.selenide.SelenideBasePageObject;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.ArrayList;
import java.util.List;

public class ItemspageObject extends SelenideBasePageObject {

    @FindBy(tagName = "items")
    private ElementsCollection items;

    public List<String> getItemNames(){
        List<String> retList = new ArrayList<>();
        for(SelenideElement item:items){
            retList.add(item.$("item").getValue());
        }

        return retList;
    }
}
