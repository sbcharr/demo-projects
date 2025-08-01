package com.github.sbcharr.concurrency.webpageindexer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IOUtil {
    public static void read(List<String> data, String fileName) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String read(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return sb.toString();
    }

    public static void write(String webpage, long id) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("pages/" + id + ".html"), StandardCharsets.UTF_8))) {
            writer.write(webpage);
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}