package jp.ne.oskd.techverify.selenide.test;

import com.codeborne.selenide.SelenideElement;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jp.ne.oskd.techverify.selenide.SelenideBase;
import jp.ne.oskd.techverify.selenide.test.pageObject.TablePageObject;
import jp.ne.oskd.techverify.selenide.test.pageObject.elementContainer.TableRow;
import org.junit.Rule;
import org.junit.Test;

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
                        .withBody("<table>" +
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
                                "</table>"))

        );

        TablePageObject itemsPage = open("/items", TablePageObject.class);
        List<TableRow> rows= itemsPage.getTable().getTableRows();

        assertEquals("RowsCount",rows.size(),5);

        TableRow headerRow = rows.get(0);
        assertEquals("TH size",1,headerRow.getHeaders().size());
        assertEquals("TH Text","Header1",headerRow.getHeaders().get(0).getText());

        TableRow messageRow = rows.get(1);
        assertEquals("TD size",1,messageRow.getDatas().size());
        assertEquals("TD Text","Data1",messageRow.getDatas().get(0).getSelf().getText());

        TableRow hrefRow = rows.get(2);
        assertEquals("TD size",1,hrefRow.getDatas().size());
        assertEquals("TD Text","Data2",hrefRow.getDatas().get(0).getSelf().getText());
        assertHefUrl("href","/users",hrefRow.getDatas().get(0).getATag().getAttribute("href"));

        TableRow inputRow = rows.get(3);
        assertEquals("TD size",1,inputRow.getDatas().size());
        assertEquals("TD Text","Input:",inputRow.getDatas().get(0).getSelf().getText());
        SelenideElement inputTag = inputRow.getDatas().get(0).getInputTag();
        inputTag.setValue("aaaaa");
        assertEquals("InputTagValue","aaaaa",inputTag.getValue());
    }
}
