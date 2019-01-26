package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import sample.Control.TankControl;

import static sample.EntityTypes.Type.*;

public class UtilityFactory implements EntityFactory {

    @Spawns("tankUp")
    public Entity newTankUp(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TANKUP)
                .bbox(new HitBox(BoundingShape.box(28,28)))
                .with(new CollidableComponent(true))
                .with(new TankControl())
                .build();
    }

    @Spawns("tankDown")
    public Entity newTankDown(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TANKDOWN)
                .bbox(new HitBox(BoundingShape.box(28,28)))
                .with(new CollidableComponent(true))
                .with(new TankControl())
                .build();
    }

    @Spawns("tankLeft")
    public Entity newTankLeft(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TANKLEFT)
                .bbox(new HitBox(BoundingShape.box(28,28)))
                .with(new CollidableComponent(true))
                .with(new TankControl())
                .build();
    }

    @Spawns("tankRight")
    public Entity newTankRight(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TANKRIGHT)
                .bbox(new HitBox(BoundingShape.box(28,28)))
                .with(new CollidableComponent(true))
                .with(new TankControl())
                .build();
    }

    @Spawns("tankActivator")
    public Entity newTankActivator(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TANKACTIVATOR)
                .bbox(new HitBox(BoundingShape.box(10,10)))
                .with(new CollidableComponent(true))
                .build();
    }
}
