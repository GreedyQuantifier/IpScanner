package org.example.service;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.example.util.Utils;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {


    private static final Pattern PATTERN_DOMAIN = Pattern.compile("https?://[0-9A-Za-z.-]+");
    private final ExecutorService executorService;

    private File file;

    private final SyncWriter writer;

    public Scanner(int countThread) {
        executorService = Executors.newFixedThreadPool(countThread);
        File name = Utils.getName();
        writer = new SyncWriter(name);
        file = name;
    }

    private static List<String> domainFind(String ip, List<Certificate> certificates) {
        if (certificates.isEmpty()) return null;

        List<String> strings = new ArrayList<>();
        strings.add("Certs on IP (" + ip + ") have domains:");

        for (Certificate certificate : certificates) {
            Matcher matcher = PATTERN_DOMAIN.matcher(certificate.toString());
            while (matcher.find()) {
                strings.add(matcher.group());
            }
        }

        if (strings.size() == 1) strings.add("Not found");

        return strings;
    }

    public String findRange(String ip) {

        IpFabric.build(ip).list().forEach(s -> executorService.submit(() -> {
            List<String> strings = domainFind(s, scan(s));
            if (strings != null) writer.writeToFile(strings);
        }));

        executorService.shutdown();

        return file.getName();
    }

    private List<Certificate> scan(String serverUri) {
        List<Certificate> certificates = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.custom().disableCookieManagement().disableAutomaticRetries().evictExpiredConnections().setSSLContext(SSLContexts.custom().loadTrustMaterial((chain, authType) -> {
            certificates.addAll(Arrays.asList(chain));
            return false;
        }).build()).build()) {

            HttpGet httpGet = new HttpGet(serverUri);
            httpClient.execute(httpGet);

        } catch (Exception ignore) {
//            Suppressed checked exception
        }
        return certificates;
    }


}
