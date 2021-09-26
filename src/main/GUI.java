package main;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.*;

public class GUI {
    /**
     * displays a popup window where the user can enter: entry name, username,
     * password, and master password
     * 
     * @return an array containing (in the following order 0-3): entry name,
     *         username, password, master password. If a field is left blank or a
     *         button that is not "OK" was pressed, null will be returned instead.
     */
    public static String[] createEntry() {
        final String generatePasswordText = "Generate Random Password";
        final String entryText = "Entry Name: ";
        final String usernameText = "Username: ";
        final String passwordText = "Password: ";
        final String masterPasswordText = "Master Password: ";
        final String title = "New Entry";

        JTextField entry = new JTextField();
        JTextField username = new JTextField();
        JTextField password = new JTextField();
        JPasswordField masterPassword = new JPasswordField();

        JButton generateRandomPassword = new JButton(generatePasswordText);
        generateRandomPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                password.setText(getRandomPassword(16));
            }
        });

        Object[] message = { entryText, entry, usernameText, username, passwordText, password, generateRandomPassword,
                masterPasswordText, masterPassword };

        int option = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null);

        if (option != JOptionPane.OK_OPTION || entry.getText() == null || username.getText() == null
                || password.getText() == null || masterPassword.getPassword() == null)
            return null;

        String[] output = new String[] { entry.getText(), username.getText(), password.getText(),
                new String(masterPassword.getPassword()) };

        return output;
    }

    /**
     * displays a popup window where the user can enter: entry name, password
     * 
     * @return an array containing (in the following order 0-1): entry name,
     *         password. If a field is left blank or a button that is not "OK" was
     *         pressed, null will be returned instead.
     */
    public static String[] removeEntry() {
        final String entryText = "Entry Name: ";
        final String passwordText = "Password: ";
        final String title = "Remove Entry";

        JTextField entry = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] message = { entryText, entry, passwordText, password };

        int option = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null);

        if (option != JOptionPane.OK_OPTION || entry.getText() == null || password.getPassword() == null)
            return null;

        String[] output = new String[] { entry.getText(), new String(password.getPassword()) };

        return output;
    }

    /**
     * displays a popup where the user can see the: entry name, username, password.
     * As well as copying the username and password to the clipboard
     * 
     * @param entry    entry name
     * @param username username
     * @param password password
     */
    public static void displayEntry(String entry, String username, String password) {
        // it is important to make sure all strings have the same length
        String entryText = "Entry Name: ";
        String usernameText = "Username:   ";
        String passwordText = "Password:   ";
        final String copyIcon = "/resources/copyIcon.png";
        final String title = "Entry";
        final int textFieldSize = 16;

        // entry----------
        JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField entryField = new JTextField(textFieldSize);
        JLabel entryLabel = new JLabel(entryText);
        entryField.setEditable(false);
        entryField.setText(entry);
        entryPanel.add(entryLabel);
        entryPanel.add(entryField);

        // username------------
        JButton copyUsername = new JButton(new ImageIcon(GUI.class.getResource(copyIcon)));
        copyUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(username), null);
            }
        });

        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField usernameField = new JTextField(textFieldSize);
        JLabel usernameLabel = new JLabel(usernameText);
        usernameField.setEditable(false);
        usernameField.setText(username);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernamePanel.add(copyUsername);

        // password------------
        JButton copyPassword = new JButton(new ImageIcon(GUI.class.getResource(copyIcon)));
        copyPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(password), null);
            }
        });

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField passwordField = new JTextField(textFieldSize);
        JLabel passwordLabel = new JLabel(passwordText);
        passwordField.setEditable(false);
        passwordField.setText(password);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        passwordPanel.add(copyPassword);

        // popup---------
        Object[] message = { entryPanel, usernamePanel, passwordPanel };

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, null);
    }

    /**
     * displays a popup window where the user can enter a password
     * 
     * @return the password or null if a button other then "OK" was pressed
     */
    public static String enterPassword() {
        final String passwordText = "Password: ";
        final String title = "New Entry";

        JPasswordField password = new JPasswordField();

        Object[] message = { passwordText, password };

        int option = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null);

        if (option != JOptionPane.OK_OPTION || password.getPassword() == null)
            return null;

        // I know that getPassword recommends that you set the values of the char array
        // to 0. However, the array will have to be converted into a String later on
        // anyway, so deleting the char array for security reasons is meaningless.
        return new String(password.getPassword());
    }

    /**
     * display wrong password message
     */
    public static void wrongPassword() {
        JOptionPane.showMessageDialog(null, "Error: Wrong password", "Error", JOptionPane.ERROR_MESSAGE, null);
    }

    /**
     * display entry already exists
     */
    public static void entryExists() {
        JOptionPane.showMessageDialog(null, "Error: Entry already exists", "Error", JOptionPane.ERROR_MESSAGE, null);
    }

    /**
     * generates a random alphanumeric string of a given length
     * 
     * @param length length of the desired string
     * @return a random alphanumeric string
     */
    private static String getRandomPassword(int length) {
        final String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}
