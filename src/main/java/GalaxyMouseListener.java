import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author CharlieRay668 (Charlie Ray)
 */
public class GalaxyMouseListener implements MouseListener, MouseMotionListener {

    private GalaxyPainter galaxyPainter;

    public GalaxyMouseListener(GalaxyPainter galaxyPainter) {
        this.galaxyPainter = galaxyPainter;
    }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        for (Function f : galaxyPainter.getFunctions()) {
            if (f.contains(e.getX(), e.getY())) {
                f.setSelected(true);
                galaxyPainter.repaint();
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (Function f : galaxyPainter.getFunctions()) {
            f.setSelected(false);
        }
        galaxyPainter.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        for (Function f : galaxyPainter.getFunctions()) {
            if (f.isSelected()) {
                f.setX(e.getX());
                f.setY(e.getY());
                galaxyPainter.repaint();
                break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
