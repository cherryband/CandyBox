package org.quna.candybox.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quna.candybox.data.Comment;
import org.quna.candybox.data.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by graphene on 2016-12-07.
 */

public class CandybooruImagePageParser {
    private static final String COMMENT_CONTAINER = "Commentsmain";
    private static final String DETAILS = "authordetails";
    private static final String CONTENT = "text";
    private final String url;

    public CandybooruImagePageParser(String url) {
        this.url = url;
    }

    public List<Comment> getComments() throws IOException {
        Document doc = Jsoup.connect(url).timeout(0).get();
        Element page = doc.body();

        ArrayList<Comment> datas = new ArrayList<Comment>();
        Elements commentContainer = page.getElementById(COMMENT_CONTAINER).children();
        for (Element element : commentContainer) {
            if (element.id().contains("comment")) {
                Element detail = element.getElementsByClass(DETAILS).first();
                Element content = element.getElementsByClass(CONTENT).first();

                String rawContent = content.children().html();
                Element authorElement = detail.getElementsByTag("strong").first();
                Element dateTimeElement = detail.getElementsByTag("small").first();

                String author = authorElement.getElementsByTag("a").first().text();
                String dateTime = dateTimeElement.getElementsByTag("span").first().text();

                boolean isBciMember = element.className().contains("bci");
                boolean isCreator = element.className().contains("author");
                boolean isAdmin = element.className().contains("admin");

                Comment comment = new Comment(author, rawContent, dateTime, isBciMember,
                        isCreator, isAdmin);

                datas.add(comment);
            }
        }
        return datas;
    }

    public Image getImage() throws IOException {
        Document doc = Jsoup.connect(url).timeout(0).get();
        Element page = doc.body();

        Element mainImage = page.getElementById("main_image");
        Element details = page.getElementById("Image_Detailsleft");

        String rawLink = mainImage.attr("src");
        int width = Integer.parseInt(mainImage.attr("width"));
        int height = Integer.parseInt(mainImage.attr("height"));

        String uploader = details.getElementsByTag("a").first().data();
        Element dateTimeElement = details.getElementsByAttributeValueContaining("title", "on ")
                .first();
        String dateTime = dateTimeElement.attr("title");

        Elements tags = page.getElementById("Tagged_Withleft").children();
        ArrayList<String> tagList = new ArrayList<String>();
        for (Element tag : tags) {
            if (tag.tagName().equals("a")) {
                tagList.add(tag.data());
            }
        }

        Image image = new Image(rawLink, width, height, uploader, dateTime, tagList);
        setCreatorIfExists(details, image);
        setSourceIfExists(page, image);
        setFavouritedIfExists(page, image);
        return image;
    }

    private void setFavouritedIfExists(Element page, Image image) {
        Element favourited = page.getElementById("Favourited_Byleft");

        if (favourited != null) {
            ArrayList<String> favouritedUserList = new ArrayList<String>();
            Elements favouritedUsers = favourited.children();
            for (Element user : favouritedUsers) {
                if (user.tagName().equals("a"))
                    favouritedUserList.add(user.data());
            }
            image.setFavourited(favouritedUserList);
        }
    }

    private void setSourceIfExists(Element page, Image image) {
        Elements sourceLink = page.getElementsByClass("sourceimage");
        if (!sourceLink.isEmpty()) {
            Element link = sourceLink.first().getElementsByTag("a").first();
            image.setSourceLink(link.attr("href"));
        }
    }

    private void setCreatorIfExists(Element details, Image image) {
        Elements creatorElement = details.getElementsByClass("artist");
        if (!creatorElement.isEmpty()) {
            String creator = creatorElement.first().data();
            image.setCreator(creator);
        }
    }
}
