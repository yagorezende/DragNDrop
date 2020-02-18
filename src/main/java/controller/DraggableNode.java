package controller;

import java.io.IOException;
import java.util.UUID;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import model.DragContainer;
import model.DragIconType;

public class DraggableNode extends AnchorPane{
    @FXML AnchorPane root_pane;
    @FXML private Label tittle_bar;
    @FXML private Label close_button;
    @FXML private AnchorPane left_link_handle;
    @FXML private AnchorPane right_link_handle;


    // Link drag event handlers
    private EventHandler <MouseEvent> linkHandleDragDetected;
    private EventHandler <DragEvent> linkHandleDragDropped;
    private EventHandler <DragEvent> contextLinkDragOver;
    private EventHandler <DragEvent> contextLinkDragDropped;

    // Node drag event handlers
    private EventHandler <DragEvent> contextDragOver;
    private EventHandler <DragEvent> contextDragDropped;

    private NodeLink dragLink = null;
    private AnchorPane right_pane = null;
    private AnchorPane left_pane = null;

    private DragIconType type = null;

    private Point2D dragOffset = new Point2D(0.0, 0.0);

    private final DraggableNode self;

    public DraggableNode(){
        self = this;

        setId(UUID.randomUUID().toString());

        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/layout/draggable_node.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        }catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize(){
        buildNodeDragHandlers();
        buildLinkDragHandlers();

        left_link_handle.setOnDragDetected(linkHandleDragDetected);
        right_link_handle.setOnDragDetected(linkHandleDragDetected);

        left_link_handle.setOnDragDropped(linkHandleDragDropped);
        right_link_handle.setOnDragDropped(linkHandleDragDropped);

        dragLink = new NodeLink();
        dragLink.setVisible(false);

        parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                right_pane = (AnchorPane) getParent();
            }
        });

    }

    public void buildNodeDragHandlers(){
        tittle_bar.setOnDragDetected(
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    // clean references
                    getParent().setOnDragOver(null);
                    getParent().setOnDragDropped(null);

                    // set the new ones
                    getParent().setOnDragOver(contextDragOver);
                    getParent().setOnDragDropped(contextDragDropped);

                    // begin drag ops
                    dragOffset = new Point2D(event.getX(), event.getY());

                    relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                    ClipboardContent content = new ClipboardContent();
                    DragContainer container = new DragContainer();

                    container.addData("type", type.toString());
                    content.put(DragContainer.DragNode, container);

                    startDragAndDrop(TransferMode.ANY).setContent(content);

                    event.consume();
                }
            }
        );

        // create the drag handler
        contextDragOver = new EventHandler <DragEvent>() {
            //dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                event.consume();
            }
        };

        //dragdrop for node dragging
        contextDragDropped = new EventHandler <DragEvent> () {
            // Clean all handlers on drop
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                event.setDropCompleted(true);
                event.consume();
            }
        };

        //close button click
        close_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AnchorPane parent = (AnchorPane) self.getParent();
                parent.getChildren().remove(self);
            }
        });
    }

    private void buildLinkDragHandlers(){
        linkHandleDragDetected = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(contextLinkDragOver);
                getParent().setOnDragDropped(contextLinkDragDropped);

                //Set up user-draggable link
                // the index represents the index of the obj in the scene layer
                right_pane.getChildren().add(0, dragLink);

                dragLink.setVisible(false);

                Point2D p = new Point2D(
                  getLayoutX() + (getWidth()/2.0),
                  getLayoutY() + (getHeight()/2.0)
                );

                dragLink.setStart(p);

                // Drag Content Code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

//                AnchorPane link_handle = (AnchorPane) event.getSource();
                // Node get from: AnchorPane > HBox > VBox > Root AnchorPane
//                DraggableNode parent = (DraggableNode) link_handle.getParent().getParent().getParent();

                container.addData("source", getId());

                content.put(DragContainer.AddLink, container);
                startDragAndDrop(TransferMode.ANY).setContent(content);

//                parent.startDragAndDrop(TransferMode.ANY).setContent(content);

                event.consume();
            }
        };

        linkHandleDragDropped = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //get the drag data.  If it's null, abort.
                //This isn't the drag event we're looking for.
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container == null) return;

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                dragLink.setVisible(false);
                right_pane.getChildren().remove(0);

                AnchorPane link_handle = (AnchorPane) event.getSource();

                ClipboardContent content = new ClipboardContent();

//                DraggableNode parent = (DraggableNode) link_handle.getParent().getParent().getParent();

                container.addData("target", getId());
                content.put(DragContainer.AddLink, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                event.consume();
            }
        };

        contextLinkDragOver = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                // Relocate user-draggable link
                if(!dragLink.isVisible()) dragLink.setVisible(true);

                // set the end of the line to the cursor coordinates
                dragLink.setEnd(new Point2D(event.getX(), event.getY()));

                event.consume();
            }
        };

        contextLinkDragDropped = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                dragLink.setVisible(false);
                right_pane.getChildren().remove(0);

                event.setDropCompleted(true);
                event.consume();
            }
        };
    }

    public void relocateToPoint (Point2D p) {
        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
            (int) (localCoords.getX() - dragOffset.getX()),
            (int) (localCoords.getY() - dragOffset.getY())
        );
    }

    public void setType(DragIconType type){
        this.type = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");

        switch (this.type) {

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

    public DragIconType getType() {
        return type;
    }
}
