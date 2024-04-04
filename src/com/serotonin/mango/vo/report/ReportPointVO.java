package com.serotonin.mango.vo.report;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.serotonin.util.SerializationHelper;

public class ReportPointVO implements Serializable {
    private int pointId;
    private String colour;
    private boolean consolidatedChart;
    private int plotType; // 0 = linear, 1 = Scatter
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private double yReference;
    private boolean useYReference;

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public boolean isConsolidatedChart() {
        return consolidatedChart;
    }

    public void setConsolidatedChart(boolean consolidatedChart) {
        this.consolidatedChart = consolidatedChart;
    }

    public int getPlotType() {
        return this.plotType;
    }

    public void setPlotType(int plotType) {
        this.plotType = plotType;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXAxisLabel() {
        return this.xAxisLabel;
    }

    public void setXAxisLabel(String xAxis) {
        this.xAxisLabel = xAxis;
    }

    public String getYAxisLabel() {
        return this.yAxisLabel;
    }

    public void setYAxisLabel(String yAxis) {
        this.yAxisLabel = yAxis;
    }

    public double getYReference() {
        return this.yReference;
    }

    public void setYReference(double yReference) {
        this.yReference = yReference;
    }

    public boolean getUseYReference() {
        return useYReference;
    }

    public void setUseYReference(boolean useYReference) {
        this.useYReference = useYReference;
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 2;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);

        out.writeInt(pointId);
        SerializationHelper.writeSafeUTF(out, colour);
        out.writeBoolean(consolidatedChart);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly
        // handled.
        if (ver == 1) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = true;
            title = SerializationHelper.readSafeUTF(in);
            xAxisLabel = SerializationHelper.readSafeUTF(in);
            yAxisLabel = SerializationHelper.readSafeUTF(in);
            yReference = in.readDouble();
            useYReference = true;

        } else if (ver == 2) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = in.readBoolean();
            title = SerializationHelper.readSafeUTF(in);
            xAxisLabel = SerializationHelper.readSafeUTF(in);
            yAxisLabel = SerializationHelper.readSafeUTF(in);
            yReference = in.readDouble();
            useYReference = in.readBoolean();

        }
    }
}
