package com.example.ailearning.module.fusion.vo;

import java.util.List;

public class FusionGraphVO {
    private List<FusionNodeVO> nodes;
    private List<FusionEdgeVO> edges;
    private List<FusionNodeVO> weakPoints;
    private List<String> recommendedPath;

    public List<FusionNodeVO> getNodes() { return nodes; }
    public void setNodes(List<FusionNodeVO> nodes) { this.nodes = nodes; }
    public List<FusionEdgeVO> getEdges() { return edges; }
    public void setEdges(List<FusionEdgeVO> edges) { this.edges = edges; }
    public List<FusionNodeVO> getWeakPoints() { return weakPoints; }
    public void setWeakPoints(List<FusionNodeVO> weakPoints) { this.weakPoints = weakPoints; }
    public List<String> getRecommendedPath() { return recommendedPath; }
    public void setRecommendedPath(List<String> recommendedPath) { this.recommendedPath = recommendedPath; }
}
