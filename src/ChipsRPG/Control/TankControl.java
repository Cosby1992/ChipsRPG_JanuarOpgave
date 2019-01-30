package ChipsRPG.Control;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static ChipsRPG.EntityTypes.*;


//Tank textureUP size 29 witdh, 30 heigh
//

public class TankControl extends Component {


    private boolean wallHit = false;
    private int speed = -100;
    private String lastMove = "";
    private boolean activated = false;

    private AnimationChannel tankUp, tankDown, tankLeft, tankRight;


    public TankControl(){

        tankUp = new AnimationChannel("Tank.png",8,29,30, Duration.seconds(0.5),0,1);
        tankRight = new AnimationChannel("Tank.png",8,29,30, Duration.seconds(0.5),2,3);
        tankDown = new AnimationChannel("Tank.png",8,29,30, Duration.seconds(0.5),4,5);
        tankLeft = new AnimationChannel("Tank.png",8,29,30, Duration.seconds(0.5),6,7);



    }


    @Override
    public void onAdded() {
        super.onAdded();

        AnimatedTexture textureUp = new AnimatedTexture(tankUp);
        AnimatedTexture textureDown = new AnimatedTexture(tankDown);
        AnimatedTexture textureRight = new AnimatedTexture(tankRight);
        AnimatedTexture textureLeft = new AnimatedTexture(tankLeft);

        if(entity.getType()==TANKUP){
            entity.setView(textureUp);
            entity.setScaleX(0.8);
            entity.setScaleX(0.8);
        }

        if (entity.getType()==TANKDOWN){
            entity.setView(textureDown);
            entity.setScaleX(0.8);
            entity.setScaleX(0.8);
        }

        if(entity.getType()==TANKLEFT){
            entity.setView(textureLeft);
            entity.setScaleX(0.8);
            entity.setScaleX(0.8);
        }

        if(entity.getType()==TANKRIGHT){
            entity.setView(textureRight);
            entity.setScaleX(0.8);
            entity.setScaleX(0.8);
        }


    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);



        if(activated && !wallHit && entity.isType(TANKUP)){
            entity.translateY(speed*tpf);
            if(speed == -100 ){
                entity.setScaleY(1);
            } else {
                entity.setScaleY(-1);
            }
        } else if (activated && !wallHit && entity.isType(TANKDOWN)){
            entity.translateY(-speed*tpf);
            if(speed == -100 ){
                entity.setScaleY(1);
            } else {
                entity.setScaleY(-1);
            }
        } else if (activated && !wallHit && entity.isType(TANKLEFT)){
            entity.translateX(-speed*tpf);
            if(speed == -100 ){
                entity.setScaleX(-1);
            } else {
                entity.setScaleX(1);
            }
        } else if (activated && !wallHit && entity.isType(TANKRIGHT)){
            entity.translateX(speed*tpf);
            if(speed == -100 ){
                entity.setScaleX(-1);
            } else {
                entity.setScaleX(1);
            }
        }

    }


    public boolean isWallHit() {
        return wallHit;
    }

    public void setWallHit(boolean wallHit) {
        this.wallHit = wallHit;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
