package com.example.ailearning.module.course.vo;

import java.util.List;

public class CourseGraphVO {
    private List<KnowledgePointVO> nodes;
    private List<KnowledgePointRelationVO> edges;

    public List<KnowledgePointVO> getNodes() { return nodes; }
    public void setNodes(List<KnowledgePointVO> nodes) { this.nodes = nodes; }
    public List<KnowledgePointRelationVO> getEdges() { return edges; }
    public void setEdges(List<KnowledgePointRelationVO> edges) { this.edges = edges; }
}
