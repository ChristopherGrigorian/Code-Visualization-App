import javax.swing.*;

public class GalaxyControlPanel extends JPanel {

    public GalaxyControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(new JLabel("Control Panel"));
        addButton(controlPanel, "Pause Simulation");
        addButton(controlPanel, "Resume Simulation");
        this.add(controlPanel);
    }

    private void addButton(JPanel panel, String label) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.addActionListener(new GalaxyListener());
        panel.add(button);
    }
}