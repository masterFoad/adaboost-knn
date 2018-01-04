package view.scene_util;

import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BubbleChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

public class CircularBubbleChart<X, Y> extends BubbleChart<X, Y> {

    public CircularBubbleChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
    }

    public CircularBubbleChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        getData().stream().flatMap(series -> series.getData().stream())
            .map(Data::getNode)
            .map(StackPane.class::cast)
            .map(StackPane::getShape)
            .map(Ellipse.class::cast)
            .forEach(ellipse -> ellipse.setRadiusY(ellipse.getRadiusX()));
    }
}