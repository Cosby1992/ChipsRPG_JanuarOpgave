package ChipsRPG.Factories;

import ChipsRPG.Control.BugControl;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import ChipsRPG.Control.EnemySpiderControl;

import static ChipsRPG.EntityTypes.BUG;
import static ChipsRPG.EntityTypes.ENEMYTEST;

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

    @Spawns("bug")
    public Entity newBug(SpawnData data){
        System.out.println("A new bug object was created");

        return Entities.builder()
                .type(BUG)
                .from(data)
                .with(new CollidableComponent(true))
                .with(new BugControl())
                .build();
    }


}
