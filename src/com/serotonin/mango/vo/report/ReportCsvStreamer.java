/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.report;

import java.io.PrintWriter;
import java.util.ResourceBundle;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.serotonin.mango.view.export.CsvWriter;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.web.i18n.I18NUtils;

/**
 * @author Matthew Lohbihler
 */
public class ReportCsvStreamer implements ReportDataStreamHandler {
    private final PrintWriter out;

    // Working fields
    private TextRenderer textRenderer;
    private final String[] data = new String[5];
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
    private final CsvWriter csvWriter = new CsvWriter();
    
    private final ResourceBundle this_bundle;
    private Boolean horizontal;
    private String[] header;
    private static final String CRLF = "\r\n";

    // Make array lists to store rdvs
    private ArrayList<ArrayList<ReportDataValue>> rdvLists;
    private ArrayList<ReportPointInfo> pointsList;

    public ReportCsvStreamer(PrintWriter out, ResourceBundle bundle, Boolean writeHeader) {
        this.out = out;
        this_bundle = bundle;

        // CHANGED
        if(writeHeader) {
            this.horizontal = false;
            // Write the header by default
            data[0] = I18NUtils.getMessage(this_bundle, "reports.pointName");
            data[1] = I18NUtils.getMessage(this_bundle, "common.time");
            data[2] = I18NUtils.getMessage(this_bundle, "common.value");
            data[3] = I18NUtils.getMessage(this_bundle, "reports.rendered");
            data[4] = I18NUtils.getMessage(this_bundle, "common.annotation");
            out.write(csvWriter.encodeRow(data));
        }
        else{
            this.rdvLists = new ArrayList<>();
            this.pointsList  = new ArrayList<>();
            this.horizontal = true;
        }
        
    }

    public void startPoint(ReportPointInfo pointInfo) {

        if(!horizontal){
            data[0] = pointInfo.getExtendedName();
            textRenderer = pointInfo.getTextRenderer();
        }
        else{
            // Start new list for unique point
            if(pointsList.isEmpty() || pointInfo.getExtendedName() != pointsList.get(pointsList.size() - 1).getExtendedName()){
                pointsList.add(pointInfo);
                rdvLists.add(new ArrayList<>());
            }
        }
    }

    public void pointData(ReportDataValue rdv) {

        if(!horizontal){
            data[1] = dtf.print(new DateTime(rdv.getTime()));

            if (rdv.getValue() == null)
                data[2] = data[3] = null;
            else {
                data[2] = rdv.getValue().toString();
                data[3] = textRenderer.getText(rdv.getValue(), TextRenderer.HINT_FULL);
            }

            data[4] = rdv.getAnnotation();

            out.write(csvWriter.encodeRow(data));
        }
        else
            rdvLists.get(pointsList.size() - 1).add(rdv);        // Add the point data  to the rdv sub-list
            

    }

    public void done() {
        if(horizontal)
            genReport();    // Don't forget to call the meat

        out.flush();
        out.close();
    }

    private void makeHeader(int pointCt){
        this.header = new String[(pointCt * 5) + 1];

        int ct = 0;
        while(ct < (pointCt * 5)){
            header[ct++] = I18NUtils.getMessage(this_bundle, "reports.pointName");
            header[ct++] = I18NUtils.getMessage(this_bundle, "common.time");
            header[ct++] = I18NUtils.getMessage(this_bundle, "common.value");
            header[ct++] = I18NUtils.getMessage(this_bundle, "reports.rendered");
            header[ct++] = I18NUtils.getMessage(this_bundle, "common.annotation");
        }
        header[ct] = CRLF;

        out.write(csvWriter.encodeRow(header));

    }

    private void genReport(){

        // Adjust point lists to remove empty unqiue data sets
        for(int i = (pointsList.size() - 1); i >= 0; i--){
            if(rdvLists.get(i).isEmpty()){
                rdvLists.remove(i);
                pointsList.remove(i);
            }
        }

        formatReps();

        int pointCt = pointsList.size();
        String[] pointData = new String[(pointCt * 5) + 1];
        
        // Write the header
        makeHeader(pointCt);

        // Determine longest list length
        int longest = 0;
        for(int i = 0; i < pointCt; i++){
            if(rdvLists.get(i).size() > longest)
                longest = rdvLists.get(i).size();
        }

        int currLineCt = 0;
        // Write point data
        for(int i = 0; i < longest; i++){

            for(int j = 0; j < pointCt; j++){
                if(rdvLists.get(j).size() <= i){
                    // Print empty cells
                    pointData[currLineCt++] = " ";
                    pointData[currLineCt++] = " ";
                    pointData[currLineCt++] = " ";
                    pointData[currLineCt++] = " ";
                    pointData[currLineCt++] = " ";
                }
                else{
                    // Print not empty cells
                    pointData[currLineCt++] = pointsList.get(j).getExtendedName();
                    textRenderer = pointsList.get(j).getTextRenderer();

                    pointData[currLineCt++] = dtf.print(new DateTime(rdvLists.get(j).get(i).getTime()));

                    if (rdvLists.get(j).get(i).getValue() == null)
                        pointData[currLineCt++] = data[currLineCt++] = null;
                    else {
                        pointData[currLineCt++] = rdvLists.get(j).get(i).getValue().toString();
                        pointData[currLineCt++] = textRenderer.getText(rdvLists.get(j).get(i).getValue(), TextRenderer.HINT_FULL);
                    }

                    pointData[currLineCt++] = rdvLists.get(j).get(i).getAnnotation();
                }

            }   
            pointData[currLineCt] = CRLF;
            currLineCt = 0;
            out.write(csvWriter.encodeRow(pointData));
        }
    }

    private void formatReps(){
        int ptCt = 0;
        int ind = 0;
        while(ptCt < pointsList.size()){

            ReportPointInfo tempInf = pointsList.get(ptCt);
            int cntr = ptCt;
            while(cntr < pointsList.size()){
                
                // If device names are the send
                if(pointsList.get(cntr).getDeviceName().compareToIgnoreCase(tempInf.getDeviceName()) == 0){
                    if(pointsList.get(cntr).getPointName().compareToIgnoreCase(tempInf.getPointName()) > 0){
                        tempInf = pointsList.get(cntr);
                        ind = cntr;
                    }
                }
                else if(pointsList.get(cntr).getDeviceName().compareToIgnoreCase(tempInf.getDeviceName()) > 0){
                    tempInf = pointsList.get(cntr);
                    ind = cntr;
                }
                cntr++;
            }
            
            pointsList.add(tempInf);
            pointsList.remove(ind);         
            rdvLists.add(rdvLists.get(ind));
            rdvLists.remove(ind);
            ptCt++;
        }
    }
}
