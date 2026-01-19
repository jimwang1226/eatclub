package com.eatclub.api.dto;

public class PeakTimeResponse {

    private String peakTimeStart;
    private String peakTimeEnd;

    public PeakTimeResponse() {
    }

    public PeakTimeResponse(String peakTimeStart, String peakTimeEnd) {
        this.peakTimeStart = peakTimeStart;
        this.peakTimeEnd = peakTimeEnd;
    }

    public String getPeakTimeStart() {
        return peakTimeStart;
    }

    public void setPeakTimeStart(String peakTimeStart) {
        this.peakTimeStart = peakTimeStart;
    }

    public String getPeakTimeEnd() {
        return peakTimeEnd;
    }

    public void setPeakTimeEnd(String peakTimeEnd) {
        this.peakTimeEnd = peakTimeEnd;
    }
}
