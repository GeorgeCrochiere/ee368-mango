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
package com.serotonin.mango.db.upgrade;

import java.io.OutputStream;

/**
 * @author Matthew Lohbihler
 */
public class Upgrade1_12_1 extends DBUpgrade {
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

    // Added attempt to upgrade database
    private static String[] script = { //
            "alter table reportInstancePoints add column consolidatedChart char(1);", //
            "update reportInstancePoints set consolidatedChart='Y';", //
            "alter table reportInstancePoints add column plotType integer;", //
            "alter table reportInstancePoints add column title varchar(50);", //
            "alter table reportInstancePoints add column xAxisLabel varchar(50);", //
            "alter table reportInstancePoints add column yAxisLabel varchar(50);", //
            "alter table reportInstancePoints add column useYRef char(1);", //
            "alter table reportInstancePoints add column yRefVal double;", //
    };
}