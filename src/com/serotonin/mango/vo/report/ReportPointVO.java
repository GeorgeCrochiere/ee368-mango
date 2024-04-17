package com.serotonin.mango.vo.report;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.common.graph.ElementOrder;
import com.serotonin.json.JsonObject;
import com.serotonin.util.SerializationHelper;

public class ReportPointVO implements Serializable {
    private int pointId;
    private String colour;
    private boolean consolidatedChart;
    private int plotType; // 0 = linear, 1 = Scatter
    private String title;
    private boolean useYReference;
    private String xaxisLabel;
    private double yreference;
    private String yaxisLabel;

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

    public String getXaxisLabel() {
        return this.xaxisLabel;
    }

    public void setXaxisLabel(String xAxis) {
        this.xaxisLabel = xAxis;
    }

    public String getYaxisLabel() {
        return this.yaxisLabel;
    }

    public void setYaxisLabel(String yAxis) {
        this.yaxisLabel = yAxis;
    }

    public double getYreference() {
        return this.yreference;
    }

    public void setYreference(double yReference) {
        this.yreference = yReference;
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
        out.writeInt(plotType);
        SerializationHelper.writeSafeUTF(out, title);
        out.writeBoolean(useYReference);
        SerializationHelper.writeSafeUTF(out, xaxisLabel);
        out.writeDouble(yreference);
        SerializationHelper.writeSafeUTF(out, yaxisLabel);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly
        // handled.
        if (ver == 1) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = true;
            plotType = 0;
            title = SerializationHelper.readSafeUTF(in);
            useYReference = true;
            xaxisLabel = SerializationHelper.readSafeUTF(in);
            yreference = in.readDouble();
            yaxisLabel = SerializationHelper.readSafeUTF(in);

        } else if (ver == 2) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = in.readBoolean();
            try {
                plotType = in.readInt();
            } catch (EOFException e) {
                plotType = 0;
            }
            try {
                title = SerializationHelper.readSafeUTF(in);
                // title = in.toString();
            } catch (EOFException e) {
                title = "";
            }
            try {
                useYReference = in.readBoolean();
            } catch (EOFException e) {
                useYReference = false;
            }
            try {
                xaxisLabel = SerializationHelper.readSafeUTF(in);
            } catch (EOFException e) {
                xaxisLabel = "";
            }
            try {
                yreference = in.readDouble();
            } catch (EOFException e) {
                yreference = 0.0;
            }
            try {
                yaxisLabel = SerializationHelper.readSafeUTF(in);
            } catch (EOFException e) {
                yaxisLabel = "";
            }
        }
    }
}
