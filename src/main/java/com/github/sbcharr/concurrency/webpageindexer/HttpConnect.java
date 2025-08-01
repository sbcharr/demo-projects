package com.github.sbcharr.concurrency.webpageindexer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.*;

public class HttpConnect {
    public static @Nullable String download(String sourceUrl) throws URISyntaxException, MalformedURLException {
        System.out.println("downloading -> " + sourceUrl);
        URL url = new URI(sourceUrl).toURL();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                return IOUtil.read(connection.getInputStream());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
