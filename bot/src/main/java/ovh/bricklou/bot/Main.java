package ovh.bricklou.bot;

public class Main {
    public static void main(String[] args) {
        Bot bot = new Bot();

        Runtime.getRuntime().addShutdownHook(new Thread(bot::shutdown));

        try {
            bot.start();
        } catch (Exception e) {
            Bot.logger().error("The bot has encountered an unexpected error: ", e);
        }
    }
}
