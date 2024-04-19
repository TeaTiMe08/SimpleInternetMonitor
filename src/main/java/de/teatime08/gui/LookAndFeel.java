package de.teatime08.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;

public class LookAndFeel {

    public LookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize FlatLaf." );
        }
    }

    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }
}
