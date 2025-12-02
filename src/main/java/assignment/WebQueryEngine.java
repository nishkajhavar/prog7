package assignment;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 */
public class WebQueryEngine {
    private WebIndex index;

    public WebQueryEngine(WebIndex index) {
        this.index = index;
    }

    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        return new WebQueryEngine(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param query A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {
        // 1. Initialize Parser
        QueryParser parser = new QueryParser(query);

        // 2. Parse the query string into a tree
        GeneralNode root = parser.parseQuery();

        // 3. Handle parsing errors (via the graceful error flag)
        if (root == null || parser.hasError()) {
            return Collections.emptyList();
        }

        // 4. Evaluate the root of the tree
        Set<Page> results = execute(root);

        // Return the results. Collection<Page> is satisfied by Set<Page>.
        return results;
    }

    private Set<Page> setIntersection(Set<Page> set1, Set<Page> set2) {
        Set<Page> output = new HashSet<>(set1);
        output.retainAll(set2);
        return output;
    }

    private Set<Page> setUnion(Set<Page> set1, Set<Page> set2) {
        Set<Page> output = new HashSet<>(set1);
        output.addAll(set2);
        return output;
    }

    private Set<Page> setDifference(Set<Page> main, Set<Page> set2) {
        Set<Page> output = new HashSet<>(main);
        output.removeAll(set2);
        return output;
    }

    private Set<Page> execute(GeneralNode node) {
        if (node instanceof WordNode) {
            String word = ((WordNode) node).getWord();
            return index.getPageSet(word);
        }
        else if (node instanceof OperationNode) {
            OperationNode operation = (OperationNode) node;
            Set<Page> left = execute(operation.getLeft());
            Set<Page> right = execute(operation.getRight());
            if (node instanceof AndNode) {
                return setIntersection(left, right);
            }
            else if (node instanceof OrNode) {
                return setUnion(left, right);
            }
        }
        else if (node instanceof NegationNode) {
            NegationNode negationNode = (NegationNode) node;
            // 1. Get the results of the child expression (which must be a WordNode)
            Set<Page> childResults = execute(negationNode.getChildNode());

            // 2. Get the universe of all pages
            Set<Page> universe = index.getAllPages();

            // 3. Return the set difference (Universe - ChildResults)
            return setDifference(universe, childResults);
        }
        return new HashSet<>();
    }
}