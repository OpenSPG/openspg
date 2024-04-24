package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Any;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class DefaultThinkerTests {
  private GraphState<IVertexId> buildGraphState() {
    GraphState<IVertexId> graphState = new MemGraphState();

    Vertex<IVertexId, IProperty> vertex1 =
        new Vertex<>(IVertexId.from("a1", "A"), new VertexVersionProperty());
    Vertex<IVertexId, IProperty> vertex2 =
        new Vertex<>(IVertexId.from("a2", "A"), new VertexVersionProperty());
    Vertex<IVertexId, IProperty> vertex3 =
        new Vertex<>(IVertexId.from("b", "B"), new VertexVersionProperty());
    Vertex<IVertexId, IProperty> vertex4 =
        new Vertex<>(IVertexId.from("c", "C"), new VertexVersionProperty("k1", "abcd"));

    Edge a1b =
        new Edge(
            IVertexId.from("a1", "A"),
            IVertexId.from("b", "B"),
            new VertexVersionProperty(),
            0L,
            Direction.OUT,
            "ab");
    Edge a2b =
        new Edge(
            IVertexId.from("a2", "A"),
            IVertexId.from("b", "B"),
            new VertexVersionProperty(),
            0L,
            Direction.OUT,
            "ab");
    Edge bc =
        new Edge(
            IVertexId.from("b", "B"),
            IVertexId.from("c", "C"),
            new VertexVersionProperty(),
            0L,
            Direction.OUT,
            "bc");

    graphState.addVertex(vertex1);
    graphState.addEdges(vertex1.getId(), new LinkedList<>(), Arrays.asList(a1b));
    graphState.addVertex(vertex2);
    graphState.addEdges(vertex2.getId(), new LinkedList<>(), Arrays.asList(a2b));

    graphState.addVertex(vertex3);
    graphState.addEdges(
        vertex3.getId(), Arrays.asList(a1b.reverse(), a2b.reverse()), Arrays.asList(bc));

    graphState.addVertex(vertex4);
    graphState.addEdges(vertex2.getId(), Arrays.asList(bc.reverse()), new LinkedList<>());

    return graphState;
  }

  @Test
  public void testFindForward() {
    Thinker thinker = new DefaultThinker(buildGraphState());
    List<Element> triples =
        thinker.find(new Entity<>(IVertexId.from("a1", "A"), "A"), new Any(), new Any()).getData();
    Assert.assertTrue(triples.size() == 1);
  }

  @Test
  public void testBackForward() {
    Thinker thinker = new DefaultThinker(buildGraphState());
    List<Element> triples =
        thinker.find(new Any(), new Any(), new Entity<>(IVertexId.from("b", "B"), "B")).getData();
    Assert.assertTrue(triples.size() == 2);
  }
}
