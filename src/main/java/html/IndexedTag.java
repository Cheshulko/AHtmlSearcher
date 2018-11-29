package html;

import org.jsoup.nodes.Element;

public class IndexedTag {
    private Element element;
    private int index;

    public IndexedTag(Element element, int index){
        this.element = element;
        this.index = index;
    }

    public Element getElement() {
        return element;
    }

    public int getIndex() {
        return index;
    }
}
