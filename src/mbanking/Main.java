package mbanking;

import mbanking.dao.DatabaseException;
import mbanking.gui.GunadarmaBankGUI;
import mbanking.service.Bank;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GunadarmaBankGUI gui = new GunadarmaBankGUI(new Bank("Gunadarma Bank"));
                gui.setVisible(true);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage() + (e.getCause() == null ? "" : "\nDetail: " + e.getCause().getMessage()),
                        "GUNADARMA BANK",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
