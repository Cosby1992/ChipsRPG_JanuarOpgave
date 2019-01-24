package sample.Control;

import com.almasb.fxgl.entity.component.Component;

public class DirtBlockControl extends Component {

    private int speed = 100;

    public DirtBlockControl(){

    }

    @Override
    public void onUpdate(double tpf){

    }


    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}

