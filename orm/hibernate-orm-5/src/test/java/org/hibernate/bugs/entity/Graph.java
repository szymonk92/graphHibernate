package org.hibernate.bugs.entity;

import org.hibernate.annotations.OptimisticLock;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "T_GRAPH")
public class Graph {

    private Long id;
    private Set<Node> startNodes = new HashSet<>();
    private UUID uuid;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "graph_seq")
    @Column(name = "ID", updatable = false, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "uuid")
    public UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "startNodeOf")
    @OptimisticLock(excluded = true)
    public Set<Node> getStartNodes() {
        return startNodes;
    }

    public void setStartNodes(Set<Node> startNodes) {
        this.startNodes = startNodes;
    }

    public void addStartNode(Node node) {
        startNodes.add(node);
        node.setStartNodeOf(this);
    }

    public void removeStartNode(Node node) {
        startNodes.remove(node);
        node.setStartNodeOf(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph node = (Graph) o;
        return Objects.equals(uuid, node.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
