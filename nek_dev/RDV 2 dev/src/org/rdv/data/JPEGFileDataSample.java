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
 * $URL: https://rdv.googlecode.com/svn/branches/RDV-1.9/src/org/rdv/data/JPEGFileDataSample.java $
 * $Revision: 1149 $
 * $Date: 2008-07-03 10:23:04 -0400 (Thu, 03 Jul 2008) $
 * $Author: jason@paltasoftware.com $
 */

package org.rdv.data;

import java.io.File;

/**
 * A class to represent a data file containing a JPEG image.
 * 
 * @author Jason P. Hanley
 */
public class JPEGFileDataSample extends DataSample{
  /** the JPEG image */
  private final byte[] data;
  
  /** the JPEG file */
  private final File file;
  
  /**
   * Creates the object to represent this JPEG file data sample.
   * 
   * @param timestamp  the timestamp of the image
   * @param data       the image data
   * @param file       the file containing the image
   */
  public JPEGFileDataSample(double timestamp, byte[] data, File file) {
    super(timestamp);
    
    this.data = data;
    this.file = file;
  }

  /**
   * Gets the image data as a byte array.
   * 
   * @return  the image
   */
  public byte[] getData() {
    return data;
  }
  
  /**
   * Gets the file containing the image.
   * 
   * @return  the file containing the image
   */
  public File getFile() {
    return file;
  }
}