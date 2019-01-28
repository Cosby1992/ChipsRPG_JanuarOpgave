package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;
import sample.Control.PlayerControl;

import static sample.EntityTypes.PLAYER;

public class PlayerFactory implements EntityFactory {

    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {

        AnimationChannel animIdleForward;

        animIdleForward = new AnimationChannel("newUpAnimated.png", 8, 15, 26,Duration.seconds(1), 0, 0);

        AnimatedTexture view = new AnimatedTexture(animIdleForward);

        System.out.println("A player object was spawned");

        return  Entities.builder()
                .type(PLAYER)
                .from(data)
                .at(data.getX(),data.getY())
                .viewFromNodeWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();

    }

}
