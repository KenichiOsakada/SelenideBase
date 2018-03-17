package jp.ne.oskd.techverify.selenide.test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jp.ne.oskd.techverify.selenide.SelenideBase;
import jp.ne.oskd.techverify.selenide.test.pageObject.ItemspageObject;
import org.junit.Rule;
import org.junit.Test;

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
                        .withHeader("content","text/html").withHeader("title","Items")
                        .withStatus(200)
                        .withBody("<table>" +
                                "<tr>" +
                                "<th>Header1</th>" +
                                "</tr>" +
                                "<tr>" +
                                "<td>Data1</td>" +
                                "</tr>" +
                                "<tr>" +
                                "<td>Data2</td>" +
                                "</tr>" +
                                "</table>"))

        );

        ItemspageObject itemsPage = open("/items", ItemspageObject.class);
        List<String> ret = itemsPage.getItemNames();
        assertEquals("ItemCount", 2, ret.size());
    }
}
