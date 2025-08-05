package com.github.sbcharr.concurrency.webpageindexer;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IndexerWithExplicitLockApp {
    private Deque<Weblink> queue = new ArrayDeque<>();
    private static final Object lock = new Object();
    private List<Thread> downloaderThreadList = new ArrayList<>();
    private List<Thread> indexerThreadList = new ArrayList<>();


    private static class Weblink {
        private long id;
        private String title;
        private String url;
        private String host;
        private volatile String htmlPage;

        public Weblink(long id, String title, String url, String host) {
            this.id = id;
            this.title = title;
            this.url = url;
            this.host = host;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getHost() {
            return host;
        }

        public String getHtmlPage() {
            return htmlPage;
        }

        public void setHtmlPage(String htmlPage) {
            this.htmlPage = htmlPage;
        }
    }

    private static class Downloader implements Runnable {
        private Weblink weblink;

        public Downloader(Weblink weblink) {
            this.weblink = weblink;
        }

        public void run() {
            try {
                synchronized (lock) {
                    String htmlPage = HttpConnect.download(weblink.getUrl());
                    weblink.setHtmlPage(htmlPage);
                    lock.notifyAll();
                }
            } catch (MalformedURLException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class Indexer implements Runnable {
        private Weblink weblink;
        private Lock lock;
        private Condition pageCondition;

        private Indexer(Weblink weblink, Lock lock, Condition pageCondition) {
            this.weblink = weblink;
            this.lock = lock;
            this.pageCondition = pageCondition;
        }

        public void run() {
            String htmlPage = weblink.getHtmlPage();
            synchronized (lock) {
                while (htmlPage == null) {
                    try {
                        System.out.println(weblink.getId() + " not yet downloaded!");
                        lock.wait();
                        System.out.println(weblink.getId() + " active now!");
                        htmlPage = weblink.getHtmlPage();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                index(htmlPage);
            }
        }

        private void index(String text) {
            if (text != null) {
                System.out.println("Indexed: " + weblink.getId() + "\n");
            }
        }
    }


    public void go() {
        while (!queue.isEmpty()) {
            Weblink weblink = queue.pollFirst();

            Lock lock = new ReentrantLock();
            Condition pageCondition = lock.newCondition();

            Thread downloaderThread = new Thread(new Downloader(weblink));
            Thread indexerThread = new Thread(new Indexer(weblink, lock, pageCondition));

            downloaderThread.setName("Downloader thread " + weblink.getId());
            indexerThread.setName("Indexer thread " + weblink.getId());

            downloaderThreadList.add(downloaderThread);
            indexerThreadList.add(indexerThread);

            downloaderThread.start();
            indexerThread.start();
        }

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        // interrupt any downloader thread that's taking too long to finish downloading
        for (int i = 0; i < downloaderThreadList.size(); i++) {
            Thread thread = downloaderThreadList.get(i);
            if (thread.isAlive()) {
                System.out.println(thread.getName() + " is still active. Stopping ...");
                indexerThreadList.get(i).interrupt();
            }
        }
    }

    public void add(Weblink link) {
        queue.addLast(link);
    }

    public Weblink createWeblink(long id, String title, String url, String host) {
        return new Weblink(id, title, url, host);
    }

    public static void main(String[] args) {
        IndexerWithExplicitLockApp app = new IndexerWithExplicitLockApp();

        app.add(app.createWeblink(2000, "Nested Classes", "https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html", "http://docs.oracle.com"));
        app.add(app.createWeblink(2001, "Java SE Downloads", "https://www.oracle.com/technetwork/java/javase/downloads/index.html", "http://www.oracle.com"));
        app.add(app.createWeblink(2002, "Interface vs Abstract Class", "https://mindprod.com/jgloss/interfacevsabstract.html", "http://mindprod.com"));
        app.add(app.createWeblink(2003, "Virtual Hosting and Tomcat", "https://tomcat.apache.org/tomcat-6.0-doc/virtual-hosting-howto.html", "http://tomcat.apache.org"));

        app.go();
    }
}

