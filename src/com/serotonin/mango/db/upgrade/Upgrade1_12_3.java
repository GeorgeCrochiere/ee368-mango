package com.serotonin.mango.db.upgrade;

import java.io.OutputStream;

/**
 * @author George D. Crochiere
 */
public class Upgrade1_12_3 extends DBUpgrade {
    @Override
    public void upgrade() throws Exception {
        OutputStream out = createUpdateLogOutputStream("1_12_3");

        out.flush();
        out.close();
    }

    // Added additional attempt to upgrade db
    @Override
    protected String getNewSchemaVersion() {
        return "1.12.4";
    }

}
