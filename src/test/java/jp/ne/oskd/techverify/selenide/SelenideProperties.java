package jp.ne.oskd.techverify.selenide;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uitest")
public class SelenideProperties {

    /**
     * 起動対象ブラウザ
     */
    private String browser;

    /**
     * ドライバ
     */
    private String drivaer;


    /**
     * 実績フォルダルート
     */
    private String resultRoot;

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    /**
     * ヘッドレスモード
     */
    private boolean headless;

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDrivaer() {
        return drivaer;
    }

    public void setDrivaer(String drivaer) {
        this.drivaer = drivaer;
    }

    public String getResultRoot() {
        return resultRoot;
    }

    public void setResultRoot(String resultRoot) {
        this.resultRoot = resultRoot;
    }



}
