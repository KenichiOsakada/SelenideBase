package jp.ne.oskd.techverify.selenide.test;

import com.codeborne.selenide.SelenideElement;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jp.ne.oskd.techverify.selenide.SelenideBase;
import jp.ne.oskd.techverify.selenide.test.pageObject.TablePageObject;
import jp.ne.oskd.techverify.selenide.test.pageObject.elementContainer.TableRow;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class SelenideBaseTest extends SelenideBase {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    public void testList() throws Exception {
        stubFor(get(
                urlEqualTo("/items"))
                .willReturn(aResponse()
                        .withHeader("content", "text/html").withHeader("title", "Items")
                        .withStatus(200)
//                        .withBodyFile(getClass().getResource("/jp/ne/oskd/techverify/selenide/test/SelenideBaseTest/testList/ListHtlm.html").getPath()))
                        .withBody("<script type='text/javascript'>" +
                                "function handleDownload(id) {" +
                                "var content = 'ABCDEFGHIJKLMN';" +
                                "var blob = new Blob([ content ], { \"type\" : \"text/plain\" });" +
                                "if (window.navigator.msSaveBlob) {" +
                                "window.navigator.msSaveBlob(blob, \"test.txt\");" +
                                "window.navigator.msSaveOrOpenBlob(blob, \"test.txt\");" +
                                "} else {" +
                                "document.getElementById(id).href = window.URL.createObjectURL(blob);" +
                                "}" +
                                "}" +
                                "</script>" +
                                "<table>" +
                                "<tr>" +
                                "<th>Header1</th>" +
                                "</tr>" +
                                "<tr>" +
                                "<td>Data1</td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td><a href=\"/users\">Data2</a></td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td>Input:<input type=\"text\" name=\"NAME\"></td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td><input type=\"file\" name=\"FILE\"></td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td><a id=\"download\" href=\"#\" download=\"test.txt\" onclick=\"handleDownload('download')\">DownLoad</a></td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td><a id=\"download2\" href=\"#\" download=\"test2.txt\" onclick=\"handleDownload('download2')\">DownLoad</a></td>" +
                                "</tr>" +
                                "</table>" +
                                "<input type='hidden' name='hiddenItem' value='HiddenValue' />"))

        );

        TablePageObject itemsPage = open("/items", TablePageObject.class);
        List<TableRow> rows = itemsPage.getTable().getTableRows();

        assertEquals("RowsCount", rows.size(), 7);

        TableRow headerRow = rows.get(0);
        assertEquals("TH size", 1, headerRow.getHeaders().size());
        assertEquals("TH Text", "Header1", headerRow.getHeaders().get(0).getText());

        TableRow messageRow = rows.get(1);
        assertEquals("TD size", 1, messageRow.getDatas().size());
        assertEquals("TD Text", "Data1", messageRow.getDatas().get(0).getSelf().getText());

        TableRow hrefRow = rows.get(2);
        assertEquals("TD size", 1, hrefRow.getDatas().size());
        assertEquals("TD Text", "Data2", hrefRow.getDatas().get(0).getSelf().getText());
        assertHefUrl("href", "/users", hrefRow.getDatas().get(0).getATag().getAttribute("href"));

        TableRow inputRow = rows.get(3);
        assertEquals("TD size", 1, inputRow.getDatas().size());
        assertEquals("TD Text", "Input:", inputRow.getDatas().get(0).getSelf().getText());
        SelenideElement inputTag = inputRow.getDatas().get(0).getInputTag();
        inputTag.setValue("aaaaa");
        assertEquals("InputTagValue", "aaaaa", inputTag.getValue());

        TableRow fileUpRow = rows.get(4);
        assertEquals("TD size", 1, fileUpRow.getDatas().size());
        SelenideElement fileUpTag = fileUpRow.getDatas().get(0).getInputTag();
        fileUpTag.uploadFile(new File(getClass().getResource("/jp/ne/oskd/techverify/selenide/test/SelenideBaseTest/testList/TestUpFile.txt").getPath()));

        TableRow fileDownRow1 = rows.get(5);
        assertEquals("TD size", 1, fileDownRow1.getDatas().size());
        fileDownRow1.getDatas().get(0).getATag().click();
        File down1 = getLatestDownloadFile();
        assertEquals("FileNm1","test.txt",down1.getName());

        TableRow fileDownRow2 = rows.get(6);
        assertEquals("TD size", 1, fileDownRow1.getDatas().size());
        fileDownRow2.getDatas().get(0).getATag().click();
        File down2 = getLatestDownloadFile();
        assertEquals("FileNm2","test2.txt",down2.getName());

        assertEquals("HiddenItem","HiddenValue",itemsPage.getHiddenItem().getAttribute("value"));
//        itemsPage.getHiddenItem().setValue("SetValue");
        setHiddenValueByName("hiddenItem","SetValue");
        assertEquals("HiddenItem","SetValue",itemsPage.getHiddenItem().getAttribute("value"));
    }
}
