package org.quna.candybox.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quna.candybox.data.Thumbnail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom page parser for Candybooru.
 * Created by qtwyeuritoiy on 2016-12-07.
 */

public class CandybooruMainPageParser {
    private static final String BCB_URL = "https://www.bittersweetcandybowl.com";
    private static final String NO_RESULT = "No_Images_Foundmain";
    private static final String THUMBNAIL_CONTAINER = "thumbnailcontainer";

    private final String url;

    public CandybooruMainPageParser(String url) {
        this.url = url;
    }

    public List<Thumbnail> getThumbnails(int index) throws IOException {
        ArrayList<Thumbnail> thumbnailList = new ArrayList<Thumbnail>();
        String url = this.url + Integer.toString(index);
        Document doc = Jsoup.connect(url).timeout(0).get();
        Element page = doc.body();
        if (page.getElementById(NO_RESULT) != null) //No Result
            return thumbnailList;

        Elements thumbnails = page.getElementsByClass(THUMBNAIL_CONTAINER);
        //Parse post link(href) then store.
        for (Element thumbnail : thumbnails) {
            Element link = thumbnail.getElementsByTag("a").first();
            String pageLink = BCB_URL + link.attr("href");
            if (pageLink.contains("?"))
                pageLink = pageLink.split("?")[0];
            Element thumbnailElement = link.getElementsByTag("img").first();
            String imageLink = BCB_URL + thumbnailElement.attr("src");
            String alt = thumbnailElement.attr("alt");

            int width = Integer.parseInt(thumbnailElement.attr("width"));
            int height = Integer.parseInt(thumbnailElement.attr("height"));
            Thumbnail thumbnail1 = new Thumbnail(imageLink, pageLink, alt, width, height);
            thumbnailList.add(thumbnail1);
        }
        return thumbnailList;
    }
}
