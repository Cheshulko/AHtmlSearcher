import html.HtmlSearcher;
import html.IndexedTag;
import html.button.IButtonChecker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

// 1: body[0] > div[0] > div[0] > div[2] > div[0] > div[0] > div[1] > a[1] >
// 2: body[0] > div[0] > div[0] > div[2] > div[1] > div[0] > div[1] > a[1] >
// 3: body[0] > div[0] > div[0] > div[2] > div[0] > div[0] > div[2] > a[0] >
// 4: body[0] > div[0] > div[0] > div[2] > div[0] > div[0] > div[2] > a[0] >

public class Main{
    private static String CHARSET_NAME = "utf8";
    private static String TARGET_ELEMENT_ID = "make-everything-ok-button";

    public static void main(String args[]){
        if (args.length < 2){
            System.out.println("Wrong input parameters");
            return;
        }

        try {
            final File initFile = new File(args[0]);
            final File targetFile = new File(args[1]);

            final Document baseDocument = Jsoup.parse(initFile, CHARSET_NAME, initFile.getAbsolutePath());
            final Document targetDocument = Jsoup.parse(targetFile, CHARSET_NAME, targetFile.getAbsolutePath());

            final HtmlSearcher htmlBaseSearcher = new HtmlSearcher(baseDocument);
            final HtmlSearcher htmlTargetSearcher = new HtmlSearcher(targetDocument);

            Optional<Element> targetElement = htmlBaseSearcher.getElementById(TARGET_ELEMENT_ID);

            targetElement.ifPresent(element -> {
                List<IndexedTag> patternPath = htmlBaseSearcher.buildPathFromCurrentToBody(element);

                List<IndexedTag> targetPath = htmlTargetSearcher.findElementByPathPattern(patternPath, new IButtonChecker() {
                    @Override
                    public Boolean isCurrentOkButton(Element element) {
                        return element.hasClass("btn") &&
                                !element.hasClass("btn-warning") &&
                                !element.hasClass("btn-danger");
                    }
                });
                htmlTargetSearcher.printPath(targetPath);
            });
        }catch (IOException e){
            System.out.println("Something went wrong");
            System.out.println(e);
        }
    }
}