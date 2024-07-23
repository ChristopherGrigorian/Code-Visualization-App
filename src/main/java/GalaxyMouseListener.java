import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GalaxyMouseListener implements MouseListener, MouseMotionListener {

    private Galaxy galaxy;

    public GalaxyMouseListener(Galaxy galaxy) {
        this.galaxy = galaxy;
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (Function f : galaxy.getFunctions()) {
            if (f.contains(e.getX(), e.getY())) {
                f.setSelected(true);
                galaxy.repaint();
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (Function f : galaxy.getFunctions()) {
            f.setSelected(false);
        }
        galaxy.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (Function f : galaxy.getFunctions()) {
            if (f.isSelected()) {
                f.setX(e.getX());
                f.setY(e.getY());
                galaxy.repaint();
                break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
