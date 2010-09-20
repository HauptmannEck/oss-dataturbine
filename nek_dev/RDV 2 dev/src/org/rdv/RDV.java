/*
 * RDV
 * Real-time Data Viewer
 * http://rdv.sourceforge.net/
 * 
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
 * $URL: https://rdv.googlecode.com/svn/branches/RDV-1.9/src/org/rdv/RDV.java $
 * $Revision: 1149 $
 * $Date: 2008-07-03 10:23:04 -0400 (Thu, 03 Jul 2008) $
 * $Author: jason@paltasoftware.com $
 */

package org.rdv;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.rdv.action.ActionFactory;
import org.rdv.rbnb.LocalServer;
import org.rdv.rbnb.RBNBController;
import org.rdv.ui.ApplicationFrame;
import org.rdv.util.PortKnock;

/**
 * The RDV application class.
 * 
 * @author Jason P. Hanley
 */
public class RDV extends SingleFrameApplication {
  
  /** the logger */
  static Log log = org.rdv.LogFactory.getLog(DataPanelManager.class.getName());
  
  /** the command line arguments */
  private String[] args;

  /**
   * Initializes the application before startup.
   * @param args  the command line arguments
   */
  @Override
  protected void initialize(String[] args) {
    super.initialize(args);
    
    this.args = args;

    //enable dynamic layout during application resize
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!toolkit.isDynamicLayoutActive()) {
      toolkit.setDynamicLayout(true);
    }

