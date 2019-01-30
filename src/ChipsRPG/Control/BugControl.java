package ChipsRPG.Control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.List;

import static ChipsRPG.EntityTypes.*;
import static ChipsRPG.EntityTypes.GREENDOOR;
import static ChipsRPG.EntityTypes.YELLOWDOOR;

public class BugControl extends Component {

    private PositionComponent position;

    private int speed = 100;

    private String lastMove = "up";

    public BugControl(){

    }

    @Override
    public void onAdded() {
        super.onAdded();

        AnimationChannel moveX = new AnimationChannel("EnemySpider.png",4,27,27, Duration.seconds(0.2),0,3);

        AnimatedTexture moveXtexture = new AnimatedTexture(moveX);

        moveXtexture.loopAnimationChannel(moveX);

        getEntity().setViewWithBBox(moveXtexture);

    }

    private Point2D before = new Point2D(0,0);
    private Point2D after = new Point2D(0,0);

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        before = position.getValue();

        if(lastMove.equalsIgnoreCase("up")){
            if(canMove(new Point2D(0,-1)) && !canMove(new Point2D(-1,0))){
                moveUp(tpf);
            } if(canMove(new Point2D(-1,0))){
                moveLeft(tpf);
            } if (!canMove(new Point2D(-1,0)) && !canMove(new Point2D(0,-1)) && canMove(new Point2D(1,0))) {
                moveRight(tpf);
            } if (!canMove(new Point2D(-1,0)) && !canMove(new Point2D(0,-1)) && !canMove(new Point2D(1,0))){
                moveDown(tpf);
            }
        } if(lastMove.equalsIgnoreCase("left")){
            if(canMove(new Point2D(-1,0)) && !canMove(new Point2D(0,1))){
                moveLeft(tpf);
            } if(canMove(new Point2D(0,1))){
                moveDown(tpf);
            } if (!canMove(new Point2D(-1,0)) && !canMove(new Point2D(0,1)) && canMove(new Point2D(0,-1))) {
                moveUp(tpf);
            } if (!canMove(new Point2D(-1,0)) && !canMove(new Point2D(0,-1)) && !canMove(new Point2D(0,1))){
                moveRight(tpf);
            }
        } if(lastMove.equalsIgnoreCase("down")){
            if(canMove(new Point2D(0,1)) && !canMove(new Point2D(1,0))){
                moveDown(tpf);
            } if(canMove(new Point2D(1,0))){
                moveRight(tpf);
            } if (!canMove(new Point2D(1,0)) && !canMove(new Point2D(0,1)) && canMove(new Point2D(-1,0))) {
                moveLeft(tpf);
            } if (!canMove(new Point2D(-1,0)) && !canMove(new Point2D(0,1)) && !canMove(new Point2D(1,0))){
                moveUp(tpf);
            }
        } if(lastMove.equalsIgnoreCase("right")){
            if(canMove(new Point2D(1,0)) && !canMove(new Point2D(0,-1))){
                moveRight(tpf);
            } if(canMove(new Point2D(0,-1))){
                moveUp(tpf);
            } if (!canMove(new Point2D(1,0)) && !canMove(new Point2D(0,-1)) && canMove(new Point2D(0,1))) {
                moveDown(tpf);
            } if (!canMove(new Point2D(1,0)) && !canMove(new Point2D(0,-1)) && !canMove(new Point2D(0,1))){
                moveLeft(tpf);
            }
        }

        after = position.getValue();

        if (after.getX() < before.getX()) {
            entity.setRotation(180);
        }
        if (after.getX() > before.getX()) {
            entity.setRotation(0);
        }
        if (after.getY() < before.getY()) {
            entity.setRotation(-90);
        }
        if (after.getY() > before.getY()) {
            entity.setRotation(90);
        }

    }

    public void moveUp(double tpf){
            position.translateY(-speed*tpf);
            lastMove = "up";
            //entity.setRotation(-90);
    }

    public void moveDown(double tpf){

            position.translateY(speed*tpf);
            lastMove = "down";
            //entity.setRotation(90);

    }

    public void moveLeft(double tpf){

            position.translateX(-speed*tpf);
            lastMove = "left";

    }

    public void moveRight(double tpf){

            position.translateX(speed*tpf);
            lastMove = "right";
            //entity.setRotation(0);

    }



    private boolean canMove(Point2D direction) {

        Point2D newPosition = position.getValue().add(direction);

        boolean bugCanMove = true;

        List<Entity> e = FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(newPosition.getX(),newPosition.getY(),30,30));

        for (Entity entity:e) {
            if (entity.isType(WALL)
                    || entity.isType(TANKUP)
                    || entity.isType(TANKDOWN)
                    || entity.isType(TANKLEFT)
                    || entity.isType(TANKRIGHT)
                    || entity.isType(DIRTBLOCK)){
                bugCanMove = false;
            }
        }

        return bugCanMove;

    }





}
