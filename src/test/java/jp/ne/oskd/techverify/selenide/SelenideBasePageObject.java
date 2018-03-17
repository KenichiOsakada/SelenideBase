package jp.ne.oskd.techverify.selenide;

import com.codeborne.selenide.Selenide;

public abstract class SelenideBasePageObject{


    /**
     * 起動時に指定したSelenideBase
     */
    private SelenideBase baseClass;

    /**
     * SelenideBaseを指定します
     * @param baseClass SelenideBaseを指定します
     */
    public void setBaseClass(SelenideBase baseClass) {
        this.baseClass = baseClass;
    }

    /**
     * PageObjectを取得します
     * @param pageObjectClass pageObject
     * @param <PageObjectClass> pageObject
     */
    public <PageObjectClass extends SelenideBasePageObject>  void page(Class<PageObjectClass> pageObjectClass){
        Selenide.page(pageObjectClass);
        baseClass.screenShot(pageObjectClass);
    }

    /**
     * タイトルを取得します
     * @return タイトル
     */
    public String getTitle(){
        return baseClass.getDriver().getTitle();
    }

}
