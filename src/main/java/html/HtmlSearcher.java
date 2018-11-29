package html;

import html.button.IButtonChecker;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class HtmlSearcher {

    private Document htmlDocument;

    {
        htmlDocument = null;
    }

    public HtmlSearcher(Document htmlDocument){
        this.htmlDocument = htmlDocument;
    }

    public Optional<Element> getElementById(String elementId){
        try {
            return Optional.of(htmlDocument.getElementById(elementId));
        }catch (NullPointerException e){
            System.out.println("No element with targetId in target document");
            return Optional.empty();
        }
    }

    public List<IndexedTag> buildPathFromCurrentToBody(Element element){
        Element bodyElementInInitDocument = htmlDocument.body();

        List<IndexedTag> path = new ArrayList<>();

        while (!element.equals(bodyElementInInitDocument)) {
            Element parent = element.parent();
            int index = 0;
            for(int i = 0; i < parent.children().size(); ++i){
                if(parent.children().get(i).equals(element)){
                    break;
                }
                if(parent.children().get(i).nodeName().equals(element.nodeName())){
                    ++index;
                }
            }

            path.add(new IndexedTag(element, index));
            element = parent;

        }
        return path;
    }

    public void printPath(List<IndexedTag> path){
        int pathLength = path.size();

        for(int i = 0; i < pathLength - 1; ++i){
            Element element = path.get(i).getElement();
            int index = path.get(i).getIndex();

            System.out.print(element.nodeName() + "[" + index + "] > ");
        }

        if(pathLength > 0){
            Element element = path.get(pathLength - 1).getElement();
            int index = path.get(pathLength - 1).getIndex();

            System.out.println(element.nodeName() + "[" + index + "]");
        }
    }

    public List<IndexedTag> findElementByPathPattern(List<IndexedTag> pattern, IButtonChecker buttonChecker){
        return new DFS(pattern, htmlDocument.body(), buttonChecker).searchPath();
    }

    private class DFS{
        private List<IndexedTag> patternPath;
        private List<IndexedTag> foundPath;
        private Boolean isFound;
        private Element root;
        private IButtonChecker buttonChecker;

        public DFS(List<IndexedTag> patternPath, Element root, IButtonChecker buttonChecker){
            this.patternPath = patternPath;
            this.isFound = false;
            this.root = root;
            this.buttonChecker = buttonChecker;
        }

        public List<IndexedTag> searchPath(){
            foundPath = new Stack<>();

            dfs(root, 0, 0);
            return foundPath;
        }

        private void dfs(Element current, int depth, int hierarchyIndex){

            if (isFound) return ;

            foundPath.add(new IndexedTag(current, hierarchyIndex));

            // Try to find on same or smaller depth
            if (depth < patternPath.size()){
                IndexedTag el = patternPath.get(patternPath.size() - 1 - depth);

                // Try to find on same hierarchy index
                int sameTagHierarchyIndex = 0;
                for(int i = 0; i < current.children().size(); ++i){
                    Element cur = current.children().get(i);
                    if(cur.nodeName().equals(el.getElement().nodeName())){
                        if(sameTagHierarchyIndex == el.getIndex()){
                            dfs(cur, depth + 1, sameTagHierarchyIndex);
                        }
                        ++sameTagHierarchyIndex;
                    }
                }

                // Try to find on other hierarchy index
                sameTagHierarchyIndex = 0;
                for(int i = 0; i < current.children().size(); ++i){
                    Element cur = current.children().get(i);
                    if(cur.nodeName().equals(el.getElement().nodeName())){
                        dfs(cur, depth + 1, sameTagHierarchyIndex);
                        ++sameTagHierarchyIndex;
                    }
                }
            }else{
                // Try to find deeper If some tags were added
                for(int i = 0; i < current.children().size(); ++i) {
                    Element cur = current.children().get(i);
                    dfs(cur, depth + 1, i);
                }
            }

            if (buttonChecker.isCurrentOkButton(current) && !isFound){
                isFound = true;
            }

            if(!isFound){
                foundPath.remove(foundPath.size() - 1);
            }
        }
    }
}
