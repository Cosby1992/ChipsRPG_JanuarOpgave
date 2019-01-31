package ChipsRPG.Control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.Timer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import static ChipsRPG.EntityTypes.*;


public class PlayerControl extends Component {

    private Timer timer = new Timer();
    private PositionComponent position;

    private int speed = 100;
    //private boolean canMove = true;
    private ArrayList<String> inventory = new ArrayList<String>();
    private boolean onIce = false;
    private boolean inWater = false;
    private String lastMove = "up";
    private boolean wallHit = false;



    private AnimatedTexture texture;
    private AnimationChannel animIdleForward, animIdleBackward, animIdleLeft, animIdleRight;
    private AnimationChannel animForward, animBackward, animLeft, animRight, inWaterUp, inWaterDown, inWaterLeft, inWaterRight, onIceAnim;

    //Constructor
    public PlayerControl() {
        // initializing AnimationChannels to use with walking and swimming

        animForward = new AnimationChannel("newUpAnimated.png", 8, 15, 26, Duration.seconds(0.5), 0, 7);
        animBackward = new AnimationChannel("newDownAnimated.png", 8, 15, 26, Duration.seconds(0.5), 0, 7);
        animLeft = new AnimationChannel("newLeftAnimated.png", 8, 15, 26, Duration.seconds(0.5), 0, 7);
        animRight = new AnimationChannel("newRightAnimated.png", 8, 15, 26, Duration.seconds(0.5), 0, 7);

        animIdleForward = new AnimationChannel("newUpAnimated.png", 8, 15, 26, Duration.seconds(1), 0, 0);
        animIdleBackward = new AnimationChannel("newDownAnimated.png", 8, 15, 26, Duration.seconds(1), 0, 0);
        animIdleLeft = new AnimationChannel("newLeftAnimated.png", 8, 15, 26, Duration.seconds(1), 0, 0);
        animIdleRight = new AnimationChannel("newRightAnimated.png", 8, 15, 26, Duration.seconds(1), 0, 0);

        inWaterUp = new AnimationChannel("inWaterUp.png",2,18,19,Duration.seconds(0.5),0,1);
        inWaterDown = new AnimationChannel("inWaterDown.png",2,18,19,Duration.seconds(0.5),0,1);
        inWaterLeft = new AnimationChannel("inWaterLeft.png",2,18,19,Duration.seconds(0.5),0,1);
        inWaterRight = new AnimationChannel("inWaterRight.png",2,18,19,Duration.seconds(0.5),0,1);

        onIceAnim = new AnimationChannel("onIce.png", 4,11,26,Duration.seconds(0.5),0,3);

        texture = new AnimatedTexture(animIdleForward);

        System.out.println("playerControl initialized");
    }

    @Override
    public void onAdded() {

        inventory.clear();
        entity.setViewWithBBox(texture);

        System.out.println("player - on added");

    }


    @Override
    public void onUpdate(double tpf) {

        if(!inWater && !onIce){
            setSpeed(100);
            setIdleTexture(FXGL.getInput());
        } else if(inWater && !onIce){
            setSpeed(50);
        } else if (onIce && !inWater){
            setSpeed(225);
        } else{
            setSpeed(100);
        }


        if(onIce && !getInventory().contains("ICEBOOTS"))
        //Ice movement (constant while on ice)
        if (lastMove.equalsIgnoreCase("right")){
            entity.translateX(speed * tpf);
            setLastMove("right");
        } else if (lastMove.equalsIgnoreCase("left")){
            entity.translateX(-speed * tpf);
            setLastMove("left");
        } else if (lastMove.equalsIgnoreCase("up")){
            entity.translateY(-speed * tpf);
            setLastMove("up");
        } else if (lastMove.equalsIgnoreCase("down")){
            entity.translateY(speed * tpf);
            setLastMove("down");
        }
/*
        if(onIce && wallHit && !getInventory().contains("ICEBOOTS"))
            //Ice movement (constant while on ice)
            if (lastMove.equalsIgnoreCase("right")){
                entity.translateX(-speed * tpf);
                setLastMove("left");
            } else if (lastMove.equalsIgnoreCase("left")){
                entity.translateX(speed * tpf);
                setLastMove("right");
            } else if (lastMove.equalsIgnoreCase("up")){
                entity.translateY(speed * tpf);
                setLastMove("down");
            } else if (lastMove.equalsIgnoreCase("down")){
                entity.translateY(-speed * tpf);
                setLastMove("up");
            }
*/
    }

    public void moveRight(double tpf) {
        if (!isOnIce() && canMove(new Point2D(2,0))) {
            position.translateX(speed * tpf);
            if (isInWater() && getTexture().getAnimationChannel() != inWaterRight){
                getTexture().loopAnimationChannel(inWaterRight);
            } else if (!isInWater() && getTexture().getAnimationChannel() != animRight) {
                getTexture().loopAnimationChannel(animRight);
            }
            setLastMove("right");
        }
    }

