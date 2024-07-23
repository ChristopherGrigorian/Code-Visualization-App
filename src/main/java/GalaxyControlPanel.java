import javax.swing.*;

public class GalaxyControlPanel extends JPanel {

    public GalaxyControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(new JLabel("Control Panel"));
        JButton pauseButton = new JButton("Pause Simulation");
        pauseButton.setActionCommand("Pause Simulation");
        controlPanel.add(pauseButton);
        JButton resumeButton = new JButton("Resume Simulation");
        resumeButton.setActionCommand("Resume Simulation");
        controlPanel.add(resumeButton);
        // add event listener
        GalaxyListener listener = new GalaxyListener();
        pauseButton.addActionListener(listener);
        resumeButton.addActionListener(listener);
        this.add(controlPanel);

    }
}
