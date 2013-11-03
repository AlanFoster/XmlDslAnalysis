package foo.graph;

import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Transformer;

/**
 * This class is dedicated to help glue Scala and Java code together.
 * There are particular scenarios where Scala can not interact with Java's poor type system.
 * As such this class provides a means of gluing the two languages together
 */
public class GraphGlue {
    /**
     * Creates a new MinimumSpanningForest
     *
     * @param graph The Graph of V, E
     * @param <V> The generic type of the Graph's Verticies
     * @param <E> The generic type of the Graph's edges
     * @return A new MinimumSpanningForest2 implementation
     */
    public static <V, E> MinimumSpanningForest2<V, E> newMinimumSpanningForest(Graph<V, E> graph) {
        MinimumSpanningForest2<V, E> minimumSpanningForest =
                new MinimumSpanningForest2<V, E>(graph,
                        new DelegateForest<V, E>(), DelegateTree.<V, E>getFactory(),
                        new Transformer<E, Double>() {
                            public Double transform(E e) {
                                return 1d;
                            }
                        });
        return minimumSpanningForest;
    }
}
