package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sample.Control.EnemyControl;
import sample.Control.PlayerControl;

import static sample.EntityTypes.Type.ENEMYTEST;
import static sample.EntityTypes.Type.PLAYER;

public class EnemyFactory implements EntityFactory {

    @Spawns("enemyTest")
    public Entity newEnemyTest(SpawnData data){
        System.out.println("A new enemyTest object was created");

        return Entities.builder()
                .type(ENEMYTEST)
                .from(data)
                .bbox(new HitBox(BoundingShape.circle(12)))
                .viewFromNode(new Circle(12,Color.valueOf("BLACK")))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }

}
