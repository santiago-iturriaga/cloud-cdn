/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package globetraff;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;

/**
 *
 * @author mmlab
 */

public class DirChooser extends JPanel
   implements ActionListener {
   JButton go;
   
   JFileChooser chooser;
   String choosertitle;
   File baseDir;
   File defaultDir;
   String defaultDir_str;
   
  public DirChooser() {
    baseDir     = new java.io.File(".");
    defaultDir  = new java.io.File(baseDir.toString()+"/data");
    chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(defaultDir);
    
    go = new JButton("Select");
    go.addActionListener(this);
    add(go);
   }

  public void actionPerformed(ActionEvent e) {
    int result;
    
    chooser.setDialogTitle(choosertitle);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //
    // disable the "All files" option.
    //
    chooser.setAcceptAllFileFilterUsed(false);
    //    
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
      System.out.println("getCurrentDirectory(): " 
         +  chooser.getCurrentDirectory());
      System.out.println("getSelectedFile() : " 
         +  chooser.getSelectedFile());
      }
    else {
      System.out.println("No Selection ");
      }
     }
   
  public String getCurrentDirStr()
  {
      return chooser.getCurrentDirectory().toString();
  }
  
  public Dimension getPreferredSize(){
    return new Dimension(70, 40);
    }
}
