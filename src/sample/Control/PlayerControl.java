package sample.Control;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import java.util.ArrayList;

public class PlayerControl extends Component {

    private boolean canMove = true;
    private int speed = 100;
    private ArrayList<Entity> inventory = new ArrayList<Entity>();


    private AnimatedTexture texture;
    private AnimationChannel animIdleForward, animForward, animIdleRight, animRight,animIdleLeft, animLeft, animBackward, animIdleBackward;

    //Constructor
    public PlayerControl() {
        animIdleForward = new AnimationChannel("forward8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animForward = new AnimationChannel("forward8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);
        animIdleBackward = new AnimationChannel("backwards8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animBackward = new AnimationChannel("backwards8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);
        animIdleLeft = new AnimationChannel("left8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animLeft = new AnimationChannel("left8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);
        animIdleRight = new AnimationChannel("right8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animRight = new AnimationChannel("right8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);


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

        if(canMove){
            speed = 100;
        }else{
            speed = 0;
        }
    }

    public void moveRight(double tpf) {
            entity.translateX(speed * tpf);
            //texture.loopAnimationChannel(animRight);
    }

    public void moveLeft(double tpf) {

        entity.translateX(-speed * tpf);
        //texture.loopAnimationChannel(animLeft);
    }

    public void moveUp(double tpf) {

        entity.translateY(-speed * tpf);
        //texture.loopAnimationChannel(animForward);
    }

    public void moveDown(double tpf){

        entity.translateY(speed * tpf);
        //texture.loopAnimationChannel(animBackward);
    }

    //player inventory as ArrayList
    public void addInventory(Entity collectableEntity){
        inventory.add(collectableEntity);
        for (int i = 0; i <inventory.size() ; i++) {
            System.out.println("added to inventory" + inventory.get(i).toString());
        }

    }


    //getters and setters
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        System.out.println("speed is set to = " + speed);
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
}