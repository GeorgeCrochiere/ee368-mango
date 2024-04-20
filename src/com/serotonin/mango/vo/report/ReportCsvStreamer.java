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
import java.util.Collections;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.serotonin.mango.view.export.CsvWriter;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.web.i18n.I18NUtils;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import javafx.util.Pair;

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
    private final ResourceBundle sessionBundle; // Data member to store current exportation sessions resource bundle

    // Dictionary to store the point information
    private Dictionary<String,Pair<TextRenderer,ArrayList<ReportDataValue>>> pointDict;
    private ReportPointInfo currPoint;  // most recent point process

    private Boolean horizontal; // Predicate value for format processing

    private String[] header; // String array for horizontal header generation
    private static final String CRLF = "\r\n"; // Constant to append to the end of a row for file printing

    public ReportCsvStreamer(PrintWriter out, ResourceBundle bundle, Boolean writeHeader) {
        this.out = out;
        this.sessionBundle = bundle;       // Captures the sessions bundle for object use

        // If path for default single column report generation
        if(writeHeader) {
            this.horizontal = false;
            // Write the header by default
            data[0] = I18NUtils.getMessage(sessionBundle, "reports.pointName");
            data[1] = I18NUtils.getMessage(sessionBundle, "common.time");
            data[2] = I18NUtils.getMessage(sessionBundle, "common.value");
            data[3] = I18NUtils.getMessage(sessionBundle, "reports.rendered");
            data[4] = I18NUtils.getMessage(sessionBundle, "common.annotation");
            out.write(csvWriter.encodeRow(data));
        }
        // Initialization for private data members for horizontal report generation
        else{
            this.horizontal = true;                 // set formating predicate
            this.pointDict = new Hashtable<>();     // create hashtable to represent the dictionary
        }
        
    }

    public void startPoint(ReportPointInfo pointInfo) {
        // if true start default row print processing
        if(!horizontal){
            data[0] = pointInfo.getExtendedName();
            textRenderer = pointInfo.getTextRenderer();
        }
        // Else interpret point object
        else{
            // If an empty list or new point processed
            if(pointDict.isEmpty() || currPoint.getExtendedName() != pointInfo.getExtendedName()){
                this.currPoint = pointInfo;
                // add new entry to the dictionary
                pointDict.put(currPoint.getExtendedName(), new Pair<>(currPoint.getTextRenderer(), new ArrayList<>()));
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
        // else capture value to map to currPoint key in dictionary
        else
            pointDict.get(currPoint.getExtendedName()).getValue().add(rdv);



    }

    public void done() {
        //  if true generate the report with new formating
        if(horizontal)
            genReport();

        out.flush();
        out.close();
    }


    /**
        Purpose: Generates a horizontally formatted '.csv' file containing sensor point information
                    Points are arranged in unique column groups, ordered alphabetically from left to right

        <p>
        Pre: ReportCsvStreamer's done() function has been called after array lists have been appropriately populated

        <p>
        Post: '.csv' file has been print to with proper format and exported for users
    */
    // Generates the report 
    private void genReport(){

        // Calculate size of dictionary for printing appropriate number of column groups
        int pointCt = pointDict.size();
        String[] pointData = new String[(pointCt * 5) + 1];
        
        // Write the header
        makeHeader(pointCt);

        // Capture the keys (point names) and sort for printing alphabetically
        ArrayList<String> points = Collections.list(pointDict.keys());
        Collections.sort(points);

        // Determine longest data value list length
        int longest = 0;
        for(int i = 0; i < pointCt; i++){
            if(pointDict.get(points.get(i)).getValue().size() > longest)
                longest = pointDict.get(points.get(i)).getValue().size();
        }
        

        // Loop through dictionary, print each points nth array list value
        int currLineCt = 0;
        // Loop through list of rdv elements
        for(int i = 0; i < longest; i++){
            // loop through each point
            for(int j = 0; j < pointCt; j++){
                // If the current points list has been 
                if(pointDict.get(points.get(j)).getValue().size() <= i){
                    // Print empty cells
                    for(int k = 0; k < 5; k++)
                        pointData[currLineCt + k] = " ";

                    currLineCt += 5;
                }
                else{
                    // Print not empty cells
                    pointData[currLineCt++] = points.get(j);
                    textRenderer = pointDict.get(points.get(j)).getKey();

                    pointData[currLineCt++] = dtf.print(new DateTime(pointDict.get(points.get(j)).getValue().get(i).getTime()));

                    if (pointDict.get(points.get(j)).getValue().get(i).getValue() == null)
                        pointData[currLineCt++] = data[currLineCt++] = null;
                    else {
                        pointData[currLineCt++] = String.valueOf(pointDict.get(points.get(j)).getValue().get(i).getValue());
                        pointData[currLineCt++] = textRenderer.getText(pointDict.get(points.get(j)).getValue().get(i).getValue(), TextRenderer.HINT_FULL);
                    }

                    pointData[currLineCt++] = pointDict.get(points.get(j)).getValue().get(i).getAnnotation();
                }

            }   
            // Finish line
            pointData[currLineCt] = CRLF;
            currLineCt = 0;
            out.write(csvWriter.encodeRow(pointData));
        }
    }

    /**
        Purpose: Writes the header for each unique data sensor column group

        <p>
        Pre: A number of point info sets have been process and the count value has been passed
            in the call of this method

        <p>
        Post: Writes the header to the '.csv' file

        @param pointCt represents the number of point sets that will be print
    */
    private void makeHeader(int pointCt){
        int headLength = (pointCt * 5);     // Capture requisite header count length
        this.header = new String[headLength + 1];   // Instantiate header data member

        // For loop to add the header portions for each unique point
        for(int i = 0; i < headLength; i += 5){
            header[i] = I18NUtils.getMessage(sessionBundle, "reports.pointName");
            header[i + 1] = I18NUtils.getMessage(sessionBundle, "common.time");
            header[i + 2] = I18NUtils.getMessage(sessionBundle, "common.value");
            header[i + 3] = I18NUtils.getMessage(sessionBundle, "reports.rendered");
            header[i + 4] = I18NUtils.getMessage(sessionBundle, "common.annotation");
        }
        header[headLength] = CRLF;
        // to write the entire row to csv
        out.write(csvWriter.encodeRow(header));

    }
}
