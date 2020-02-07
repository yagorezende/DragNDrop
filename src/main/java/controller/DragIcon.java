package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.DragIconType;

import java.io.IOException;

public class DragIcon extends AnchorPane {

    private DragIconType mType;

    public DragIcon() {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/layout/drag_icon.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        @FXML
        private void initialize() {}

    public DragIconType getType() {
        return mType;
    }

    public void setType(DragIconType type){
        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");
        switch (mType) {

            case blue:
                getStyleClass().add("icon-blue");
                break;

            case red:
                getStyleClass().add("icon-red");
                break;

            case green:
                getStyleClass().add("icon-green");
                break;

            case grey:
                getStyleClass().add("icon-grey");
                break;

            case purple:
                getStyleClass().add("icon-purple");
                break;

            case yellow:
                getStyleClass().add("icon-yellow");
                break;

            case black:
                getStyleClass().add("icon-black");
                break;
            default:
                break;
        }
    }
}