package com.example.EduGraph.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "node_connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeConnection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    private Node sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    private Node targetNode;

    @Column(length = 100)
    private String label;

    public Node getFromNode() { return sourceNode; }
    public Node getToNode() { return targetNode; }
    public String getRelationshipLabel() { return label; }
    public Boolean getBidirectional() { return false; }
}