package jp.ne.oskd.techverify.selenide;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "selenide.setting")
public class SelenideProperties {

    /**
     * 起動対象ブラウザ
     */
    private String browser;

    /**
     * ドライバ
     */
    private String driver;


    /**
     * 実績フォルダルート
     */
    private String resultRoot;

    /**
     * ヘッドレスモード
     */
    private boolean headless;


    /**
     * 接続URLのルート
     */


    /**
     *
     */
    private String chromeDriver;

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }


    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getResultRoot() {
        return resultRoot;
    }

    public void setResultRoot(String resultRoot) {
        this.resultRoot = resultRoot;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String baseUrl;

    public String getChromeDriver() {
        return chromeDriver;
    }

    public void setChromeDriver(String chromeDriver) {
        this.chromeDriver = chromeDriver;
    }
}
