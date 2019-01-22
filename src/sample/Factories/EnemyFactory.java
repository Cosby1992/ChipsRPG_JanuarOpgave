package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import sample.Control.EnemySpiderControl;

import static sample.EntityTypes.Type.ENEMYTEST;

public class EnemyFactory implements EntityFactory {

    @Spawns("enemyTest")
    public Entity newEnemyTest(SpawnData data){
        System.out.println("A new enemyTest object was created");

        return Entities.builder()
                .type(ENEMYTEST)
                .from(data)
                .with(new CollidableComponent(true))
                .with(new EnemySpiderControl())
                .build();
    }

}
