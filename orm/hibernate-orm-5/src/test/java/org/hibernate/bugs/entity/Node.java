package org.hibernate.bugs.entity;

import org.hibernate.annotations.OptimisticLock;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "T_NODE")
public class Node {

    private Long id;
    private UUID uuid;

    private Set<Node> nextNode = new HashSet<>();
    private Set<Node> prevNode = new HashSet<>();
    private NodeContent nodeContent;
    private String name;
    private Graph startNodeOf;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "node_seq")
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "T_NODES_ADJACENCY",
            joinColumns = {@JoinColumn(name = "parent_id")},
            inverseJoinColumns = {@JoinColumn(name = "child_id")}
    )
    public Set<Node> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Set<Node> nextNode) {
        this.nextNode = nextNode;
    }

    public void addNextNode(Node node) {
        this.nextNode.add(node);
        node.getPrevNode().add(this);
    }

    public void removeNextNode(Node node) {
        this.nextNode.remove(node);
        node.getPrevNode().remove(this);
    }

    @ManyToMany(mappedBy = "nextNode")
    @OptimisticLock(excluded = true)
    public Set<Node> getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Set<Node> prevNode) {
        this.prevNode = prevNode;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public NodeContent getNodeContent() {
        return nodeContent;
    }

    public void setNodeContent(NodeContent nodeContent) {
        this.nodeContent = nodeContent;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "startNodeOf")
    public Graph getStartNodeOf() {
        return startNodeOf;
    }

    public void setStartNodeOf(Graph startNodeOf) {
        this.startNodeOf = startNodeOf;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(uuid, ((Node) o).uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

}
