package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class NodeLink extends AnchorPane {
    @FXML Line node_link;

    public NodeLink(){
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
}
