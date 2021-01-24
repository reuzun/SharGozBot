package ceng.estu.main;

import ceng.estu.utilities.Command;
import ceng.estu.utilities.LavaPlayerAudioProvider;
import ceng.estu.utilities.TrackScheduler;
import ceng.estu.webhandle.WebHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import reactor.core.scheduler.Schedulers;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author reuzun
 */
public class SharGozBot {

    protected static final Map<String, Command> commands = new HashMap<>();
    protected static String SYSTEM_PREFIX_PROPERTY = "!";

    static {
        commands.put("ping", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Your ping is calculated as 49ms.").block());
        commands.put("deniz senin kardeşin", event -> event.getMessage()
                .getChannel().block()
                .createMessage("baba yalan söylüyorsun bu olamaz nayır.").block());
    }

    public static void main(String[] args) {

        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize. It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();
        // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);

        CommandHandler.initializeCommands(provider, playerManager, player); //Adjusts the commands from a class in the same package

        EventDispatcher customDispatcher = EventDispatcher.builder()
                .eventScheduler(Schedulers.boundedElastic())
                .build();

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
                .gateway().setEventDispatcher(customDispatcher)
                .login()
                .block();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    final String content = event.getMessage().getContent(); // 3.1 Message.getContent() is a String
                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        // We will be using ! as our "prefix" to any command in the system.
                        if (content.startsWith(SYSTEM_PREFIX_PROPERTY + entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        } else if (content.contains("${")) {
                            //String str = content.substring(content.lastIndexOf("$") + 2, content.lastIndexOf("}")); //1444/12
                            String strToParse = content;

                            String regex = "\\$\\{.*?}";

                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(strToParse);

                            StringBuilder sb = new StringBuilder();
                            sb.append(":\n");
                            int holder = 0;
                            while(matcher.find()) {
                                String str = strToParse.substring(matcher.start(), matcher.end()).replace("${","").replace("}","");
                                //event.getMessage().getChannel().block().createMessage(":\n"+ (holder++) + ". value : " + String.valueOf(eval(str))).block();
                                sb.append((holder++) + ". value : " + eval(str) + "\n");


                                /*
                                //basic idea. can be improved.
                                if (str.contains("*")) {
                                    //double res = Double.parseDouble(str.substring(0, str.lastIndexOf("*"))) * Double.parseDouble(str.substring(str.lastIndexOf("*") + 1, str.length()));
                                    event.getMessage().getChannel().block().createMessage(String.valueOf(result)).block();
                                } else if (str.contains("/")) {
                                    //double res = Double.parseDouble(str.substring(0, str.lastIndexOf("/"))) / Double.parseDouble(str.substring(str.lastIndexOf("/") + 1, str.length()));
                                    event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                } else if (str.contains("+")) {
                                    //double res = Double.parseDouble(str.substring(0, str.lastIndexOf("+"))) + Double.parseDouble(str.substring(str.lastIndexOf("+") + 1, str.length()));
                                    event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                } else if (str.contains("-")) {
                                    //double res = Double.parseDouble(str.substring(0, str.lastIndexOf("-"))) - Double.parseDouble(str.substring(str.lastIndexOf("-") + 1, str.length()));
                                    event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                } else if (str.contains("^")) {
                                    //double res = Math.pow(Double.parseDouble(str.substring(0, str.lastIndexOf("^"))), Double.parseDouble(str.substring(str.lastIndexOf("^") + 1, str.length())));
                                    event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                }
                                */

                            }
                            event.getMessage().getChannel().block().createMessage(sb.toString()).block();
                            break;
                        }
                    }
                });


        client.onDisconnect().block();
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }


}
