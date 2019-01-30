package ChipsRPG.Control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.List;

import static ChipsRPG.EntityTypes.*;
import static ChipsRPG.EntityTypes.TANKRIGHT;

public class DirtBlockControl extends Component {

    private PositionComponent position;
    private String lastMove = "";
    private int speed = 100;

    public DirtBlockControl(){

    }

    @Override
    public void onUpdate(double tpf){

    }

    public void moveUp(double tpf) {
        if (canMove(new Point2D(0, -1))) {
            position.translateY(-speed * tpf);
            lastMove = "up";
            //entity.setRotation(-90);

        }
    }

    public void moveDown(double tpf){
        if(canMove(new Point2D(0,1))) {
            position.translateY(speed * tpf);
            lastMove = "down";
            //entity.setRotation(90);
        }
    }

    public void moveLeft(double tpf){
        if(canMove(new Point2D(-1,0))) {
            position.translateX(-speed * tpf);
            lastMove = "left";
        }
    }

    public void moveRight(double tpf){
        if(canMove(new Point2D(1,0))) {
            position.translateX(speed * tpf);
            lastMove = "right";
            //entity.setRotation(0);
        }
    }

    private boolean canMove(Point2D direction) {

        Point2D newPosition = position.getValue().add(direction);

        boolean dirtBlockCanMove = true;

        List<Entity> e = FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(newPosition.getX(),newPosition.getY(),30,30));

        for (Entity entity:e) {
            if (entity.isType(WALL)
                    || entity.isType(TANKUP)
                    || entity.isType(TANKDOWN)
                    || entity.isType(TANKLEFT)
                    || entity.isType(TANKRIGHT)) {
                dirtBlockCanMove = false;
            }
        }

        return dirtBlockCanMove;

    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }
}

