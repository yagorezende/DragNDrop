package controller;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
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
    private EventHandler <DragEvent> contextLinkDragDrop;

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
                getParent().setOnDragDropped(contextLinkDragDrop);

                // Drag Content Code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                AnchorPane link_handle = (AnchorPane) event.getSource();
                // Node get from: AnchorPane > HBox > VBox > Root AnchorPane
                DraggableNode parent = (DraggableNode) link_handle.getParent().getParent().getParent();

                container.addData("source", parent.getType().toString());

                content.put(DragContainer.AddLink, container);

                parent.startDragAndDrop(TransferMode.ANY).setContent(content);

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

                AnchorPane link_handle = (AnchorPane) event.getSource();
                DraggableNode parent = (DraggableNode) link_handle.getParent().getParent().getParent();

                ClipboardContent content = new ClipboardContent();

                container.addData("target", parent.getType().toString());

                event.getDragboard().setContent(content);

                event.setDropCompleted(true);
                event.consume();
            }
        };

        contextLinkDragOver = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        };

        contextLinkDragDrop = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

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
