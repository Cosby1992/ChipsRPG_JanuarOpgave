
package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.util.Duration;
import sample.Control.PlayerControl;

import static com.almasb.fxgl.app.DSLKt.texture;
import static sample.EntityTypes.Type.*;

//Player texture size 16*29 px.
//Animated texture size 128*29 px. (frames = 8)

public class CollectiblesFactory implements EntityFactory {

    //CHIPS------------------------------------------------------------------------------------------------------------

    @Spawns("chip")
    public Entity newChip(SpawnData data){
        System.out.println("New chip was spawned");
        return Entities.builder()
                .type(CHIP)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("chip.png")
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

    //KEYS-------------------------------------------------------------------------------------------------------------

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

    @Spawns("greenKey")
    public static Entity newGreenKey(SpawnData data){
        System.out.println("New greenKey object was created");
        return Entities.builder()
                .type(GREENKEY)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("greenKey.png")
                .build();
    }

    @Spawns("yellowKey")
    public static Entity newYellowKey(SpawnData data) {
        System.out.println("New yellowKey object was created");
        return Entities.builder()
                .type(YELLOWKEY)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("yellowKey.png")
                .build();
    }

    //DOORS------------------------------------------------------------------------------------------------------------

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

    @Spawns("greenDoor")
    public static Entity newGreenDoor(SpawnData data){
        System.out.println("New greenDoor object was created");
        return Entities.builder()
                .type(GREENDOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("greenDoor.png")
                .build();
    }

    @Spawns("yellowDoor")
    public static Entity newYellowDoor(SpawnData data){
        System.out.println("New yellowDoor object was created");
        return Entities.builder()
                .type(YELLOWDOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("yellowDoor.png")
                .build();
    }


    //BOOTS------------------------------------------------------------------------------------------------------------

    @Spawns("fireBoots")
    public static Entity newFireBoots(SpawnData data){
        System.out.println("New fireBoots object was created");
        return Entities.builder()
                .type(FIREBOOTS)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("fireBoots.png")
                .build();
    }

    @Spawns("iceBoots")
    public static Entity newIceBoots(SpawnData data){
        System.out.println("New IceBoots object was created");
        return Entities.builder()
                .type(ICEBOOTS)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("iceBoots.png")
                .build();
    }

    @Spawns("waterBoots")
    public static Entity newWaterBoots(SpawnData data){
        System.out.println("New waterBoots object was created");
        return Entities.builder()
                .type(WATERBOOTS)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .viewFromTexture("waterBoots.png")
                .build();
    }


    //ENDZONE----------------------------------------------------------------------------------------------------------

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