    //disable default drop target for swing components
    System.setProperty("suppressSwingDropSupport", "true");
  }

  /**
   * Starts up the application and shows the UI.
   */
  @Override
  protected void startup() {
    show(new ApplicationFrame());
  }
  
  /**
   * Processes any command line arguments after the application has started and
   * the UI is visible.
   */
  @Override
  protected void ready() {
    super.ready();
    
    Task task = new ProcessArgsTask(this, args);
    task.execute();
  }
  
  /**
   * A task to process command line arguments.
   * 
   * @author Jason P. Hanley
   */
  private class ProcessArgsTask extends Task {
    private final String[] args;
    
    public ProcessArgsTask(Application application, String[] args) {
      super(application);
      
      this.args = args;
    }

    @Override
    protected Object doInBackground() throws Exception {
      parseArgs(args, true);
      return null;
    }
  }

  /**
   * Shuts down the application.
   */
  @Override
  protected void shutdown() {
    super.shutdown();

    RBNBController.getInstance().exit();

    try {
      LocalServer.getInstance().stopServer();
    } catch (Exception e) {}
  }

  /**
   * Creates the command line options.
   * 
   * @return  the command line options.
   */
  @SuppressWarnings("static-access")
  private static Options createOptions() {
    Options options = new Options();

    Option hostNameOption = OptionBuilder.withArgName("host name").hasArg()
        .withDescription("The host name of the RBNB server")
        .withLongOpt("host").create('h');

    Option portNumberOption = OptionBuilder.withArgName("port number").hasArg()
        .withDescription("The port number of the RBNB server").withLongOpt(
            "port").create('p');

    Option channelsOption = OptionBuilder.withArgName("channels").hasArgs()
        .withDescription("Channels to subscribe to").withLongOpt("channels")
        .create('c');

    Option playbackRateOption = OptionBuilder.withArgName("playback rate")
        .hasArg().withDescription("The playback rate").withLongOpt(
            "playback-rate").create('r');

    Option timeScaleOption = OptionBuilder.withArgName("time scale").hasArg()
        .withDescription("The time scale in seconds").withLongOpt("time-scale")
        .create('s');

    Option playOption = OptionBuilder
        .withDescription("Start playing back data").withLongOpt("play")
        .create();

    Option realTimeOption = OptionBuilder.withDescription(
        "Start viewing data in real time").withLongOpt("real-time").create();

    Option portKnockOption = OptionBuilder.withArgName("ports").hasArgs()
        .withDescription(
            "A list of ports to port knock before connecting to the server")
        .withLongOpt("knock").create('k');

    Option helpOption = new Option("?", "help", false, "Display usage");

    options.addOption(hostNameOption);
    options.addOption(portNumberOption);
    options.addOption(channelsOption);
    options.addOption(playbackRateOption);
    options.addOption(timeScaleOption);
    options.addOption(playOption);
    options.addOption(realTimeOption);
    options.addOption(portKnockOption);
    options.addOption(helpOption);

    return options;
  }

  /**
   * Parses the command line arguments. If there is an error parsing the
   * arguments, an error will be printed to stderr and false will be returned.
   * 
   * @param args     the command line arguments
   * @param setArgs  if true set the options, if false just try and parse them
   * @return         true if the options parsed correctly, false otherwise
   */
  public static boolean parseArgs(String[] args, boolean setArgs) {
    Options options = createOptions();

    URL configURL = null;
    String hostName = null;
    int portNumber = -1;
    String[] channels = null;
    double playbackRate = -1;
    double timeScale = -1;
    boolean play = false;
    boolean realTime = false;
    int[] knockPorts = null;

    CommandLineParser parser = new PosixParser();

    try {
      CommandLine line = parser.parse(options, args);

      if (line.hasOption('?')) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DataViewer", options, true);
        return false;
      }

      String[] leftOverOptions = line.getArgs();
      if (leftOverOptions != null && leftOverOptions.length > 0) {
        String configFile = leftOverOptions[0];
        
        try {
          if (configFile.matches("^[a-zA-Z]+://.*")) {
            configURL = new URL(configFile);
          } else {
            configURL = new File(configFile).toURI().toURL();
          }
        } catch (MalformedURLException e) {
          System.err.println("\"" + configFile + "\" is not a valid configuration file URL.");
          return false;
        }
      }

      if (line.hasOption('h')) {
        hostName = line.getOptionValue('h');
      }

      if (line.hasOption('p')) {
        String value = line.getOptionValue('p');
        portNumber = Integer.parseInt(value);
      }

      if (line.hasOption('c')) {
        channels = line.getOptionValues('c');
      }

      if (line.hasOption('r')) {
        String value = line.getOptionValue('r');
        playbackRate = Double.parseDouble(value);
      }

      if (line.hasOption('s')) {
        String value = line.getOptionValue('s');
        timeScale = Double.parseDouble(value);
      }

      if (line.hasOption("play")) {
        play = true;
      } else if (line.hasOption("real-time")) {
        realTime = true;
      }

      if (line.hasOption('k')) {
        String[] knockPortStrings = line.getOptionValues('k');
        knockPorts = new int[knockPortStrings.length];
        try {
          for (int i = 0; i < knockPortStrings.length; i++) {
            knockPorts[i] = Integer.parseInt(knockPortStrings[i]);
            if (knockPorts[i] < 1 || knockPorts[i] > 65535) {
              throw new NumberFormatException("Invalid port range: "
                  + knockPorts[i]);
            }
          }
        } catch (NumberFormatException e) {
          System.err.println("Invalid port knocking option: " + e.getMessage()
              + ".");
          return false;
        }
      }
    } catch (ParseException e) {
      System.err.println("Command line arguments invalid: " + e.getMessage()
          + ".");
      return false;
    }
    
    if (!setArgs) {
      return true;
    }
    
    if (configURL != null) {
        ConfigurationManager.loadConfiguration(configURL);
        return true;
    }
    
    RBNBController rbnbController = RBNBController.getInstance();

    if (playbackRate != -1) {
      rbnbController.setPlaybackRate(playbackRate);
    }

    if (timeScale != -1) {
      rbnbController.setTimeScale(timeScale);
    }

    if (hostName == null && portNumber == -1) {
      ActionFactory.getInstance().getOfflineAction().goOffline();
      return true;
    }
    
    if (knockPorts != null) {
      try {
        PortKnock.knock(hostName, knockPorts);
      } catch (IOException e) {
        log.warn("Error port knocking: " + e.getMessage() + ".");
        e.printStackTrace();
      }
    }

    if (hostName != null) {
      rbnbController.setRBNBHostName(hostName);
    }

    if (portNumber != -1) {
      rbnbController.setRBNBPortNumber(portNumber);
    }

    if (!rbnbController.connect(true)) {
      return true;
    }
    
    if (channels != null) {
      for (int i = 0; i < channels.length; i++) {
        String channel = channels[i];
        log.info("Viewing channel " + channel + ".");
        DataPanelManager.getInstance().viewChannel(channel);
      }
    }

    if (play) {
      log.info("Starting data playback.");
      rbnbController.play();
    } else if (realTime) {
      log.info("Viewing data in real time.");
      rbnbController.monitor();
    }
    
    return true;
  }
  
  /**
   * Set properties to make the application integrate better with the Mac UI.
   */
  public static void setupForMac() {
    // when on mac, use the mac menu bar
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    
    // set the application name for mac
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "RDV");    
  }

  /**
   * The main method for the application.
   * 
   * @param args  the command line arguments
   */
  
  private static String[] arguments;
  public static void main(String[] args) {
    
    
    if (!parseArgs(args, false)) {
      return;
    }
    
    arguments = args;
    
    JFrame.setDefaultLookAndFeelDecorated(true);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
//        try {
//          UIManager.setLookAndFeel(new SubstanceGraphiteLookAndFeel());
//        } catch (Exception e) {
//          System.err.println("Substance Graphite failed to initialize");
//        }
        setupForMac();
        launch(RDV.class, arguments);
       
      }
    });
    
    
  }
}