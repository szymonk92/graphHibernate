package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.bugs.entity.Graph;
import org.hibernate.bugs.entity.Node;
import org.hibernate.bugs.entity.NodeContent;
import org.hibernate.bugs.entity.nocascade.GraphNoCascade;
import org.hibernate.bugs.entity.nocascade.NodeNoCascade;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    // Entities are auto-discovered, so just add them anywhere on class-path
    // Add your tests, using standard JUnit.

    static int startNodesNo = 3;
    static int depth = 1500;

    /**
     * N  N  N
     * \ | /
     * N
     * |
     * N
     * |
     * ...
     * <p>
     * <p>
     * Set -Xss6M to make it pass.
     * time 5s 86ms
     */
    @Test
    public void persistGraph() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        //Data
        Graph graph = createSomeGraph(startNodesNo, depth);
        entityManager.persist(graph);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * This is much faster and needs much less stack memory.
     * <p>
     * time 1s 38ms
     * <p>
     * Seems to be more than 4 times faster on my laptop, and not stack memory setup required.
     * <p>
     * What's the difference between this and previous test-case?
     * - nextNode has no CASCADE in NodeNoCascade
     * - startNodes has no CASCADE in GraphNoCascade
     */
    @Test
    public void persistGraphNoCascade() {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        //Data
        GraphNoCascade graph = createSomeGraphNoCascade(startNodesNo, depth, entityManager);
        entityManager.persist(graph);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public static Graph createSomeGraph(int startNodesNo, int depth) {
        Set<Node> startNodes = new HashSet<>(startNodesNo);

        for (int i = 0; i < startNodesNo; i++) {
            Node node = generateNode();
            startNodes.add(node);
        }

        Node node2 = generateNode();

        startNodes.forEach(m -> m.addNextNode(node2));

        Node latest = node2;
        for (int i = 1; i < depth; i++) {
            Node node = generateNode();

            latest.addNextNode(node);
            latest = node;
        }

        Graph graph = new Graph();
        graph.getUuid();
        graph.setStartNodes(startNodes);
        return graph;
    }

    public GraphNoCascade createSomeGraphNoCascade(int startNodesNo, int depth, EntityManager entityManager) {
        Set<NodeNoCascade> startNodes = new HashSet<>(startNodesNo);

        for (int i = 0; i < startNodesNo; i++) {
            NodeNoCascade node = generateNodeNoCascade();
            entityManager.persist(node);
            startNodes.add(node);
        }

        NodeNoCascade node2 = generateNodeNoCascade();
        entityManager.persist(node2);

        startNodes.forEach(m -> m.addNextNode(node2));

        NodeNoCascade latest = node2;
        for (int i = 1; i < depth; i++) {
            NodeNoCascade node = generateNodeNoCascade();
            entityManager.persist(node);

            latest.addNextNode(node);
            latest = node;
        }

        GraphNoCascade graph = new GraphNoCascade();
        graph.getUuid();
        graph.setStartNodes(startNodes);
        return graph;
    }

    private static NodeNoCascade generateNodeNoCascade() {
        NodeNoCascade node = new NodeNoCascade();
        node.setName(randomString());
        node.getUuid();
        node.setNodeContent(generateContent());
        return node;
    }

    private static Node generateNode() {
        Node node = new Node();
        node.setName(randomString());
        node.getUuid();
        node.setNodeContent(generateContent());
        return node;
    }

    private static NodeContent generateContent() {
        NodeContent nc = new NodeContent();
        nc.setName(randomString());
        nc.getUuid();
        return nc;
    }

    private static String randomString() {
        byte[] array = new byte[30];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
