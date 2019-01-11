
package sample;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.util.Duration;
import sample.Control.PlayerControl;

import static com.almasb.fxgl.app.DSLKt.texture;
import static sample.EntityTypes.Type.*;

public class InsideFactory implements EntityFactory {

    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {

        System.out.println("A player object was spawned");

        return  Entities.builder()
                .type(PLAYER)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(24,30)))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();

    }

    @Spawns("wall")
    public static Entity newWall(SpawnData data){
        System.out.println("New wall object was created");
        return Entities.builder()
                .type(WALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data){
        System.out.println("New coin was spawned");
        return Entities.builder()
                .type(CHIP)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("chip.png")
                .build();
    }

    @Spawns("redKey")
    public static Entity newRedKey(SpawnData data){
        System.out.println("New redKey object was created");
        return Entities.builder()
                .type(REDKEY)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("redKey.png")
                .build();
    }

    @Spawns("blueKey")
    public static Entity newBlueKey(SpawnData data){
        System.out.println("New blueKey object was created");
        return Entities.builder()
                .type(BLUEKEY)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("blueKey.png")
                .build();
    }

    @Spawns("blueDoor")
    public static Entity newBlueDoor(SpawnData data){
        System.out.println("New blueDoor object was created");
        return Entities.builder()
                .type(BLUEDOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("blueDoor.png")
                .build();
    }

    @Spawns("redDoor")
    public static Entity newredDoor(SpawnData data){
        System.out.println("New redDoor object was created");
        return Entities.builder()
                .type(REDDOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("redDoor.png")
                .build();
    }

    @Spawns("endZone")
    public static Entity newEndZone(SpawnData data){
        System.out.println("New endZone object was created");
        return Entities.builder()
                .type(ENDZONE)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .viewFromAnimatedTexture(texture("endZone4.png").toAnimatedTexture(4,Duration.seconds(0.2)))
                .with(new CollidableComponent(true))
                .build();
    }

}


