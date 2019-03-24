package coriolis.views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.LinkedList;

/**
 * Created by maxvl on 19.05.2017.
 */
public class PendulumCanvas extends ResizableCanvas {
    private static final double PAINT_INTERVAL = 0.01;
    private static final double PAINT_COUNT = 670;
    private double time;
    private LinkedList<Pair<Double, Double>> dotList = new LinkedList<>();
    private double alpha = 0;
    private double latestSystemTime;
    private double latestPaintTime = 0;
    private DoubleProperty drawingAmplitude = new SimpleDoubleProperty();
    private DoubleProperty latitude = new SimpleDoubleProperty();
    private DoubleProperty pendantLength = new SimpleDoubleProperty();
    private DoubleProperty amplitude = new SimpleDoubleProperty();
    private DoubleProperty timeModificator = new SimpleDoubleProperty();

    public PendulumCanvas() {
        super();
        time = 0;
        latestSystemTime = System.currentTimeMillis() / 1000.;
    }

    @Override
    void draw() {
        double planeRotationSpeed = 15
                * (1 - 3. / 8 * Math.pow(amplitude.doubleValue() / pendantLengthProperty().doubleValue(), 2))
                * Math.sin(latitude.doubleValue() * Math.PI / 180) / 3600;

        double currentTime = System.currentTimeMillis() / 1000.;
        double timeChange = (currentTime - latestSystemTime) * timeModificator.doubleValue();

        alpha += planeRotationSpeed * timeChange * Math.PI / 180;
        time += timeChange;
        latestSystemTime = currentTime;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        double position = drawingAmplitude.doubleValue()
                * Math.sin(time * Math.sqrt(9.8 / pendantLength.doubleValue()));

        if (latestPaintTime + PAINT_INTERVAL < latestSystemTime) {
            latestPaintTime = latestSystemTime;
            dotList.addFirst(new Pair<>(position / drawingAmplitude.doubleValue(), alpha));
            if (dotList.size() > PAINT_COUNT)
                dotList.removeLast();
        }

        double opacityStep = Math.pow(0.01, 1. / PAINT_COUNT);
        for (int i = 0; i < dotList.size() - 1; i++) {
            gc.setLineWidth(2);
            gc.setStroke(Color.rgb(245, 184, 149,  Math.pow(opacityStep, i)));
            Pair<Double, Double> dot1 = dotList.get(i);
            Pair<Double, Double> dot2 = dotList.get(i + 1);
            double x1 = getWidth() / 2 + dot1.getKey() * drawingAmplitude.doubleValue() * Math.cos(dot1.getValue());
            double y1 = getHeight() / 2 + dot1.getKey() * drawingAmplitude.doubleValue() * Math.sin(dot1.getValue());
            double x2 = getWidth() / 2 + dot2.getKey() * drawingAmplitude.doubleValue() * Math.cos(dot2.getValue());
            double y2 = getHeight() / 2 + dot2.getKey() * drawingAmplitude.doubleValue() * Math.sin(dot2.getValue());
            gc.strokeLine(x1, y1, x2, y2);
        }

        gc.setFill(Color.BLACK);
        gc.fillOval(getWidth() / 2 + position * Math.cos(alpha) - 10,
                getHeight() / 2 + position * Math.sin(alpha) - 10, 20, 20);
    }

    @Override
    public void setRedrawing(boolean redrawing) {
        super.setRedrawing(redrawing);
        if (redrawing) {
            drawingAmplitude.bind(Bindings.min(widthProperty(), heightProperty()).divide(2));
            latestSystemTime = System.currentTimeMillis() / 1000.;
        } else
            drawingAmplitude.unbind();
    }

    public double getLatitude() {
        return latitude.get();
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public double getPendantLength() {
        return pendantLength.get();
    }

    public DoubleProperty pendantLengthProperty() {
        return pendantLength;
    }

    public double getAmplitude() {
        return amplitude.get();
    }

    public DoubleProperty amplitudeProperty() {
        return amplitude;
    }

    public double getTimeModificator() {
        return timeModificator.get();
    }

    public DoubleProperty timeModificatorProperty() {
        return timeModificator;
    }

    public void reset() {
        time = 0;
        dotList.clear();
        alpha = 0;
        latestSystemTime = System.currentTimeMillis() / 1000.;
    }
}
