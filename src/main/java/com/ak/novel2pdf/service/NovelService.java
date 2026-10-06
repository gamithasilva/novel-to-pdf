package com.ak.novel2pdf.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NovelService {

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/140.0.0.0 Safari/537.36";


    public ChapterData getChapter(String url) throws IOException {

        Document document = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(15_000)
                .get();

        // Find chapter heading
        Element heading = findChapterHeading(document);

        String chapterTitle;

        if (heading != null) {
            chapterTitle = heading.text();
        } else {
            chapterTitle = document.title();
        }

        // Find chapter content
        Element content = findChapterContent(document);

        if (content == null) {
            throw new IllegalStateException(
                    "Could not find the chapter content on this page."
            );
        }

        // Remove unwanted elements
        removeUnwantedElements(content);

        // Extract text
        StringBuilder chapterText = new StringBuilder();

        Elements paragraphs = content.select("p");

        for (Element paragraph : paragraphs) {

            String text = paragraph.text().trim();

            if (!text.isEmpty()) {
                chapterText.append(text);
                chapterText.append("\n\n");
            }
        }

        return new ChapterData(
                chapterTitle,
                chapterText.toString(),
                url
        );
    }


    private Element findChapterHeading(Document document) {

        Elements headings = document.select("h1, h2, h3");

        for (Element heading : headings) {

            String text = heading.text().trim();

            if (text.toLowerCase().contains("chapter")) {
                return heading;
            }
        }

        return null;
    }


    private Element findChapterContent(Document document) {

        Element content =
                document.select("div.reader-text").first();

        if (content != null) {
            return content;
        }

        return null;
    }


    private void removeUnwantedElements(Element content) {

        content.select(
                "script, " +
                        "style, " +
                        "noscript, " +
                        "iframe, " +
                        ".ads, " +
                        ".advertisement, " +
                        ".social-share, " +
                        ".share-buttons"
        ).remove();
    }

//    public List<String> getChapterUrls(String novelUrl) throws IOException {
//
//        Document document = Jsoup.connect(novelUrl)
//                .userAgent(USER_AGENT)
//                .referrer("https://www.google.com/")
//                .timeout(30_000)
//                .followRedirects(true)
//                .get();
//
//        System.out.println("====================================");
//        System.out.println("SEARCHING FOR CHAPTER DATA");
//        System.out.println("====================================");
//
//        // Search the HTML for interesting chapter-related information
//        String html = document.html();
//
//        String[] keywords = {
//                "chapters",
//                "chapter",
//                "chapter_id",
//                "manga_id",
//                "api",
//                "Read (537)"
//        };
//
//        for (String keyword : keywords) {
//
//            System.out.println(
//                    "Keyword [" + keyword + "] = "
//                            + html.toLowerCase()
//                            .contains(keyword.toLowerCase())
//            );
//        }
//
//        System.out.println("====================================");
//
//        // Print script sources
//        Elements scripts = document.select("script");
//
//        System.out.println(
//                "SCRIPT COUNT: " + scripts.size()
//        );
//
//        for (Element script : scripts) {
//
//            String src = script.absUrl("src");
//
//            if (!src.isEmpty()) {
//
//                System.out.println(
//                        "SCRIPT: " + src
//                );
//            }
//        }
//
//        return new ArrayList<>();
//    }


    private void inspectMangaJs() throws IOException {

        String jsUrl =
                "https://roliascan.com/content/themes/mangapeak/assets/js/manga.js";

        String js = Jsoup.connect(jsUrl)
                .userAgent(USER_AGENT)
                .timeout(30_000)
                .ignoreContentType(true)
                .execute()
                .body();

        System.out.println("====================================");
        System.out.println("MANGA.JS");
        System.out.println("====================================");

        System.out.println(js);
    }

    public List<String> getChapterUrls(String novelUrl) throws IOException {

        inspectMangaJs();

        return new ArrayList<>();
    }
    public record ChapterData(
            String title,
            String content,
            String sourceUrl
    ) {
    }
}