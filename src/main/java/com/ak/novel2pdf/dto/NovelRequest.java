package com.ak.novel2pdf.dto;

import java.util.List;

public class NovelRequest {

    private String novelTitle;

    private List<String> chapterUrls;

    public NovelRequest() {
    }

    public NovelRequest(
            String novelTitle,
            List<String> chapterUrls
    ) {
        this.novelTitle = novelTitle;
        this.chapterUrls = chapterUrls;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }

    public List<String> getChapterUrls() {
        return chapterUrls;
    }

    public void setChapterUrls(
            List<String> chapterUrls
    ) {
        this.chapterUrls = chapterUrls;
    }
}