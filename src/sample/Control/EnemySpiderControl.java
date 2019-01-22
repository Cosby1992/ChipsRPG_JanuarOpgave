package sample.Control;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class EnemySpiderControl extends Component {
    private int speed = 100;
    private boolean colliding = false;

    public EnemySpiderControl(){

    }

    @Override
    public void onAdded() {
        super.onAdded();

        AnimationChannel moveX = new AnimationChannel("EnemySpider.png",4,27,27, Duration.seconds(0.2),0,3);

        AnimatedTexture moveXtexture = new AnimatedTexture(moveX);

        moveXtexture.loopAnimationChannel(moveX);

        getEntity().setViewWithBBox(moveXtexture);

        entity.translateX(-1);
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        moveOppisite(tpf);

    }

    public void moveOppisite(double tpf){
        if(isColliding()==true){
            if(getSpeed()==100){
                getEntity().setScaleX(-1);

                setSpeed(-100);
            } else {
                getEntity().setScaleX(1);
                setSpeed(100);
            }
        }
        entity.translateX(speed*tpf);
    }


    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    public boolean isColliding() {
        return colliding;
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }
}