    public void moveLeft(double tpf) {
        if(!isOnIce() && canMove(new Point2D(-2,0))) {
            position.translateX(-speed * tpf);
            if (isInWater() && getTexture().getAnimationChannel() != inWaterLeft){
                getTexture().loopAnimationChannel(inWaterLeft);
            } else if (!isInWater() && getTexture().getAnimationChannel() != animLeft) {
                getTexture().loopAnimationChannel(animLeft);
            }
            setLastMove("left");
        }
    }

    public void moveUp(double tpf) {
        if(!isOnIce() && canMove(new Point2D(0,-2))) {
            position.translateY(-speed * tpf);
            if (isInWater() && getTexture().getAnimationChannel() != inWaterUp){
                getTexture().loopAnimationChannel(inWaterUp);
            } else if (!isInWater() && getTexture().getAnimationChannel() != animForward){
                getTexture().loopAnimationChannel(animForward);
            }
            setLastMove("up");
        }
    }

    public void moveDown(double tpf){
        if(!isOnIce() && canMove(new Point2D(0,2))){
        position.translateY(speed * tpf);
            if (isInWater() && getTexture().getAnimationChannel() != inWaterDown){
                getTexture().loopAnimationChannel(inWaterDown);
            } else if (!isInWater() && getTexture().getAnimationChannel() != animBackward){
                getTexture().loopAnimationChannel(animBackward);
            }
        setLastMove("down");
        }


    }

    private int doorSoundCounter = 0;
    private boolean canMove(Point2D direction) {

        Point2D newPosition = position.getValue().add(direction);

        boolean playerCanMove = true;

        List<Entity> e = FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(newPosition.getX(),newPosition.getY(),15,26));

        for (Entity entity:e) {
            if (entity.isType(WALL)
                    || entity.isType(TANKUP)
                    || entity.isType(TANKDOWN)
                    || entity.isType(TANKLEFT)
                    || entity.isType(TANKRIGHT)){
                doorSoundCounter = 0;
                playerCanMove = false;
            } if(entity.isType(REDDOOR) && !inventory.contains("REDKEY")
            || entity.isType(BLUEDOOR) && !inventory.contains("BLUEKEY")
            || entity.isType(YELLOWDOOR) && !inventory.contains("YELLOWKEY")
            || entity.isType(GREENDOOR) && !inventory.contains("GREENKEY")){
                if (doorSoundCounter%20==0 || doorSoundCounter==0) {
                    FXGL.getAudioPlayer().playSound("Locked_Door.wav");


                }

                System.out.println(doorSoundCounter);
                doorSoundCounter++;

                for (String s : inventory) {
                    System.out.println("Inventory contains: " + s);
                }

                playerCanMove = false;
            }
        }

        return playerCanMove;

    }




    //player inventory as ArrayList
    public void addInventory(Entity collectableEntity){
        getInventory().add(collectableEntity.getType().toString());

        for (int i = 0; i < getInventory().size(); i++) {
            System.out.println(getInventory().get(i));
        }

    }

    public ArrayList<String> getInventory(){
        return inventory;
    }

    public void setIdleTexture(Input input){
        if(!input.isHeld(KeyCode.W)
                &&!input.isHeld(KeyCode.A)
                &&!input.isHeld(KeyCode.D)
                &&!input.isHeld(KeyCode.S)){
            String lastMove = getLastMove();
            switch (lastMove){
                case "right": getTexture().loopAnimationChannel(animIdleRight); break;
                case "left": getTexture().loopAnimationChannel(animIdleLeft); break;
                case "up": getTexture().loopAnimationChannel(animIdleForward); break;
                case "down": getTexture().loopAnimationChannel(animIdleBackward); break;
            }
        }
    }

    //getters and setters
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
/*
    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
*/
    public AnimatedTexture getTexture() {
        return texture;
    }

    public void setTexture(AnimatedTexture texture) {
        this.texture = texture;
    }

    public void setInventory(ArrayList<String> inventory) {
        this.inventory = inventory;
    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public boolean isOnIce() {
        return onIce;
    }

    public void setOnIce(boolean onIce) {
        this.onIce = onIce;
    }

    public boolean isInWater() {
        return inWater;
    }

    public void setInWater(boolean inWater) {
        this.inWater = inWater;
    }

    public boolean isWallHit() {
        return wallHit;
    }

    public void setWallHit(boolean wallHit) {
        this.wallHit = wallHit;
    }
/*
    public void playerInfo(){
        System.out.println("player in water = " + isInWater()
                + "player on ice = " + isOnIce()
                + "player can move = " + isCanMove()
                + "players last move = " + getLastMove()
                + "inventory contains = " + getInventory().toString()
                + "player speed = " + getSpeed());
    }
*/
}