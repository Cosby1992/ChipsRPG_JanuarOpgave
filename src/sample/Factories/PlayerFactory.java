package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import sample.Control.PlayerControl;

import static sample.EntityTypes.Type.PLAYER;

public class PlayerFactory implements EntityFactory {
    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {

        System.out.println("A player object was spawned");

        return  Entities.builder()
                .type(PLAYER)
                .from(data)
                .at(data.getX(),data.getY())
                .bbox(new HitBox(BoundingShape.box(16,29)))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();

    }

}
