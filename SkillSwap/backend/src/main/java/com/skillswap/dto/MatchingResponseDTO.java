package com.skillswap.dto;

import java.util.List;

public class MatchingResponseDTO {
    private String analysis;
    private List<MatchResultDTO> matches;

    public MatchingResponseDTO() {}

    public MatchingResponseDTO(String analysis, List<MatchResultDTO> matches) {
        this.analysis = analysis;
        this.matches = matches;
    }

    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public List<MatchResultDTO> getMatches() { return matches; }
    public void setMatches(List<MatchResultDTO> matches) { this.matches = matches; }
}
