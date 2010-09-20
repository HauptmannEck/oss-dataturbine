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
 * $URL: https://rdv.googlecode.com/svn/branches/RDV-1.9/src/org/rdv/ui/LoginDialog.java $
 * $Revision: 1149 $
 * $Date: 2008-07-03 10:23:04 -0400 (Thu, 03 Jul 2008) $
 * $Author: jason@paltasoftware.com $
 */

package org.rdv.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rdv.AppProperties;
import org.rdv.auth.AuthenticationManager;
import org.rdv.auth.GridAuthentication;

/**
 * @author Wei Deng
 */
public class LoginDialog extends JDialog {

  /** serialization version identifier */
  private static final long serialVersionUID = 7914618740145224995L;

	static Log log = org.rdv.LogFactory.getLog(LoginDialog.class.getName());
	
	JLabel headerLabel;
  
  JLabel errorLabel;
	
	JLabel userNameLabel;
	JTextField userNameTextField;
	
	JLabel userPasswordLabel;
	JPasswordField userPasswordField;
	
	JButton loginButton;
	JButton cancelButton;
	
	public LoginDialog(JFrame owner) {
		super(owner, true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setTitle("Login to NEES");
    
    JPanel container = new JPanel();
    setContentPane(container);
    
    InputMap inputMap = container.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = container.getActionMap();
			
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipadx = 0;
		c.ipady = 0;
		
		headerLabel = new JLabel("Please specify your NEES account information.");
    headerLabel.setBackground(Color.white);
    headerLabel.setOpaque(true);
    headerLabel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0,0,1,0,Color.gray),
        BorderFactory.createEmptyBorder(10,10,10,10)));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.NORTHEAST;
    c.insets = new Insets(0,0,0,0);
    container.add(headerLabel, c);
    
    errorLabel = new JLabel();
    errorLabel.setVisible(false);
    errorLabel.setForeground(Color.RED);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.insets = new Insets(10,10,0,10);
    container.add(errorLabel, c);    
		
		c.gridwidth = 1;
		
		userNameLabel = new JLabel("Username:");
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTHEAST;
    c.insets = new Insets(10,10,10,5);
    container.add(userNameLabel, c);
		
		userNameTextField = new JTextField("", 25);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(10,0,10,10);
    container.add(userNameTextField, c);
		
		userPasswordLabel = new JLabel("Password:");
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.NORTHEAST;
    c.insets = new Insets(0,10,10,5);
    container.add(userPasswordLabel, c);

		userPasswordField = new JPasswordField(16);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(0,0,10,10);
    container.add(userPasswordField, c);
		
		JPanel buttonPanel = new JPanel();
    
    Action loginAction = new AbstractAction() {
      /** serialization version identifier */
      private static final long serialVersionUID = -5591044023056646223L;

      public void actionPerformed(ActionEvent e) {
        login();
      }      
    };
    loginAction.putValue(Action.NAME, "Login");
    inputMap.put(KeyStroke.getKeyStroke("ENTER"), "login");
    actionMap.put("login", loginAction);
		loginButton = new JButton(loginAction);
		buttonPanel.add(loginButton);
		
    Action cancelAction = new AbstractAction() {
      /** serialization version identifier */
      private static final long serialVersionUID = 6237115705468556255L;

      public void actionPerformed(ActionEvent e) {
        cancel();
      }      
    };
    cancelAction.putValue(Action.NAME, "Cancel");
    inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
    actionMap.put("cancel", cancelAction);    
    cancelButton = new JButton(cancelAction);
    buttonPanel.add(cancelButton);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
    c.insets = new Insets(0,10,10,5);
    container.add(buttonPanel, c);
		
		pack();
    setLocationByPlatform(true);
		setVisible(true);
	}
	
	public void setVisible(boolean visible) {
		if (visible) {
      errorLabel.setVisible(false);
      
			userNameTextField.requestFocusInWindow();
	 		userNameTextField.setSelectionStart(0);
	 		userNameTextField.setSelectionEnd(userNameTextField.getText().length());
      
      userPasswordField.setText("");
      
      pack();
		}
		super.setVisible(visible);
	}
	
	private void login() {
		String username = userNameTextField.getText();
		String password = new String(userPasswordField.getPassword());
    
    String centralHostName = AppProperties.getProperty("central.hostname", "central.nees.org");
		GridAuthentication authentication = new GridAuthentication(centralHostName);
    
		if (authentication.login(username, password)) {
      AuthenticationManager.getInstance().setAuthentication(authentication);
      
      dispose();
      
      errorLabel.setVisible(false);
      userPasswordField.setText("");
    } else {
      errorLabel.setText("Invalid username or password, please try again.");
      errorLabel.setVisible(true);
      
      userPasswordField.requestFocusInWindow();
      userPasswordField.setSelectionStart(0);
      userPasswordField.setSelectionEnd(userPasswordField.getPassword().length);
      
      pack();
    }
	}
	
	private void cancel() {
		dispose();		
	}	
}