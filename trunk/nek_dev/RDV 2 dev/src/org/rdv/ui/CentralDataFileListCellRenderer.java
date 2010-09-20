/*
 * RDV
 * Real-time Data Viewer
 * http://rdv.googlecode.com/
 * 
 * Copyright (c) 2005-2007 University at Buffalo
 * Copyright (c) 2005-2007 NEES Cyberinfrastructure Center
 * Copyright (c) 2008 Palta Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * $URL: https://rdv.googlecode.com/svn/branches/RDV-1.9/src/org/rdv/ui/CentralDataFileListCellRenderer.java $
 * $Revision: 1149 $
 * $Date: 2008-07-03 10:23:04 -0400 (Thu, 03 Jul 2008) $
 * $Author: jason@paltasoftware.com $
 */

package org.rdv.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.nees.data.DataFile;
import org.rdv.DataViewer;

/**
 * A class to the render the cell for a list of data files.
 * 
 * @author Jason P. Hanley
 */
public class CentralDataFileListCellRenderer extends DefaultListCellRenderer {

  /** serialization version identifier */
  private static final long serialVersionUID = 6253722034400759792L;

  public Component getListCellRendererComponent(JList list, Object value,
                                                int index, boolean isSelected,
                                                boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    
    if (value instanceof DataFile) {
      DataFile dataFile = (DataFile)value;
      setText(dataFile.getPath() + "/" + dataFile.getName());
      if (dataFile.isIsDirectory()) {
        setIcon(DataViewer.getIcon("icons/folder.gif"));
      } else {
        setIcon(DataViewer.getIcon("icons/file.gif"));
      }
    }
    
    return this;
  }
}