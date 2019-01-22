package sample.Factories;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.texture;
import static sample.EntityTypes.Type.*;


public class EnvironmentalFactory implements EntityFactory {

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


    @Spawns("ice")
    public static Entity newIce(SpawnData data){
        System.out.println("New ice object was created");
        return Entities.builder()
                .type(ICE)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("iceCornerTR")
    public static Entity newIceCornerTR(SpawnData data){
        System.out.println("New iceCorner object was created");
        return Entities.builder()
                .type(ICECORNERTR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("iceCornerTL")
    public static Entity newIceCornerTL(SpawnData data){
        System.out.println("New iceCorner object was created");
        return Entities.builder()
                .type(ICECORNERTL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("iceCornerBR")
    public static Entity newIceCornerBR(SpawnData data){
        System.out.println("New iceCorner object was created");
        return Entities.builder()
                .type(ICECORNERBR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("iceCornerBL")
    public static Entity newIceCornerBL(SpawnData data){
        System.out.println("New iceCorner object was created");
        return Entities.builder()
                .type(ICECORNERBL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("fire")
    public static Entity newFire(SpawnData data){
        System.out.println("New fire object was created");
        return Entities.builder()
                .type(FIRE)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .viewFromAnimatedTexture(texture("fire.png").toAnimatedTexture(4, Duration.seconds(0.5)))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("water")
    public static Entity newWater(SpawnData data){
        System.out.println("New water object was created");
        return Entities.builder()
                .type(WATER)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .viewFromAnimatedTexture(texture("water.png").toAnimatedTexture(4, Duration.seconds(0.5)))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("")
    public static Entity newnewnew(SpawnData data){
        return Entities.builder()
                .from(data)
                .build();
    }




}
