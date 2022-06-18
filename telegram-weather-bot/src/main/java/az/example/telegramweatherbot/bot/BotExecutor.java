package az.example.telegramweatherbot.bot;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.Connection;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;


@Service
public class BotExecutor extends BotProperties {

    public static String rightPad(String string, int length) {
        return String.format("%-" + length + "s", string);
    }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage message = new SendMessage();

        String cmd = update.getMessage().getText();


        final WebClient webClient = new WebClient();

        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage page1 = null;
        try {
            page1 = webClient.getPage("https://www.accuweather.com/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final HtmlForm form = (HtmlForm) page1.getElementsByTagName("form").get(0);
        HtmlElement button = (HtmlElement) page1.createElement("button");
        button.setAttribute("type", "submit");
        form.appendChild(button);
        final HtmlTextInput textField = form.getInputByName("query");
        textField.setValueAttribute(cmd);
        HtmlPage page2 = null;
        try {
            page2 = button.click();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HtmlPage page3 = null;
        Connection conn;
        Document doc = null;
        DomElement elm0 = null;
        for (DomElement domElement0 : page2.getElementsByTagName("div")) {
            elm0 = domElement0;
            if (elm0.getAttribute("class").equals("locations-list content-module")) {
                conn = Jsoup.connect(String.valueOf(page2.getUrl()));
                try {
                    doc = conn.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String res = "";
                for (int i = 1; i < elm0.getChildElementCount() + 1; i++) {
                    res = res + doc.selectXpath("/html/body/div/div[4]/div[1]/div[1]/div[2]/a[" + i + "]").text() + "\n";

                }
                message.setText("Search results list. Where do you exactly look for?" + "\n" + "\n" + res);
                message.setChatId(String.valueOf(update.getMessage().getChatId()));

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                webClient.close();
                break;
            }

        }
        DomElement elm1 = null;
        for (DomElement domElement : page2.getElementsByTagName("a").subList(25, 45)) {
            elm1 = domElement;
            if ((elm1.getAttribute("data-qa").equals("daily"))) {
                try {
                    page3 = elm1.click();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                HtmlPage page = page3;
                //Connecting to the web page
                conn = Jsoup.connect(String.valueOf(page.getUrl()));
                //executing the get request
                try {
                    doc = conn.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String result = " ";
                for (int i = 0; i < 7; i++) {
                    result = result + doc.select("h2.date").eq(i).text() + rightPad(" ", 10) +
                            doc.select("div.temp").eq(i).text().replace("/", "/ ") + rightPad(" ", 10) +
                            doc.select("div.phrase").eq(i).text() + rightPad(" ", 10) +
                            doc.select("div.precip").eq(i).text() + "\n";
                }
                message.setText(result);
                message.setChatId(String.valueOf(update.getMessage().getChatId()));

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                webClient.close();
                break;
            }
        }
        if (!(elm1.getAttribute("data-qa").equals("daily")) && !elm0.getAttribute("class").equals("locations-list content-module")) {
            message.setText("No results found");
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            webClient.close();
        }
    }
}
