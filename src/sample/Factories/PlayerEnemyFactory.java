package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sample.Control.PlayerControl;

import static sample.EntityTypes.Type.ENEMYTEST;
import static sample.EntityTypes.Type.PLAYER;

public class PlayerEnemyFactory implements EntityFactory {

    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {

        System.out.println("A player object was spawned");

        return  Entities.builder()
                .type(PLAYER)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(16,29)))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();

    }

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
