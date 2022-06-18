package az.example.telegramweatherbot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public abstract class BotProperties extends TelegramLongPollingBot {

    private static final String botUserName = "my_bot";
    private static final String botToken = "5293390082:AAHZehBZpbkB2QnC-QqDNZyj4Vl84dD3bBA";

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {};
}
