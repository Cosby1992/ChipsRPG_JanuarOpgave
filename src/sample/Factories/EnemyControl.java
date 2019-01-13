package sample.Factories;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.component.Component;

public class EnemyControl extends Component {
    private int speed = 100;
    private int damage = 10;
    private boolean colliding = false;

    public EnemyControl(){

    }

    @Override
    public void onAdded() {
        super.onAdded();

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
                setSpeed(-100);
            } else {
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

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isColliding() {
        return colliding;
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }
}
