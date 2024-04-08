package com.serotonin.mango.db.upgrade;

import java.io.OutputStream;

/**
 * @author George D. Crochiere
 */
public class Upgrade1_13_0 extends DBUpgrade {
    @Override
    public void upgrade() throws Exception {
        OutputStream out = createUpdateLogOutputStream("1_12_1");

        runScript(script, out);

        out.flush();
        out.close();
    }

    @Override
    protected String getNewSchemaVersion() {
        return "1.12.2";
    }

    private static String[] script = { // plotType, title, xAxisLabel, yAxisLabel, useYRef, yRefVal
            "alter table app.reportinstancepoints add column plotType INTEGER;", //
            "alter table app.reportinstancepoints add column title VARCHAR(50);", //
            "alter table app.reportinstancepoints add column xLabelAxis VARCHAR(50);", //
            "alter table app.reportinstancepoints add column yLabelAxis VARCHAR(50);", //
            "alter table app.reportinstancepoints add column useYRef CHAR(1)", //
            "alter table app.reportinstancepoints add column yRefVal DOUBLE", //
    };
}
