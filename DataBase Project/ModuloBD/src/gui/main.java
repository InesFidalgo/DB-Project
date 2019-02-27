/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Manuel
 */
public class main {
    public static void main(String[] args){
    SwingUtilities.invokeLater(new Runnable(){
        @Override
        public void run(){
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new WikiStarterGUI());
            frame.pack();
            frame.setVisible(true);
        }
    });
}
    
    
}
