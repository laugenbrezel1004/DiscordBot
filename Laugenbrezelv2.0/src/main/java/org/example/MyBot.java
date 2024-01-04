
package org.example;

import bsh.EvalError;
import bsh.Interpreter;
import com.jcraft.jsch.JSchException;
import com.mashape.unirest.http.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;


// Weitere notwendige Importe für deine Klasse
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.List;

import java.awt.desktop.AppForegroundEvent;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Objects;

public class MyBot extends ListenerAdapter {

    private static SSHConnector connector;
    private static boolean isVerbindungDa = false;

    public static void main(String[] args) throws InterruptedException, JSchException, IOException {
        Message.suppressContentIntentWarning();
        JDABuilder builder = JDABuilder.createDefault("MTE5MDc2NDUyNDk5NTg3ODk3Mg.GZYbhy.BWZqWDM7TRhHBWsgm2fh4pywSFYwnpFucjNwpE");
        builder.addEventListeners(new MyBot());
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA jda = builder.build().awaitReady();
        Guild guild = jda.getGuildById(774668646123438121L);
        assert guild != null;
        guild.upsertCommand("test", "IchBinEinTest").queue();
        guild.upsertCommand("startminecraftserver", "Hier kannst du den Minecraftserver zum Leben erwecken!").queue();
        guild.upsertCommand("killminecraftserver", "Hier kannst du den Minecraftserver umbrigen!").queue();
        guild.upsertCommand(Commands.slash("minecraftserver", "Hier kannst du Befehle übergeben").addOption(OptionType.STRING,"befehl", "Ich bin cool")).queue();
        guild.upsertCommand(Commands.slash("wetter", "Hier kannst du das aktuelle Wetter sehen.").addOption(OptionType.STRING,"stadt", "Von welcher Stadt möchtest du die Wetterinformationen erfahren?")).queue();

        connector = new SSHConnector("192.168.178.92", 5678, "minecraft", "tfarcenim");
        try {
            connector.connect();
            isVerbindungDa = true;

        } catch (Exception exception){
            //event.reply("Du hast den Testbefehl ausgeführt!").setEphemeral(false).queue();
            isVerbindungDa = false;

        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isVerbindungDa){
            event.reply("Linux-Server ist offline!").setEphemeral(false).queue();
        }

        if (event.getName().equalsIgnoreCase("TEST")) {
            event.reply("Du hast den Testbefehl ausgeführt!").setEphemeral(false).queue();
        }
        if (event.getName().equalsIgnoreCase("STARTMINECRAFTSERVER")) {
            try {
                connector.execute("java -jar /home/minecraft/paper-1.20.4-364.jar nogui");
                event.reply("Der Minecraftserver hat das Licht der Welt erblickt!").setEphemeral(false).queue();
            } catch (JSchException | IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        if (event.getName().equalsIgnoreCase("KILLMINECRAFTSERVER")) {
            try {
                connector.execute("stop"); //!!!!!!!!!!!
                event.reply("Der Minecraftserver hat den Löffel abgegeben!").setEphemeral(false).queue();
            } catch (JSchException | IOException exception) {
                event.reply("Es ist ein Fehler aufgetreten").setEphemeral(false).queue();
                throw new RuntimeException(exception);
            }
            // connector.disconnect();
        }
        if (event.getName().equalsIgnoreCase("minecraftserver")) {
            System.err.println("Ich bin hier");
            String command = Objects.requireNonNull(event.getOption("befehl")).getAsString();
            try {
                String execute = connector.execute(command);//!!!!!!!!!!!
                event.reply(execute).setEphemeral(false).queue();
            } catch (JSchException | IOException exception) {
                event.reply("Es ist ein Fehler aufgetreten").setEphemeral(false).queue();
                throw new RuntimeException(exception);
            }
        }




        if (event.getName().equalsIgnoreCase("wetter")){
            Gson gson = new Gson();

            String city = Objects.requireNonNull(event.getOption("stadt")).getAsString();
            try {
                String apiKey = "984ba0f3806e4de3b1a0a6064b638920";
                String apiUrl = "https://api.weatherbit.io/v2.0/current?city=" + city + "&key=" + apiKey;

                //JsonNode response = Unirest.get(apiUrl).header("accept", "application/json").asJson().getBody();
                //String jsonString = response.toString();
                //für vollständigen, aber blöd sortierten daten dem link "apiUrl" folgen
               String jsonString = "{\"count\":1,\"data\":[{\"app_temp\":7.8,\"aqi\":9,\"city_name\":\"Berlin\",\"clouds\":97,\"country_code\":\"DE\",\"datetime\":\"2023-12-31:14\",\"dewpt\":3.9,\"dhi\":39.52,\"dni\":342.99,\"elev_angle\":5.84,\"ghi\":64.82,\"gust\":7.72,\"h_angle\":67.5,\"lat\":52.52437,\"lon\":13.41053,\"ob_time\":\"2023-12-31 14:18\",\"pod\":\"d\",\"precip\":0,\"pres\":998,\"rh\":77,\"slp\":1003.5,\"snow\":0,\"solar_rad\":18.1,\"sources\":[\"analysis\",\"C6421\",\"radar\",\"satellite\"],\"state_code\":\"16\",\"station\":\"C6421\",\"sunrise\":\"07:16\",\"sunset\":\"15:01\",\"temp\":7.8,\"timezone\":\"Europe/Berlin\",\"ts\":1704032307,\"uv\":0.4184287,\"vis\":16,\"weather\":{\"code\":804,\"icon\":\"c04d\",\"description\":\"Overcast clouds\"},\"wind_cdir\":\"SSE\",\"wind_cdir_full\":\"south-southeast\",\"wind_dir\":148,\"wind_spd\":4.12}]}";
                String cleanedJson = jsonString.replaceAll("[{}\",\\[\\]]"," ");
               /* List<String> foundObjects = new ArrayList<>();
                List<String> askedObjects = List.of("sunset", "sunrise", "clouds", "wind_spd", "city_name","datetime", "descroption","uv","temp");
                String[] parts = cleanedJson.split(" : ");
                for ( String part : parts){
                    if(part.equalsIgnoreCase("1")){
                        foundObjects.add(part);
                    }
                }
                String test = foundObjects.toString();
                event.reply(test).setEphemeral(false).queue();
                */
                WeatherData weatherData = gson.fromJson(jsonString, WeatherData.class);
                String temperature = String.valueOf(weatherData.data.get(0).temp);
                String sonnenaufgang = String.valueOf(weatherData.data.get(0).sunrise);
                String sonnenuntergang = String.valueOf(weatherData.data.get(0).sunset);
                String wolken    = String.valueOf(weatherData.data.get(0).clouds);
                String windgeschwindigkeit = String.valueOf(weatherData.data.get(0).wind_spd);
                String cityname = String.valueOf(weatherData.data.get(0).city_name);
                String datum = String.valueOf(weatherData.data.get(0).datetime);
                String beschreibung = String.valueOf(weatherData.data.get(0).weather);
                String uv = String.valueOf(weatherData.data.get(0).uv);
                //get user
                StringBuilder botschaft = new StringBuilder();
                botschaft.append("In der Stadt ").append(cityname).append(" beginnt ein neuer Tag mit einem atemberaubenden Sonnenaufgang um ").append(sonnenaufgang)
                        .append(" und endet mit einem malerischen Sonnenuntergang um ").append(sonnenaufgang).append(". Die Himmelsschicht zeigt ").append(wolken)
                        .append("% Wolken, was auf eine ").append(beschreibung).append(" Szenerie hindeutet. Die aktuelle Temperatur beträgt ").append(temperature)
                        .append(" Grad Celsius, was ein ").append(beschreibung).append(" Wetter beschreibt.\n\n").append("Der Wind weht mit einer Geschwindigkeit von ")
                        .append(windgeschwindigkeit).append(" km/h, wodurch eine angenehme Brise entsteht. Trotz einiger Wolken ist die UV-Strahlung mit einem Wert von ")
                        .append(uv).append(" auf einem moderaten Niveau, aber es wird empfohlen, angemessene Sonnenschutzmaßnahmen zu ergreifen.\n\n")
                        .append("Der aktuelle Zeitpunkt ist ").append(datum).append(", und es scheint, als wäre es ein perfekter Tag, um die Schönheit der Natur zu genießen.");





                System.out.println(botschaft);



                event.reply(String.valueOf(botschaft)).setEphemeral(false).queue();







           // System.out.print("Test");
            }catch (Exception e) {
                event.reply("Error 40 discovered!").setEphemeral(false).queue();
            }


        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return; // Ignoriert Nachrichten von anderen Bots

        if (event.getMessage().getContentRaw().equals("ping")) {
            event.getChannel().sendMessage("pong").queue();
        }


       /* if (event.getMessage().getContentRaw().startsWith("#eval"))
        {
            Interpreter interpreter = new Interpreter();
            try {
                interpreter.set("event", event);
                interpreter.eval(event.getMessage().getContentRaw().replace("#eval",""));
                //#eval event.getChannel().sendMessage(System.getProperty("user.name")).submit();
            } catch ( EvalError e) {
                event.getChannel().sendMessage("Befehl gib es nicht:\n " + e.getMessage()).queue();            }
        }
hier interagiere ich mit meinem system -> nicht mit dem linux server über ssh
*/



        if (event.getMessage().getContentRaw().equals("nein") || event.getMessage().getContentRaw().equals("Nein")) {
            event.getChannel().sendMessage("Doch").submit();
        }
       /* if (event.getMessage().equals("loadMinecraftServer")) {
return;
        }
        */



    }
    public class WeatherData {
        List<Data> data;
        int count;
    }

    public class Data {
        String sunrise;
        String pod;
        double pres;
        List<String> sources;
        String ob_time;
        String timezone;
        String wind_cdir;
        double lon;
        int clouds;
        double wind_spd;
        String city_name;
        String datetime;
        double h_angle;
        double precip;
        String station;
        Weather weather;
        double elev_angle;
        double dni;
        double lat;
        double uv;
        double vis;
        double temp;
        double dhi;
        double app_temp;
        double ghi;
        double dewpt;
        int wind_dir;
        double solar_rad;
        String country_code;
        double rh;
        double slp;
        double snow;
        String sunset;
        int aqi;
        String state_code;
        String wind_cdir_full;
        double gust;
        long ts;
    }

    public class Weather {
        int code;
        String icon;
        String description;


    }

}


