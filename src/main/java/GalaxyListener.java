import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GalaxyListener implements ActionListener {

    public GalaxyListener() {}
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Pause Simulation")) {
            Simulator.getInstance().pauseSimulation();
        } else if (e.getActionCommand().equals("Resume Simulation")) {
            Simulator.getInstance().resumeSimulation();
        }
    }
}
