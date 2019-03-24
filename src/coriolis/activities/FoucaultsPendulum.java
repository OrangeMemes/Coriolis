package coriolis.activities;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import coriolis.views.ControlNode;
import coriolis.views.PendulumCanvas;
import coriolis.Activity;

/**
 * Created by maxvl on 19.05.2017.
 */
public class FoucaultsPendulum implements Activity {
    private static final double DEFAULT_TIME_MODIFICATOR = 50;
    private static final double DEFAULT_AMPLITUDE = 1;
    private static final double DEFAULT_LENGTH = 150;
    private static final double DEFAULT_LATITUDE = 90;

    private static FoucaultsPendulum instance = new FoucaultsPendulum();

    private VBox canvasWrapper;
    private PendulumCanvas canvas;
    private TilePane pane;
    private final ControlNode timeModificatorControl;
    private final ControlNode amplitudeControl;
    private final ControlNode lengthControl;
    private final ControlNode latitudeControl;

    public static FoucaultsPendulum getInstance() {
        return instance;
    }

    private FoucaultsPendulum() {
        canvas = new PendulumCanvas();

        timeModificatorControl = new ControlNode("Ускорить время", 1, 100, DEFAULT_TIME_MODIFICATOR);
        canvas.timeModificatorProperty().bind(timeModificatorControl.valueProperty());

        amplitudeControl = new ControlNode("Амплитуда, м", 0.01, 5, DEFAULT_AMPLITUDE);
        canvas.amplitudeProperty().bind(amplitudeControl.valueProperty());

        lengthControl = new ControlNode("Длина подвеса, м", 5, 300, DEFAULT_LENGTH);
        canvas.pendantLengthProperty().bind(lengthControl.valueProperty());

        latitudeControl = new ControlNode("Широта, °", -90, 90, DEFAULT_LATITUDE);
        canvas.latitudeProperty().bind(latitudeControl.valueProperty());

        pane = new TilePane(Orientation.HORIZONTAL,
                timeModificatorControl,
                latitudeControl,
                lengthControl,
                amplitudeControl
                );

        pane.setTileAlignment(Pos.CENTER);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setPadding(new Insets(10));

        canvasWrapper = new VBox(canvas);
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());
        canvasWrapper.setAlignment(Pos.CENTER);
        canvasWrapper.parentProperty().addListener((observable, oldValue, newValue) ->
                canvas.setRedrawing(newValue!=null));
    }


    @Override
    public Region getTop() {
        return canvasWrapper;
    }

    @Override
    public Region getBottom() {
        return pane;
    }

    @Override
    public String getLabel() {
        return "Маятник Фуко";
    }

    @Override
    public void reset() {
        timeModificatorControl.valueProperty().setValue(DEFAULT_TIME_MODIFICATOR);
        latitudeControl.valueProperty().setValue(DEFAULT_LATITUDE);
        lengthControl.valueProperty().setValue(DEFAULT_LENGTH);
        amplitudeControl.valueProperty().setValue(DEFAULT_AMPLITUDE);
        canvas.reset();
    }

}
