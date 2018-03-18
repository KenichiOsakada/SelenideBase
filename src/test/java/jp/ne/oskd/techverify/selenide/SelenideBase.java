package jp.ne.oskd.techverify.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.TextReport;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import io.netty.util.internal.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Selenideで検証を行うためのBaseクラス
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public abstract class SelenideBase {

    /**
     * ファイルの種別
     */
    protected enum ResultFileTypeEnum {
        SecreenShot("ScreenShot"), DownloadFile("DownLoad");

        private String folderNm;

        ResultFileTypeEnum(String folderNm) {
            this.folderNm = folderNm;
        }
    }

    /**
     * ファイル名称
     */
    private Map<ResultFileTypeEnum, Map<String, Integer>> mapFileNmBase = new HashMap<ResultFileTypeEnum, Map<String, Integer>>() {
        {
            put(ResultFileTypeEnum.SecreenShot, new HashMap<String, Integer>());
            put(ResultFileTypeEnum.DownloadFile, new HashMap<String, Integer>());
        }
    };

    @Autowired
    private SelenideProperties properties;

    @Rule
    public TestRule report = new TextReport();

    /**
     * メソッド名取得のためのルール
     */
    @Rule
    public TestName testName = new TestName();


    /**
     * 　WebDriver
     */
    private WebDriver driver;

    /**
     * 実績格納フォルダルート
     */
    private String rootDir;

    /**
     * 実績ファイル数
     */
    private int resultFileCount;

    /**
     * Test実施前のBeforeメソッド
     * WebDriverの設定と実績格納フォルダの作成を行います
     */
    @Before
    public void before() throws Exception {
        initResultFolder();
        setWebDriver();
        setListeners();
    }

    @After
    public void after() {
        driver.close();
    }

    /**
     * WebDriverを設定します
     */
    private void setWebDriver() {
        if (WebDriverRunner.FIREFOX.equals(properties.getBrowser())) {
            setFireFox();
        } else if (WebDriverRunner.CHROME.equals(properties.getBrowser())) {
            setChrome();
        } else {
            throw new RuntimeException("Browser Setting Wrong Value = " + properties.getBrowser());
        }

        WebDriverRunner.setWebDriver(driver);
        //以下共通的な設定群
        Configuration.fastSetValue = true;
        //スクリーンショットの設定ディレクトリ設定
        Configuration.reportsFolder = getScreenShotFolder();

        Configuration.baseUrl = properties.getBaseUrl();
    }

    /**
     * FireFox用の設定を行います
     */
    private void setFireFox() {
//        Configuration.browser = WebDriverRunner.FIREFOX;
//        // プロファイルの作成
//        FirefoxProfile profile = new FirefoxProfile();
//        // ダウンロードするファイルの保存先フォルダを指定
//        // ダウンロードフォルダ
//        profile.setPreference("browser.download.dir", getFileDownloadPath());
//        // ★ここが大事★
//        // 指定したmimeタイプは有無を言わさずダウンロードする
//        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
//                "text/html, text/plain, application/vnd.ms-excel, text/csv, application/zip, text/comma-separated-values, application/octet-stream");
//        // ダウンロードダイアログを見せないようにする
//        profile.setPreference("browser.download.manager.showWhenStarting", false);
//
//        driver = new FirefoxDriver(profile);
    }

    /**
     * Chromeの設定を行います
     */
    private void setChrome() {
        Configuration.browser = WebDriverRunner.CHROME;
        //自動ダウンロードされない？？？
        System.setProperty("webdriver.chrome.driver", getClass().getResource(properties.getChromeDriver()).getPath());

        ChromeOptions chromeOptions = new ChromeOptions();
        //HeadLessモード指定
        if (properties.isHeadless()) {
            chromeOptions.addArguments("--headless");
        }
        Map<String, Object> chromePrefs = new HashMap<>();
        //PopUp表示を抑制
        chromePrefs.put("profile.default_content_settings.popups", 0);
        //ダウンロードフォルダ指定
        chromePrefs.put("download.default_directory", getFileDownloadPath());
        //ダウンロード先指定ダイアログ表示抑制
        chromePrefs.put("download.prompt_for_download", false);
        chromeOptions.setExperimentalOption("prefs", chromePrefs);
        driver = new ChromeDriver(chromeOptions);
    }

    /**
     * 実績格納フォルダを初期化します
     */
    private void initResultFolder() throws Exception {

        String rootPath;
        rootPath = properties.getResultRoot();

        //未指定の場合はTargetFolder配下に作成
        if (StringUtil.isNullOrEmpty(rootPath)) {
            rootPath = Paths.get("target/UITest").toAbsolutePath().toString();
        }

        //フォルダの相対パスを取得
        StringBuffer sb = new StringBuffer(rootPath);
        sb.append(File.separator);
        sb.append(getResultFolderRelativePath());
        sb.append(File.separator);
        sb.append(testName.getMethodName());

        rootDir = sb.toString();
        //ルートディレクトリを初期化
        recursiveDeleteFile(new File(rootDir));

        //各子フォルダの作成
        makeDirs(getFileDownloadPath());
        makeDirs(getScreenShotFolder());
    }


    /**
     * フォルダを作成します
     *
     * @param folderPath 作成対象フォルダ
     * @throws IOException フォルダ作成
     */
    private void makeDirs(String folderPath) throws IOException {
        //ルートディレクトリを作成
        if (!new File(folderPath).mkdirs()) {
            throw new IOException(String.format("Result Folder Create Error Path = %s", rootDir));
        }
    }

    /**
     * 対象のファイルオブジェクトの削除を行う.<BR>
     * ディレクトリの場合は再帰処理を行い、削除する。
     *
     * @param delTargetFile ファイルオブジェクト
     */
    private static void recursiveDeleteFile(File delTargetFile) {

        // 存在しない場合は処理終了
        if (!delTargetFile.exists()) {
            return;
        }
        // 対象がディレクトリの場合は再帰処理
        if (delTargetFile.isDirectory() && delTargetFile.listFiles() != null) {
            for (File child : delTargetFile.listFiles()) {
                recursiveDeleteFile(child);
            }
        }
        // 対象がファイルもしくは配下が空のディレクトリの場合は削除する
        delTargetFile.delete();
    }

    /**
     * 実績格納フォルダの相対パスを指定します
     * 未指定の場合はパッケージ名を含むクラス名で作成
     *
     * @return 実績格納フォルダの相対パス
     */
    private String getResultFolderRelativePath() {
        String fileSepalator = File.separator;
        if (fileSepalator.equals("\\")) {
            fileSepalator = "\\\\";
        }

        return getClass().getName().replaceAll("\\.", fileSepalator);
    }

    /**
     * スクリーンショット格納フォルダパスを返却します
     *
     * @return スクリーンショット格納フォルダパス
     */
    private String getScreenShotFolder() {
        return new StringBuffer(rootDir).append(File.separator).append(ResultFileTypeEnum.SecreenShot.folderNm).toString();
    }


    /**
     * ダウンロードファイル格納パスを返却します
     *
     * @return ダウンロードファイル格納パス
     */
    protected String getFileDownloadPath() {
        return new StringBuffer(rootDir).append(File.separator).append(ResultFileTypeEnum.DownloadFile.folderNm).toString();
    }

    /**
     * 指定ページを開きます
     *
     * @param url               起動URL
     * @param pageObjectClass   SelenideBasePageObject
     * @param <PageObjectClass> SelenideBasePageObject
     * @return 起動ページを詰めたPageObject
     */
    public <PageObjectClass extends SelenideBasePageObject> PageObjectClass open(String url, Class<PageObjectClass> pageObjectClass) {

        PageObjectClass pageObject = Selenide.open(url, pageObjectClass);

        screenShot(pageObjectClass);
        pageObject.setBaseClass(this);
        return pageObject;
    }

    /**
     * スクリーンショットを取得します
     *
     * @param pageObject 対象pageObjectClass
     */
    protected <PageObjectClass extends SelenideBasePageObject> void screenShot(Class<PageObjectClass> pageObject) {
        String fileNm = createFileNm(ResultFileTypeEnum.SecreenShot, pageObject.getSimpleName());
        Selenide.screenshot(fileNm);
    }

    /**
     * ファイル名を作成して返却します
     *
     * @param type   実績ファイルタイプ
     * @param baseNm ファイル名のもととなる名称
     * @return 作成ファイル名
     */
    String createFileNm(ResultFileTypeEnum type, String baseNm) {
        Map<String, Integer> mapNameIndex = mapFileNmBase.get(type);

        int nameIndex = 0;
        if (mapNameIndex.containsKey(baseNm)) {
            nameIndex = mapNameIndex.get(baseNm);
        }
        nameIndex++;
        mapNameIndex.put(baseNm, nameIndex);

        return String.format("%s-%s", baseNm, nameIndex);
    }

    /**
     * 最新のダウンロードファイルを取得します
     *
     * @return 最新のダウンロードファイル
     */
    public File getLatestDownloadFile() {
        final File downloadDir = new File(getFileDownloadPath());

        WebDriverWait waitForUpload = new WebDriverWait(driver, 1000);

        waitForUpload.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver _driver) {
                //ファイル名からダウンロード中か否かを判定
                return isDownloadFinish(getLatestFile(downloadDir));
            }
        });
        File retFile = getLatestFile(downloadDir);

        if (isDownloadFinish(retFile)) {
            return retFile;
        }
        return null;
    }

    /**
     * ダウンロード中か否かを判定します
     *
     * @param target 対象ファイル
     * @return ダウンロード完了か否か
     */
    private static boolean isDownloadFinish(File target) {
        return target != null && !target.getName().endsWith(".crdownload") && !target.getName().endsWith(".tmp");
    }

    /**
     * 最新ファイルを取得します
     *
     * @param targetDir ダウンロードディレクトリ
     * @return 最新ダウンロードファイル
     */
    private File getLatestFile(File targetDir) {
        File[] downLoaededFiles = targetDir.listFiles();
        if (downLoaededFiles == null || downLoaededFiles.length == resultFileCount) {
            return null;
        }

        Arrays.sort(downLoaededFiles, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.lastModified() <= file2.lastModified() ? 1 : -1;
            }
        });

        return downLoaededFiles[0];
    }

    /**
     * 実績ファイル数を設定します
     *
     * @param resultFileCount 実績ファイル数を設定します
     */
    void setResultFileCount(int resultFileCount) {
        this.resultFileCount = resultFileCount;
    }

    /**
     * WebDriverListenerを設定します
     */
    private void setListeners() {
        WebDriverRunner.addListener(createWebEventLisner());
    }

    /**
     * WebDriverListenerImplを作成/返却します
     *
     * @return WebDriverListenerImpl
     */
    protected WebDriverListenerImpl createWebEventLisner() {
        return new WebDriverListenerImpl(this);
    }


    public WebDriver getDriver() {
        return driver;
    }


    /**
     * RootUrlの相対パスの確認を行います
     *
     * @param message  エラー時メッセージ
     * @param expected 想定
     */
    public void assertCurrentUrl(String message, String expected) {
        assertEquals(message, properties.getResultRoot() + expected, driver.getCurrentUrl());
    }

    /**
     * Hrf属性のURL確認を行います。
     * <br>
     * Href属性は、相対パスで記載していても絶対パスに変換されるため、当メソッドで確認する
     *
     * @param message   エラー時メッセージ
     * @param expected  想定URL
     * @param actualUrl 実URL
     */
    public void assertHefUrl(String message, String expected, String actualUrl) {
        assertEquals(message, properties.getBaseUrl() + expected, actualUrl);
    }
}
