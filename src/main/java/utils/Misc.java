package utils;

import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by KhoaNguyen on 12/26/2016.
 */
public class Misc {

    public static JsonObject readJsonObjectFile(String filename) {
        BufferedReader br = null;
        JsonObject jsonObject = new JsonObject();
        try {

            try {
                br = new BufferedReader(new FileReader(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                String fullContent = sb.toString();
                jsonObject = new JsonObject(fullContent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}
