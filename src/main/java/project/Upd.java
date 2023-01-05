package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// set collor build.gradle -> implementation group: 'com.diogonunes', name: 'JCDP', version: '2.0.3.1'
import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

public class Upd {
    public static void main(String[] args) throws IOException {
        ColoredPrinter printer = new ColoredPrinter
                .Builder(1, false).build();

        printer.println("Hello, colorful world!",
                Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.BLACK);
        new Finder();
    }

    private static class Finder {
        Finder() throws IOException {
            Scanner sc = new Scanner(System.in);
            final int CONNECTION_TIMEOUT = 15_000; // ??? 15 sec
            System.out.print("Введите УНП: ");
            String unp = sc.nextLine(); //В процессе ликвидации 192168617, mts 800013732

            String urlVsUNP = String.format("https://www.portal.nalog.gov.by/grp/getData?unp=%s&charset=UTF-8&type=JSON", unp);
            final URL url = new URL(urlVsUNP);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(CONNECTION_TIMEOUT);

            try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                final StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                // parse to map
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Row>>(){}.getType();
                Map<String, Row> myMap = gson.fromJson(String.valueOf(content), type);
                System.out.println("Статус преприятия: " + myMap.get("ROW").VKODS);
                System.out.println("Для завершения нажмите ввод");
                String line = sc.nextLine();
                sc.close();
                // parse to object
               /* Gson g = new Gson();
                Obj unit = g.fromJson(String.valueOf(content), Obj.class);

                ColoredPrinter printer = new ColoredPrinter
                        .Builder(1, false).build();

                printer.println("Статус преприятия: " + unit.getROW().VKODS,
                        Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.BLACK);

                System.out.println();

                */

            } catch (final Exception ex) {
                System.out.print(ex.getCause() == null ? "УНП не найден" : ex.getClass() );
            }
        }
    }

    class Obj {
        Row ROW;

        public Row getROW() {
            return ROW;
        }

        public void setROW(Row row) {
            this.ROW = row;
        }
    }

    private static class Row {

        public String VUNP; // – УНП плательщика;
        public String VNAIMP; // – полное наименование плательщика;
        public String VNAIMK; // – краткое наименование плательщика;
        public String DREG; // – дата постановки на учет;
        public String NMNS; // – код инспекции МНС;
        public String VMNS; // – наименование инспекции МНС;
        public String CKODSOST; // – код состояния плательщика;
        public String VKODS; // – наименование плательщика;
        public String DLIKV; // – дата изменения состояния плательщика;
        public String VLIKV; // – основание изменения состояния плательщика;
    }
}
