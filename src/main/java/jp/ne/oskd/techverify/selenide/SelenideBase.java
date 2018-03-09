package jp.ne.oskd.techverify.selenide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class SelenideBase {

    WebDriver driver;

    String dowloadPath;
    public File getLatestDownloadFile() throws InterruptedException {
        final File downloadDir = new File(dowloadPath);

        WebDriverWait waitForUpload = new WebDriverWait(driver,1000);
        File latestFile = null;

        waitForUpload.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver _driver) {

                File[] downLoaededFiles = downloadDir.listFiles();
                Arrays.sort(downLoaededFiles, new Comparator<File>() {
                    @Override
                    public int compare(File file1, File file2) {
                        return file1.lastModified() <= file2.lastModified() ? 1 : -1;
                    }
                });
                boolean downLoaded = false;
                if(downLoaededFiles.length == 0){
                    return false;
                }

                File latestFile =downLoaededFiles[0];
                if(!latestFile.getName().endsWith(".crdownload") && !latestFile.getName().endsWith(".tmp")){
                    return true;
                }
                return false;
            }
        });

        File[] downLoaededFiles = downloadDir.listFiles();
        Arrays.sort(downLoaededFiles, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.lastModified() <= file2.lastModified() ? 1 : -1;
            }
        });

        return downLoaededFiles[0];
    }
}
