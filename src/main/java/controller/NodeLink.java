package controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.UUID;

public class NodeLink extends AnchorPane {
    @FXML Line node_link;

    public NodeLink(){
        setId(UUID.randomUUID().toString());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/node_link.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize(){
    }

    public void setStart(Point2D startPoint){
        node_link.setStartX(startPoint.getX());
        node_link.setStartY(startPoint.getY());
    }

    public void setEnd(Point2D endPoint){
        node_link.setEndX(endPoint.getX());
        node_link.setEndY(endPoint.getY());
    }

    public void bindEnds(DraggableNode source, DraggableNode target){
        node_link.startXProperty().bind(Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));
        node_link.startYProperty().bind(Bindings.add(source.layoutYProperty(), (source.getWidth() / 2.0)));
        node_link.endXProperty().bind(Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));
        node_link.endYProperty().bind(Bindings.add(target.layoutYProperty(), (target.getWidth() / 2.0)));
    }
}
