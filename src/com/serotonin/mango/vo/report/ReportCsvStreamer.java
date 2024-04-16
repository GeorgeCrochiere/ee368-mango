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
    private final ResourceBundle this_bundle; // Data member to store current exportation sessions resource bundle


    // Array lists for data value and point info object storage
    private ArrayList<ArrayList<ReportDataValue>> rdvLists;
    private ArrayList<ReportPointInfo> pointsList;
    private Boolean horizontal; // Predicate value for format processing

    private String[] header; // String array for horizontal header generation
    private static final String CRLF = "\r\n"; // Constant to append to the end of a row for file printing

    public ReportCsvStreamer(PrintWriter out, ResourceBundle bundle, Boolean writeHeader) {
        this.out = out;
        this_bundle = bundle;       // Captures the sessions bundle for object use

        // If path for default single column report generation
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
        // Initialization for private data members for horizontal report generation
        else{
            this.rdvLists = new ArrayList<>();
            this.pointsList  = new ArrayList<>();
            this.horizontal = true;
        }
        
    }

    public void startPoint(ReportPointInfo pointInfo) {
        // if true start default row print processing
        if(!horizontal){
            data[0] = pointInfo.getExtendedName();
            textRenderer = pointInfo.getTextRenderer();
        }
        // else adds point to unique point list and intializes new report data value list if new unique point is processed
        else{
            // Start new list for unique point
            if(pointsList.isEmpty() || pointInfo.getExtendedName() != pointsList.get(pointsList.size() - 1).getExtendedName()){
                pointsList.add(pointInfo);
                rdvLists.add(new ArrayList<>());
            }
        }
    }

    public void pointData(ReportDataValue rdv) {
        // if true adds unique sensor data at specific times to row then prints to csv file
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
        // else adds report data value object to current report data value array list
        else
            rdvLists.get(pointsList.size() - 1).add(rdv);
            

    }

    public void done() {
        //  if true generate the report with new formating
        if(horizontal)
            genReport();

        out.flush();
        out.close();
    }

    /*
        Purpose: Generates a horizontally formatted '.csv' file containing sensor point information
                    Points are arranged in unique column groups, ordered alphabetically from left to right

        Pre: ReportCsvStreamer's done() function has been called after array lists have been appropriately populated

        Post: '.csv' file has been print to with proper format and exported for users

    */
    // Generates the report 
    private void genReport(){

        // Clean up report data and ppint lists for formated print
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
                        pointData[currLineCt++] = String.valueOf(rdvLists.get(j).get(i).getValue());
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

    /*
        Purpose: Makes the header for each unique data sensor

        Pre: parameter is pointCt which represents the points that need to be written to the csv

        Post: Outputs the header, where the number of unique headers is equivalent to the number of pointCt

    */
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
        // to write the entire row to csv
        out.write(csvWriter.encodeRow(header));

    }

    /*
        Purpose: Alphabetically sorts and removes empty sets of sensor point data within the array list pdm's

        Pre: Array lists have been appropriately populated with sensor point data objects

        Post: Array lists pdm's have been updated to be order alphabetically with empty sets removed

    */
    private void formatReps(){

        // Adjust point lists to remove empty unqiue data sets
        for(int i = (pointsList.size() - 1); i >= 0; i--){
            if(rdvLists.get(i).isEmpty()){
                rdvLists.remove(i);
                pointsList.remove(i);
            }
        }

        int ptCt = 0;
        int ind = 0;
        // Index through each data value entry
        while(ptCt < pointsList.size()){

            ReportPointInfo tempInf = pointsList.get(ptCt);
            int cntr = ptCt;
            // Index through each unique point
            while(cntr < pointsList.size()){
                
                // Interpret order of points first by device name, then point name is the same device
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
            // Update element locations in rdv and point info lists
            pointsList.add(tempInf);
            pointsList.remove(ind);         
            rdvLists.add(rdvLists.get(ind));
            rdvLists.remove(ind);
            ptCt++;
        }
    }
}
