package sample.Control;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import java.util.ArrayList;

public class PlayerControl extends Component {

    private boolean canMove = true;
    private int speed = 100;
    private ArrayList<String> inventory = new ArrayList<String>();
    private boolean onIce = false;
    private boolean inWater = false;
    private String lastMove = "up";


    private AnimatedTexture texture;
    private AnimationChannel animIdleForward;

    //Constructor
    public PlayerControl() {
        animIdleForward = new AnimationChannel("PlayerForwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);

        texture = new AnimatedTexture(animIdleForward);
        System.out.println("playerControl");
    }

    @Override
    public void onAdded() {
        entity.setView(texture);

        System.out.println("on added");

    }

    @Override
    public void onUpdate(double tpf) {

        if(canMove && !inWater && !onIce){
            speed = 100;
        } else if(inWater && canMove && !onIce){
            speed = 50;
        } else if (onIce && canMove && !inWater){
            speed = 200;
        } else{
            speed = 0;
        }


        if(isOnIce() && !getInventory().contains("ICEBOOTS"))
        //Ice movement (constant while on ice)
        if (getLastMove().equalsIgnoreCase("right")){
            entity.translateX(speed * tpf);
            setLastMove("right");
        } else if (getLastMove().equalsIgnoreCase("left")){
            entity.translateX(-speed * tpf);
            setLastMove("left");
        } else if (getLastMove().equalsIgnoreCase("up")){
            entity.translateY(-speed * tpf);
            setLastMove("up");
        } else if (getLastMove().equalsIgnoreCase("down")){
            entity.translateY(speed * tpf);
            setLastMove("down");
        }

    }

    public void moveRight(double tpf) {
        if (!isOnIce()) {
            entity.translateX(speed * tpf);
            setLastMove("right");
        }
    }

    public void moveLeft(double tpf) {
        if(!isOnIce()) {
            entity.translateX(-speed * tpf);
            setLastMove("left");
        }
    }

    public void moveUp(double tpf) {
        if(!isOnIce()) {
            entity.translateY(-speed * tpf);
            setLastMove("up");
        }
    }

    public void moveDown(double tpf){
        if(!isOnIce()){
        entity.translateY(speed * tpf);
        setLastMove("down");
        }
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


    //getters and setters
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

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

    public void playerInfo(){
        System.out.println("player in water = " + isInWater()
                + "player on ice = " + isOnIce()
                + "player can move = " + isCanMove()
                + "players last move = " + getLastMove()
                + "inventory contains = " + getInventory().toString()
                + "player speed = " + getSpeed());
    }

}