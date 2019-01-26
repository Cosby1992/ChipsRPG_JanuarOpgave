package sample;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.InGamePanel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sample.Control.DirtBlockControl;
import sample.Control.PlayerControl;
import sample.Control.TankControl;
import sample.Factories.*;
import sample.Control.EnemySpiderControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sample.EntityTypes.Type.*;

//Player texture size 16*29 px.
//Animated texture size 128*29 px. (frames = 8)

//new Animated texture size 120*26 px (frames = 8) frame size: 15*26



//"Inside" class

public class Inside extends GameApplication {

    //private datafields for main class (inside)
    private Entity playerFound;
    private Entity enemyTest;
    private Point2D playerPosition;
    private ArrayList<String> playerInventory;
    private AnimatedTexture texture;
    private InGamePanel panel;
    private int speed = 100;


    private  ArrayList<String> levels = new ArrayList<>(){{
        add("tankTryOut.json");
        add("ForcedPursuit.json");
        add("elements_test.json");
        add("testMap2000.json");
        add("level3.json");}};

    private int level = 0;

    private String getLevelAsString(int level){
        if (level<= levels.size() && level>=0) {
            this.level = level;
            return levels.get(level);
        }
        else{
            this.level = 0;
            return levels.get(0);
        }
    }

    private int getLevel(){
        return this.level;
    }

    private void setLevel(int level){
        this.level = level;
    }


    //Window settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280); //Setting up the window size
        settings.setHeight(720);
        settings.setTitle("Chip's RPG"); //Setting window title
        settings.setVersion("0.5");

        settings.setFullScreenAllowed(true);

        //Enabling Menu
        settings.setMenuEnabled(true);

        //Setting menu keybind
        settings.setMenuKey(KeyCode.ESCAPE);

        settings.setAppIcon("chip.png");




    }

    //Importing factories, maps (TiledMap), creating camera, and general game settings
    //Invoking startLevel method
    @Override
    protected void initGame() { //setting up Entities in GameWorld

        startLevel(getLevel());
        System.out.println("Level gameinit = " + getLevel());

        getGameState().intProperty("level").setValue(1);
        getGameState().intProperty("deaths").setValue(0);


        //20*70 og 15*70
        //adding scene background
        //Image image = new Image("assets/textures/gameBackground.jpg");

        //getGameScene().setBackgroundRepeat(image);
        getGameScene().setBackgroundRepeat("gameBackground.jpg");

    }

    //Collision handling (physics)
    @Override
    protected void initPhysics() {

        setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());
        setPlayerInventory(getPlayer().getComponent(PlayerControl.class).getInventory());

        //-------------------------------------------------------------------------------------------------------------
        //PLAYER-CHIP-WALL---------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and chip
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, CHIP) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity chip) {

                getPlayerInventory().add(chip.getType().toString());
                chip.removeFromWorld();
                getGameState().increment("points", 25);
                getGameState().increment("chipsLeft", -1);

                getAudioPlayer().playSound("Chip_Pickup.wav");

            }
        });

        //Collision handling for  playerFound and wall
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WALL) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {


                if(player.getComponent(PlayerControl.class).isCanMove()){
                    player.getComponent(PlayerControl.class).setCanMove(false);
                }

                Point2D point = wall.getCenter();
                player.translateTowards(point, -50*tpf());

                if(player.getComponent(PlayerControl.class).isOnIce()){
                   // player.getComponent(PlayerControl.class).setCanMove(true);
                    switch(player.getComponent(PlayerControl.class).getLastMove()){
                        case "up":
                            player.getComponent(PlayerControl.class).moveDown(tpf());
                            player.getComponent(PlayerControl.class).setLastMove("down");
                            break;
                        case "down":
                            player.getComponent(PlayerControl.class).moveUp(tpf());
                            player.getComponent(PlayerControl.class).setLastMove("up");
                            break;
                        case "left":
                            player.getComponent(PlayerControl.class).moveRight(tpf());
                            player.getComponent(PlayerControl.class).setLastMove("right");
                            break;
                        case "right":
                            player.getComponent(PlayerControl.class).moveLeft(tpf());
                            player.getComponent(PlayerControl.class).setLastMove("left");
                            break;
                    }
                   // player.getComponent(PlayerControl.class).setCanMove(false);
                }


            }

            @Override
            protected void onCollision(Entity player, Entity wall) {

                if (player.getComponent(PlayerControl.class).isCanMove()){
                    player.getComponent(PlayerControl.class).setCanMove(false);
                }

                Point2D point = wall.getCenter();
                player.translateTowards(point, -100*tpf());


            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {

                //playerFound.getComponent(PlayerControl.class).setCanMove(true);

            }

        });

        //Collision handling for  playerFound and water
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ENDZONE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity endZone) {

                if (getGameWorld().getEntitiesByType(CHIP).size()==0) {
                    //playerFound.getComponent(PlayerControl.class).setInWater(true);
                    setLevel(getLevel()+1);
                    System.out.println(getLevels().size() + "  " + getLevel());
                    if (getLevels().size()>getLevel()) {
                        getGameState().increment("points", 250);
                        getGameState().increment("level", 1);
                        getDisplay().showMessageBox("You just cleared level " + getLevel());
                        startLevel(getLevel());
                    } else {
                        if(getLevels().size()==getLevel()) {
                            getDisplay().showMessageBox("congratulations.. You won the game!");
                            System.out.println("congratulations.. You won the game! ");
                        }
                    }
                } else {
                    getDisplay().showMessageBox("You have not collected all the chips in this level!!");
                    System.out.println("You have not collected all the chips in this level!!");
                }
            }

        });


        //-------------------------------------------------------------------------------------------------------------
        //SPECIAL BLOCKS-----------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //DIRTBLOCK----------------------------------------------------------------------------------------------------
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, DIRTBLOCK) {
            @Override
            protected void onCollisionBegin(Entity player, Entity dirtBlock) {
                super.onCollisionBegin(player, dirtBlock);


            }

            @Override
            protected void onCollision(Entity player, Entity dirtBlock) {
                super.onCollision(player, dirtBlock);
                switch (player.getComponent(PlayerControl.class).getLastMove()){
                    case "up": dirtBlock.translateY(-speed*tpf()); break;
                    case "down": dirtBlock.translateY(speed*tpf());break;
                    case "left": dirtBlock.translateX(-speed*tpf()); break;
                    case "right": dirtBlock.translateX(speed*tpf()); break;
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity dirtBlock) {
                super.onCollisionEnd(player, dirtBlock);
                player.getComponent(PlayerControl.class).setSpeed(100);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(WALL, DIRTBLOCK) {
            @Override
            protected void onCollisionBegin(Entity wall, Entity dirtBlock) {
                super.onCollisionBegin(wall, dirtBlock);
                dirtBlock.translateTowards(wall.getCenter(),-0*tpf());
            }

            @Override
            protected void onCollision(Entity wall, Entity dirtBlock) {
                super.onCollision(wall, dirtBlock);
                dirtBlock.translateTowards(wall.getCenter(),-101*tpf());
            }

            @Override
            protected void onCollisionEnd(Entity wall, Entity dirtBlock) {
                super.onCollisionEnd(wall, dirtBlock);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(WATER, DIRTBLOCK) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                super.onCollisionBegin(a, b);
            }

            @Override
            protected void onCollision(Entity water, Entity dirtBlock) {
                super.onCollision(water, dirtBlock);

                Point2D waterLocation = new Point2D(water.getX(),water.getY());

                switch (getPlayer().getComponent(PlayerControl.class).getLastMove()){
                    case "up":
                        if(dirtBlock.getCenter().getY()-10<water.getCenter().getY()){
                            dirtBlock.removeFromWorld();
                            water.removeFromWorld();
                        } break;
                    case "down":
                        if(dirtBlock.getCenter().getY()+10>water.getCenter().getY()){
                            dirtBlock.removeFromWorld();
                            water.removeFromWorld();
                        } break;
                    case "left":
                        if(dirtBlock.getCenter().getX()-10<water.getCenter().getX()){
                            dirtBlock.removeFromWorld();
                            water.removeFromWorld();
                        } break;
                    case "right":
                        if(dirtBlock.getCenter().getX()+10>water.getCenter().getX()){
                            dirtBlock.removeFromWorld();
                            water.removeFromWorld();
                        } break;
                }


            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                super.onCollisionEnd(a, b);
            }
        });


        //-------------------------------------------------------------------------------------------------------------
        //TANKS AND TANKACTIVATOR--------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TANKUP, WALL) {
            @Override
            protected void onCollisionBegin(Entity tankUp, Entity wall) {
                super.onCollisionBegin(tankUp, wall);
                tankUp.getComponent(TankControl.class).setActivated(false);
                tankUp.getComponent(TankControl.class).setWallHit(true);
                tankUp.getComponent(TankControl.class).setSpeed(-tankUp.getComponent(TankControl.class).getSpeed());
            }

            @Override
            protected void onCollision(Entity tankUp, Entity wall) {
                super.onCollision(tankUp, wall);
            }

            @Override
            protected void onCollisionEnd(Entity tankUp, Entity wall) {
                super.onCollisionEnd(tankUp, wall);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TANKDOWN, WALL) {
            @Override
            protected void onCollisionBegin(Entity tankDown, Entity wall) {
                super.onCollisionBegin(tankDown, wall);
                tankDown.getComponent(TankControl.class).setActivated(false);
                tankDown.getComponent(TankControl.class).setWallHit(true);
                tankDown.getComponent(TankControl.class).setSpeed(-tankDown.getComponent(TankControl.class).getSpeed());

            }

            @Override
            protected void onCollision(Entity tankDown, Entity wall) {
                super.onCollision(tankDown, wall);
            }

            @Override
            protected void onCollisionEnd(Entity tankDown, Entity wall) {
                super.onCollisionEnd(tankDown, wall);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TANKLEFT, WALL) {
            @Override
            protected void onCollisionBegin(Entity tankLeft, Entity wall) {
                super.onCollisionBegin(tankLeft, wall);
                tankLeft.getComponent(TankControl.class).setActivated(false);
                tankLeft.getComponent(TankControl.class).setWallHit(true);
                tankLeft.getComponent(TankControl.class).setSpeed(-tankLeft.getComponent(TankControl.class).getSpeed());
            }

            @Override
            protected void onCollision(Entity tankLeft, Entity wall) {
                super.onCollision(tankLeft, wall);
            }

            @Override
            protected void onCollisionEnd(Entity tankLeft, Entity wall) {
                super.onCollisionEnd(tankLeft, wall);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TANKRIGHT, WALL) {
            @Override
            protected void onCollisionBegin(Entity tankRight, Entity wall) {
                super.onCollisionBegin(tankRight, wall);
                tankRight.getComponent(TankControl.class).setActivated(false);
                tankRight.getComponent(TankControl.class).setWallHit(true);
                tankRight.getComponent(TankControl.class).setSpeed(-tankRight.getComponent(TankControl.class).getSpeed());
            }

            @Override
            protected void onCollision(Entity tankRight, Entity wall) {
                super.onCollision(tankRight, wall);
            }

            @Override
            protected void onCollisionEnd(Entity tankRight, Entity wall) {
                super.onCollisionEnd(tankRight, wall);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TANKACTIVATOR, PLAYER) {
            @Override
            protected void onCollisionBegin(Entity tankActivator, Entity player) {
                super.onCollisionBegin(tankActivator, player);
                List<Entity> upTanks = getGameWorld().getEntitiesByType(TANKUP);
                List<Entity> downTanks = getGameWorld().getEntitiesByType(TANKDOWN);
                List<Entity> leftTanks = getGameWorld().getEntitiesByType(TANKLEFT);
                List<Entity> rightTanks = getGameWorld().getEntitiesByType(TANKRIGHT);

                for (Entity tank : upTanks) {
                    tank.getComponent(TankControl.class).setActivated(true);
                    tank.getComponent(TankControl.class).setWallHit(false);
                }

                for (Entity tank : downTanks) {
                    tank.getComponent(TankControl.class).setActivated(true);
                    tank.getComponent(TankControl.class).setWallHit(false);
                }

                for (Entity tank : leftTanks) {
                    tank.getComponent(TankControl.class).setActivated(true);
                    tank.getComponent(TankControl.class).setWallHit(false);
                }

                for (Entity tank : rightTanks) {
                    tank.getComponent(TankControl.class).setActivated(true);
                    tank.getComponent(TankControl.class).setWallHit(false);
                }

            }

            @Override
            protected void onCollision(Entity tankActivator, Entity player) {
                super.onCollision(tankActivator, player);

            }

            @Override
            protected void onCollisionEnd(Entity tankActivator, Entity player) {
                super.onCollisionEnd(tankActivator, player);
            }
        });


        //-------------------------------------------------------------------------------------------------------------
        //SPEEDBLOCKS--------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER,SPEEDUP) {
            @Override
            protected void onCollisionBegin(Entity player, Entity speedUp) {
                super.onCollisionBegin(player, speedUp);
                if(!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.getComponent(PlayerControl.class).setSpeed(20);
                    player.translateY(-400 * tpf());
                }
            }

            @Override
            protected void onCollision(Entity player, Entity speedUp) {
                super.onCollision(player, speedUp);
                if(!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.translateY(-400 * tpf());
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity speedUp) {
                super.onCollisionEnd(player, speedUp);
                player.getComponent(PlayerControl.class).setSpeed(100);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER,SPEEDDOWN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity speedDown) {
                super.onCollisionBegin(player, speedDown);
                if (!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.getComponent(PlayerControl.class).setSpeed(20);
                    player.translateY(400 * tpf());
                }
            }

            @Override
            protected void onCollision(Entity player, Entity speedDown) {
                super.onCollision(player, speedDown);
                if (!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.translateY(400 * tpf());
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity speedDown) {
                super.onCollisionEnd(player, speedDown);
                player.getComponent(PlayerControl.class).setSpeed(100);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER,SPEEDLEFT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity speedLeft) {
                super.onCollisionBegin(player, speedLeft);
                if (!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.getComponent(PlayerControl.class).setSpeed(20);
                    player.translateX(-400 * tpf());
                }
            }

            @Override
            protected void onCollision(Entity player, Entity speedLeft) {
                super.onCollision(player, speedLeft);
                if (!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.translateX(-400 * tpf());
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity speedLeft) {
                super.onCollisionEnd(player, speedLeft);
                player.getComponent(PlayerControl.class).setSpeed(100);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER,SPEEDRIGHT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity speedRight) {
                super.onCollisionBegin(player, speedRight);
                if (!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.getComponent(PlayerControl.class).setSpeed(20);
                    player.translateX(400 * tpf());
                }
            }

            @Override
            protected void onCollision(Entity player, Entity speedRight) {
                super.onCollision(player, speedRight);
                if (!getPlayerInventory().contains("SUCKERBOOTS")) {
                    player.translateX(400 * tpf());
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity speedRight) {
                super.onCollisionEnd(player, speedRight);
                player.getComponent(PlayerControl.class).setSpeed(100);
            }
        });


        //-------------------------------------------------------------------------------------------------------------
        //ICE AND ICE-CORNERS------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------
        AnimationChannel onIceAnim;
        onIceAnim = new AnimationChannel("onIce.png", 4,12,26,Duration.seconds(0.3),0,3);

        //Collision handling for  player and ice
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity ice) {

                if(!getPlayerInventory().contains("ICEBOOTS")) {
                    player.getComponent(PlayerControl.class).setOnIce(true);
                    player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(onIceAnim);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollision(Entity player, Entity ice) {

                if(!getPlayerInventory().contains("ICEBOOTS")) {
                    player.getComponent(PlayerControl.class).setOnIce(true);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity ice) {

                player.getComponent(PlayerControl.class).setOnIce(false);

            }

        });

        //Collision handling for  player and iceCornerTR
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICECORNERTR) {

            // order of types is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity iceCorner) {

                if(!getPlayerInventory().contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);
                    player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(onIceAnim);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }
            }

            @Override
            protected void onCollision(Entity player, Entity iceCorner) {

                if(!getPlayerInventory().contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);

                    if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("up")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY >= playerY) {
                            player.getComponent(PlayerControl.class).getEntity().translateX(-player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("left");
                            System.out.println("center hit TR, last move : "+ player.getComponent(PlayerControl.class).getLastMove());
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("right")){

                        int playerX = (int) player.getCenter().getX();
                        int icecornerX = (int) iceCorner.getCenter().getX();

                        if(icecornerX <= playerX){
                            player.getComponent(PlayerControl.class).getEntity().translateY(player.getComponent(PlayerControl.class).getSpeed() * tpf());
                            player.getComponent(PlayerControl.class).setLastMove("down");
                        }
                    }
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity iceCorner) {

                player.getComponent(PlayerControl.class).setOnIce(false);

            }
        });

        //Collision handling for  player and iceCornerTL
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICECORNERTL) {

            // order of types is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity iceCorner) {

                if(!getPlayerInventory().contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }
            }

            @Override
            protected void onCollision(Entity player, Entity iceCorner) {

                if(!getPlayerInventory().contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);

                    if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("left")){

                        int playerX = (int) player.getCenter().getX();
                        int icecornerX = (int) iceCorner.getCenter().getX();

                        if(icecornerX >= playerX){

                            player.getComponent(PlayerControl.class).getEntity().translateY(player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("down");
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("up")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY >= playerY){
                            player.getComponent(PlayerControl.class).getEntity().translateX(player.getComponent(PlayerControl.class).getSpeed() * tpf());
                            player.getComponent(PlayerControl.class).setLastMove("right");
                        }
                    }
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity iceCorner) {

                player.getComponent(PlayerControl.class).setOnIce(false);

            }
        });

        //Collision handling for  player and iceCornerBR
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICECORNERBR) {

            // order of types is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity iceCorner) {

                if(!getPlayerInventory().contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }
            }

            @Override
            protected void onCollision(Entity player, Entity iceCorner) {

                if(!getPlayerInventory().contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);

                    if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("down")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY <= playerY){
                            player.getComponent(PlayerControl.class).getEntity().translateX(-player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("left");
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("right")){

                        int playerX = (int) Math.ceil(player.getCenter().getX());
                        int icecornerX = (int) Math.ceil(iceCorner.getCenter().getX());

                        if(icecornerX <= playerX){
                            player.getComponent(PlayerControl.class).getEntity().translateY(-player.getComponent(PlayerControl.class).getSpeed() * tpf());
                            player.getComponent(PlayerControl.class).setLastMove("up");
                        }
                    }
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity iceCorner) {

                player.getComponent(PlayerControl.class).setOnIce(false);

            }
        });

        //Collision handling for  player and iceCornerTL
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICECORNERBL) {

            // order of types is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity iceCorner) {

                if(!playerInventory.contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }
            }

            @Override
            protected void onCollision(Entity player, Entity iceCorner) {

                if(!playerInventory.contains("ICEBOOTS")){
                    player.getComponent(PlayerControl.class).setOnIce(true);

                    if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("left")){

                        int playerX = (int) player.getCenter().getX();
                        int icecornerX = (int) iceCorner.getCenter().getX();

                        if(icecornerX >= playerX){

                            player.getComponent(PlayerControl.class).getEntity().translateY(-player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("up");
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("down")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY <= playerY){
                            player.getComponent(PlayerControl.class).getEntity().translateX(player.getComponent(PlayerControl.class).getSpeed() * tpf());
                            player.getComponent(PlayerControl.class).setLastMove("right");
                        }
                    }
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity iceCorner) {

                player.getComponent(PlayerControl.class).setOnIce(false);

            }
        });


        //-------------------------------------------------------------------------------------------------------------
        //WATER--FIRE--------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  player and water
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WATER) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity water) {

                if (!getPlayerInventory().contains("WATERBOOTS")) {
                    //playerFound.getComponent(PlayerControl.class).setInWater(true);
                    getGameState().increment("points", -50);
                    getGameState().increment("deaths", 1);
                    getAudioPlayer().playSound("Water_Hit.wav");
                    getDisplay().showMessageBox("Death by drowning!");
                    startLevel(getLevel());
                } else {
                    player.getComponent(PlayerControl.class).setInWater(true);
                    getAudioPlayer().playSound("Water_Hit.wav");
                }
            }

            private int waterSoundIncrement = 0;

            @Override
            protected void onCollision(Entity player, Entity water) {

                if (!player.getComponent(PlayerControl.class).isInWater()){
                    player.getComponent(PlayerControl.class).setInWater(true);
                }

                waterSoundIncrement++;
                if(waterSoundIncrement %600==0){
                    getAudioPlayer().playSound("Water_Hit.wav");
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity water) {

                player.getComponent(PlayerControl.class).setInWater(false);
                waterSoundIncrement =0;
            }

        });

        //Collision handling for  player and water
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, FIRE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity fire) {

                if (!getPlayerInventory().contains("FIREBOOTS")) {
                    //playerFound.getComponent(PlayerControl.class).setInWater(true);
                    getGameState().increment("points", -50);
                    getGameState().increment("deaths", 1);
                    getAudioPlayer().playSound("Fire_2.wav");
                    getDisplay().showMessageBox("Death by fire!");
                    startLevel(getLevel());
                } else {
                    getAudioPlayer().playSound("Fire_2.wav");
                }
            }

            private int fireSoundIncrement = 0;

            @Override
            protected void onCollision(Entity player, Entity fire) {
                fireSoundIncrement++;

                if (fireSoundIncrement%400==0){
                    getAudioPlayer().playSound("Fire_2.wav");
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity water) {


            }

        });




        //-------------------------------------------------------------------------------------------------------------
        //ENEMIES------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for enemyTest and wall (move from side to side on x-axis)
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ENEMYTEST,WALL) {

            @Override
            protected void onCollisionBegin(Entity enemyTest, Entity wall) {
                super.onCollisionBegin(enemyTest, wall);

                enemyTest.getComponent(EnemySpiderControl.class).setColliding(true);
                enemyTest.getComponent(EnemySpiderControl.class).onUpdate(tpf());
            }
            @Override
            protected void onCollision(Entity enemyTest, Entity wall) {
                super.onCollisionBegin(enemyTest, wall);
            }
            @Override
            protected void onCollisionEnd(Entity enemyTest, Entity wall) {
                super.onCollisionBegin(enemyTest, wall);

                enemyTest.getComponent(EnemySpiderControl.class).setColliding(false);
            }

        });

        //Collision Handling for EnemyTest on playerFound
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ENEMYTEST,PLAYER) {
            @Override
            protected void onCollisionBegin(Entity enemyTest, Entity player){

                        getGameState().increment("points", -50);
                        getAudioPlayer().playSound("Death_By_Enemy.wav");
                        getGameState().increment("deaths",1);
                        getDisplay().showMessageBox("You were killed by an Enemy!");
                        startLevel(getLevel());

            }

        });


        //-------------------------------------------------------------------------------------------------------------
        //BOOTS--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  player and iceBoots
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICEBOOTS) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity iceBoots) {

                getPlayerInventory().add(iceBoots.getType().toString());
                iceBoots.removeFromWorld();
                addInventoryToUI();
            }
        });

        //Collision handling for  player and fireBoots
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, FIREBOOTS) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity fireBoots) {

                getPlayerInventory().add(fireBoots.getType().toString());
                fireBoots.removeFromWorld();
                addInventoryToUI();
            }
        });

        //Collision handling for  player and waterBoots
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WATERBOOTS) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity waterBoots) {

                getPlayerInventory().add("WATERBOOTS");
                waterBoots.removeFromWorld();
                addInventoryToUI();

            }
        });

        //Collision handling for  player and suckerBoots
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, SUCKERBOOTS) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity suckerBoots) {

                getPlayerInventory().add("SUCKERBOOTS");
                suckerBoots.removeFromWorld();
                addInventoryToUI();

            }
        });


        //-------------------------------------------------------------------------------------------------------------
        //DOORS--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //-------------------------------------------------------------------------------------------------------------
        //DOORS--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and redDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity redDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

                if(getPlayerInventory().contains("REDKEY")){
                    getAudioPlayer().playSound("Open_Door.wav");
                } else {
                    getAudioPlayer().playSound("Locked_Door.wav");
                }

            }

            @Override
            protected void onCollision(Entity player, Entity redDoor) {

                if(getPlayerInventory().contains("REDKEY")){
                    getGameState().increment("points", 5);
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    redDoor.removeFromWorld();
                    getPlayerInventory().remove("REDKEY");
                    addInventoryToUI();

                    for (String aPlayerInventory : getPlayerInventory()) {
                        System.out.println(aPlayerInventory);
                    }

                } else {
                    player.getComponent(PlayerControl.class).setCanMove(false);

                    Point2D point = redDoor.getCenter();
                    player.translateTowards(point, -10*tpf());
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity redDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

            }

        });

        //Collision handling for  playerFound and blueDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BLUEDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity blueDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

                if(getPlayerInventory().contains("BLUEKEY")){
                    getAudioPlayer().playSound("Open_Door.wav");
                } else {
                    getAudioPlayer().playSound("Locked_Door.wav");
                }

            }

            @Override
            protected void onCollision(Entity player, Entity blueDoor) {

                if(getPlayerInventory().contains("BLUEKEY")){
                    getGameState().increment("points", 5);
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    blueDoor.removeFromWorld();
                    getPlayerInventory().remove("BLUEKEY");
                    addInventoryToUI();

                    for (String aPlayerInventory : getPlayerInventory()) {
                        System.out.println(aPlayerInventory);
                    }

                } else {
                    player.getComponent(PlayerControl.class).setCanMove(false);

                    Point2D point = blueDoor.getCenter();
                    player.translateTowards(point, -10*tpf());
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity blueDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

            }

        });

        //Collision handling for  playerFound and greenDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, GREENDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity greenDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

                if(getPlayerInventory().contains("GREENKEY")){
                    getAudioPlayer().playSound("Open_Door.wav");
                } else {
                    getAudioPlayer().playSound("Locked_Door.wav");
                }

            }

            @Override
            protected void onCollision(Entity player, Entity greenDoor) {

                if(getPlayerInventory().contains("GREENKEY")){
                    getGameState().increment("points", 5);
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    greenDoor.removeFromWorld();
                    getPlayerInventory().remove("GREENKEY");
                    addInventoryToUI();

                    for (String aPlayerInventory : getPlayerInventory()) {
                        System.out.println(aPlayerInventory);
                    }

                } else {
                    player.getComponent(PlayerControl.class).setCanMove(false);

                    Point2D point = greenDoor.getCenter();
                    player.translateTowards(point, -10*tpf());
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity greenDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

            }

        });

        //Collision handling for  playerFound and yellowDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, YELLOWDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity yellowDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

                if(getPlayerInventory().contains("YELLOWKEY")){
                    getAudioPlayer().playSound("Open_Door.wav");
                } else {
                    getAudioPlayer().playSound("Locked_Door.wav");
                }

            }

            @Override
            protected void onCollision(Entity player, Entity yellowDoor) {

                if(getPlayerInventory().contains("YELLOWKEY")){
                    getGameState().increment("points", 5);

                    player.getComponent(PlayerControl.class).setCanMove(true);
                    yellowDoor.removeFromWorld();
                    getPlayerInventory().remove("YELLOWKEY");
                    addInventoryToUI();

                    for (String aPlayerInventory : getPlayerInventory()) {
                        System.out.println(aPlayerInventory);
                    }

                } else {
                    player.getComponent(PlayerControl.class).setCanMove(false);

                    Point2D point = yellowDoor.getCenter();
                    player.translateTowards(point, -10*tpf());
                }

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity yellowDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);

            }

        });


        //-------------------------------------------------------------------------------------------------------------
        //KEYS---------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //-------------------------------------------------------------------------------------------------------------
        //KEYS---------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for playerFound and redKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity redKey) {

                getPlayerInventory().add("REDKEY");
                getAudioPlayer().playSound("Key_Pickup.wav");
                redKey.removeFromWorld();
                addInventoryToUI();

            }
        });

        //Collision handling for  playerFound and blueKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BLUEKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity blueKey) {

                getPlayerInventory().add("BLUEKEY");
                getAudioPlayer().playSound("Key_Pickup.wav");
                blueKey.removeFromWorld();
                addInventoryToUI();
            }
        });

        //Collision handling for playerFound and greenKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, GREENKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity greenKey) {

                getPlayerInventory().add("GREENKEY");
                getAudioPlayer().playSound("Key_Pickup.wav");
                greenKey.removeFromWorld();
                addInventoryToUI();

            }
        });

        //Collision handling for  playerFound and yellowKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, YELLOWKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity yellowKey) {
                //Collision handling for  playerFound and water
                //getPlayerInventory().add("WATERBOOTS");
                getPlayerInventory().add("YELLOWKEY");
                getAudioPlayer().playSound("Key_Pickup.wav");
                yellowKey.removeFromWorld();
                addInventoryToUI();
            }
        });

    }

    //input handling
    @Override
    protected void initInput() {
        super.initInput();

        Input input = getInput();


        input.addAction(new UserAction("Move Up") {

            @Override
            protected void onActionBegin(){
                super.onActionBegin();
/*
                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                if(getPlayer().getComponent(PlayerControl.class).isInWater()){
                    getTexture().loopAnimationChannel(inWaterUp);
                } else {
                    getTexture().loopAnimationChannel(animForward);
                }

                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
*/
            }


            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).moveUp(tpf());
                }

            }

            @Override
            protected void onActionEnd(){
                super.onActionEnd();
            }

        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                super.onActionBegin();

                /*
                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                if(getPlayer().getComponent(PlayerControl.class).isInWater()){
                    getTexture().loopAnimationChannel(inWaterDown);
                } else {
                    getTexture().loopAnimationChannel(animBackward);
                }


                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
                */
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).moveDown(tpf());
                }
            }

            @Override
            protected void onActionEnd(){
                super.onActionEnd();

            }
        }, KeyCode.S);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin(){
                super.onActionBegin();
/*
                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                if(getPlayer().getComponent(PlayerControl.class).isInWater()){
                    getTexture().loopAnimationChannel(inWaterLeft);
                } else {
                    getTexture().loopAnimationChannel(animLeft);
                }

                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
                */
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).moveLeft(tpf());
                }

            }

            @Override
            protected void onActionEnd(){
                super.onActionEnd();
            }

        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {

            @Override
            protected void onActionBegin() {
                super.onActionBegin();
/*
                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                if(getPlayer().getComponent(PlayerControl.class).isInWater()){
                    getTexture().loopAnimationChannel(inWaterRight);
                } else {
                    getTexture().loopAnimationChannel(animRight);
                }

                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
                */
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).moveRight(tpf());
                }
            }

            @Override
            protected void onActionEnd() {
                super.onActionEnd();
            }
        }, KeyCode.D);


        //WHEELMENU----------------------------------------------------------------------------------------------------


        input.addAction(new UserAction("Open/Close Panel") {
            @Override
            protected void onActionBegin() {
                if (panel.isOpen())
                    panel.close();
                else
                    panel.open();
            }
        }, KeyCode.TAB);

    }

    //Game variables like points and chips
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        super.initGameVars(vars);

        List<Entity> chips = getGameWorld().getEntitiesByType(CHIP);
        int numberOfChips = chips.size();

        //point game variable
        vars.put("points",0);

        //chip's left
        vars.put("chipsLeft",numberOfChips);

        vars.put("level", 0);

        vars.put("deaths", 0);

    }

    //Designing and implementing UI
    @Override
    protected void initUI() {
        super.initUI();

        updateUI();
    }

    private void updateUI(){
        Texture uiContainer = getAssetLoader().loadTexture("UIContainer.png");

        uiContainer.setTranslateY(620);

        getGameScene().addUINode(uiContainer);


        // TEXT BUTTOM LEFT CORNER (POINTS + CHIPS LEFT) --------------------------------------------------------------

        //text points
        Text points = getUIFactory().newText("Points",Color.WHITE, 15);
        points.setTranslateX(15);
        points.setTranslateY(648);

        //text attached to "points" game variable
        Text pointsGameVar = getUIFactory().newText("", Color.WHITE,15);
        pointsGameVar.setTranslateX(130);
        pointsGameVar.setTranslateY(648);

        //text "chips left"
        Text chipsLeft = getUIFactory().newText("Chips Left",Color.WHITE, 15);
        chipsLeft.setTranslateX(15);
        chipsLeft.setTranslateY(702);

        //text attached to game variable "chips"
        Text chipsGameVar = getUIFactory().newText("", Color.WHITE,15);
        chipsGameVar.setTranslateX(130);
        chipsGameVar.setTranslateY(702);


        //text "level"
        Text level = getUIFactory().newText("Level ", Color.WHITE, 15);
        level.setTranslateX(1120);
        level.setTranslateY(702);

        //text attached to game variable "level"
        Text levelGameVar = getUIFactory().newText("", Color.WHITE,15);
        levelGameVar.setTranslateX(1235);
        levelGameVar.setTranslateY(702);


        //text "Deaths"
        Text deaths = getUIFactory().newText("Deaths ", Color.WHITE, 15);
        deaths.setTranslateX(1120);
        deaths.setTranslateY(648);

        //text attached to game variable "deaths" (death counter)
        Text deathsGameVar = getUIFactory().newText("", Color.WHITE, 15);
        deathsGameVar.setTranslateX(1235);
        deathsGameVar.setTranslateY(648);

        //Binding texts to game variables
        levelGameVar.textProperty().bind(getGameState().intProperty("level").asString());
        pointsGameVar.textProperty().bind(getGameState().intProperty("points").asString());
        chipsGameVar.textProperty().bind(getGameState().intProperty("chipsLeft").asString());
        deathsGameVar.textProperty().bind(getGameState().intProperty("deaths").asString());


        //adding nodes to UI
        getGameScene().addUINodes(points, chipsLeft, level, deaths, pointsGameVar, chipsGameVar, levelGameVar, deathsGameVar);


        //Adding tab menu
        panel = new InGamePanel();

        Text text = getUIFactory().newText("Achievements");
        text.setTranslateX(50);
        text.setTranslateY(50);
        panel.getChildren().add(text);

        getGameScene().addUINode(panel);
    }

    //counter used in addInventoryToUI method
    private int inventoryIULastPosition = 1;

    //Method inserting images and numbers into UI as Nodes
    private void addInventoryToUI(){

        //initializing Textures with pictures (assets)
        Texture yellowKeyPNG = getAssetLoader().loadTexture("yellowKey.png", 32, 32);
        Texture greenKeyPNG = getAssetLoader().loadTexture("greenKey.png", 32, 32);
        Texture redKeyPNG = getAssetLoader().loadTexture("redKey.png", 32, 32);
        Texture blueKeyPNG = getAssetLoader().loadTexture("blueKey.png", 32, 32);

        Texture iceBootsPNG = getAssetLoader().loadTexture("iceBoots.png", 32, 32);
        Texture fireBootsPNG = getAssetLoader().loadTexture("fireBoots.png", 32, 32);
        Texture waterBootsPNG = getAssetLoader().loadTexture("waterBoots.png", 32, 32);
        Texture suckerBootsPNG = getAssetLoader().loadTexture("suckerBoots.png",32,32);

        //Clearing and updating UI using updateUI() method
        getGameScene().clearUINodes();
        updateUI();

        //resetting counter for position of textures on scene
        setInventoryUILastPosition(1);

        //initializing new HashMap
        Map<String, Integer> counts = new HashMap<String, Integer>();

        //counting number of duplicate strings in ArrayList playerInventory
        for (String str : getPlayerInventory()) {
            if (counts.containsKey(str)) {
                counts.put(str, counts.get(str) + 1);
            } else {
                counts.put(str, 1);
            }
        }

        //initializing temp String used below
        String counterText = "";

        //using Strings from Map in a switch
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
            switch (entry.getKey()){
                case "YELLOWKEY":
                    yellowKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
                    yellowKeyPNG.setTranslateY(652);
                    getGameScene().addUINode(yellowKeyPNG); //adding picture

                    //adds number to UI if there is more than one yellowKey in inventory
                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text yellowKeyAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        yellowKeyAmount.setTranslateX(213 + 43 * getInventoryIULastPosition());
                        yellowKeyAmount.setTranslateY(658);
                        getGameScene().addUINode(yellowKeyAmount);
                        System.out.println("inside if statement yellowKey");
                    }
                    setInventoryUILastPosition(getInventoryIULastPosition()+1);
                    break;


                case "GREENKEY":
                    greenKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
                    greenKeyPNG.setTranslateY(652);
                    getGameScene().addUINode(greenKeyPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text greenKeyAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        greenKeyAmount.setTranslateX(213 + 43 * getInventoryIULastPosition());
                        greenKeyAmount.setTranslateY(658);
                        getGameScene().addUINode(greenKeyAmount);
                        System.out.println("inside if statement greenKey");
                    }
                    setInventoryUILastPosition(getInventoryIULastPosition()+1);
                    break;

                case "BLUEKEY":
                    blueKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
                    blueKeyPNG.setTranslateY(652);
                    getGameScene().addUINode(blueKeyPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text blueKeyAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        blueKeyAmount.setTranslateX(213 + 43 * getInventoryIULastPosition());
                        blueKeyAmount.setTranslateY(658);
                        getGameScene().addUINode(blueKeyAmount);
                        System.out.println("inside if statement blueKey");
                    }
                    setInventoryUILastPosition(getInventoryIULastPosition()+1);
                    break;

                case "REDKEY":
                    redKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
                    redKeyPNG.setTranslateY(652);
                    getGameScene().addUINode(redKeyPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text redKeyAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        redKeyAmount.setTranslateX(213 + 43 * getInventoryIULastPosition());
                        redKeyAmount.setTranslateY(658);
                        getGameScene().addUINode(redKeyAmount);
                        System.out.println("inside if statement redKey");
                    }
                    setInventoryUILastPosition(getInventoryIULastPosition()+1);
                    break;

                case "WATERBOOTS":
                    waterBootsPNG.setTranslateX(215 + 43 * 5);
                    waterBootsPNG.setTranslateY(652);
                    getGameScene().addUINode(waterBootsPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text waterBootsAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        waterBootsAmount.setTranslateX(213 + 43 * 5);
                        waterBootsAmount.setTranslateY(658);
                        getGameScene().addUINode(waterBootsAmount);
                        System.out.println("inside if statement waterBoots");
                    }
                    break;

                case "ICEBOOTS":
                    iceBootsPNG.setTranslateX(215 + 43 * 6);
                    iceBootsPNG.setTranslateY(652);
                    getGameScene().addUINode(iceBootsPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text iceBootsAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        iceBootsAmount.setTranslateX(213 + 43 * 6);
                        iceBootsAmount.setTranslateY(658);
                        getGameScene().addUINode(iceBootsAmount);
                        System.out.println("inside if statement iceBoots");
                    }
                    break;

                case "FIREBOOTS":
                    fireBootsPNG.setTranslateX(215 + 43 * 7);
                    fireBootsPNG.setTranslateY(652);
                    getGameScene().addUINode(fireBootsPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text fireBootsAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        fireBootsAmount.setTranslateX(213 + 43 * 7);
                        fireBootsAmount.setTranslateY(658);
                        getGameScene().addUINode(fireBootsAmount);
                        System.out.println("inside if statement fireBoots");
                    }
                    break;

                case "SUCKERBOOTS":
                    suckerBootsPNG.setTranslateX(215 + 43 * 8);
                    suckerBootsPNG.setTranslateY(652);
                    getGameScene().addUINode(suckerBootsPNG);

                    if(entry.getValue()>1){
                        counterText = entry.getValue().toString();
                        Text suckerBootsAmount = getUIFactory().newText(counterText, Color.BLACK, 13);
                        suckerBootsAmount.setTranslateX(213 + 43 * 7);
                        suckerBootsAmount.setTranslateY(658);
                        getGameScene().addUINode(suckerBootsAmount);
                        System.out.println("inside if statement suckerBoots");
                    }
                    break;

            }
        }


        //first try on inventoryUI, almost worked, but threw illegal argument exception because of duplicate nodes
        //being added to scene

        /*
try {
    for (String s : getPlayerInventory()) {
        if (s.equalsIgnoreCase("yellowKey")) {
            yellowKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            yellowKeyPNG.setTranslateY(652);
            getGameScene().addUINode(yellowKeyPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        } else if (s.equalsIgnoreCase("greenKey")) {
            greenKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            greenKeyPNG.setTranslateY(652);
            getGameScene().addUINode(greenKeyPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        } else if (s.equalsIgnoreCase("redKey")) {
            redKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            redKeyPNG.setTranslateY(652);
            getGameScene().addUINode(redKeyPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        } else if (s.equalsIgnoreCase("blueKey")) {
            blueKeyPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            blueKeyPNG.setTranslateY(652);
            getGameScene().addUINode(blueKeyPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        } else if (s.equalsIgnoreCase("iceBoots")) {
            iceBootsPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            iceBootsPNG.setTranslateY(652);
            getGameScene().addUINode(iceBootsPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        } else if (s.equalsIgnoreCase("fireBoots")) {
            fireBootsPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            fireBootsPNG.setTranslateY(652);
            getGameScene().addUINode(fireBootsPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        } else if (s.equalsIgnoreCase("waterBoots")) {
            waterBootsPNG.setTranslateX(215 + 43 * getInventoryIULastPosition());
            waterBootsPNG.setTranslateY(652);
            getGameScene().addUINode(waterBootsPNG);
            setInventoryUILastPosition(getInventoryIULastPosition() + 1);
        }
    }

    } catch (IllegalArgumentException e){
        System.out.println("ups, noget gik galt");

    }
    */
    }


    //Main method (not important here)
    public static void main(String[] args) {
        launch(args);
    }

    //Method starts a new level, is used whenever world is reloaded
    //(also when game is first launched)(also player dying!)
    private void startLevel(int level) {
        //clearing world
        System.out.println("Clear World");
        getGameWorld().clear();

        System.out.println("Adding EntityFactories");
        //adding entityfactories (Entities from map)
        getGameWorld().addEntityFactory(new PlayerFactory());
        getGameWorld().addEntityFactory(new EnvironmentalFactory());
        getGameWorld().addEntityFactory(new CollectiblesFactory()); // adding data from tiledMap data (see class)
        getGameWorld().addEntityFactory(new EnemyFactory());
        getGameWorld().addEntityFactory(new UtilityFactory());

        //setting level from json file from tiledMap
        System.out.println("Setting Level from map");
        getGameWorld().setLevelFromMap(getLevelAsString(level));

        //resetting chipsLeft value
        getGameState().setValue("chipsLeft", 0);

        //finding playerFound inside gameWorld
        System.out.println("Finding player on map");
        ArrayList<Entity> entities = getGameWorld().getEntities();
        for (Entity entity : entities) {
            if (entity.isType(PLAYER)) {
                setPlayer(entity);
            } else if(entity.isType(CHIP)){
                getGameState().increment("chipsLeft", 1);
            }
        }

        //setting Player position (used elsewhere)
        setPlayerPosition(new Point2D(getPlayer().getX(), getPlayer().getY()));

/*
        ArrayList<Entity> enemyTestList = getGameWorld().getEntities();
        for (Entity anEnemyTestList : enemyTestList) {
            if (anEnemyTestList.isType(ENEMYTEST)) {
                enemyTest = anEnemyTestList;
            }
        }
*/
        //adding camera
        Viewport viewport = getGameScene().getViewport();

        //Zooming in
        viewport.setZoom(2.2);

        //place camera and set to follow playerFound
        viewport.bindToEntity(getPlayer(),300,160);

        //prints playerinfo to console
        getPlayer().getComponent(PlayerControl.class).playerInfo();

        //Emptying player inventory
        setPlayerInventory(new ArrayList<>());

        getGameScene().clearUINodes();
        updateUI();


        //not runnable
        //getDisplay().showMessageBox("Welcome to level " + getLevel());


    }

    public Entity getPlayer() {
        return playerFound;
    }

    public void setPlayer(Entity playerFound) {
        this.playerFound = playerFound;
    }

    private AnimatedTexture getTexture() {
        return texture;
    }

    private void setTexture(AnimatedTexture texture) {
        this.texture = texture;
    }



    private ArrayList<String> getPlayerInventory() {
        return playerInventory;
    }

    private void setPlayerInventory(ArrayList<String> playerInventory) {
        this.playerInventory = playerInventory;
    }

    public Point2D getPlayerPosition() {
        return playerPosition;
    }

    private void setPlayerPosition(Point2D playerPosition) {
        this.playerPosition = playerPosition;
    }

    private ArrayList<String> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<String> levels) {
        this.levels = levels;
    }

    private int getInventoryIULastPosition() {
        return inventoryIULastPosition;
    }

    private void setInventoryUILastPosition(int inventoryIULastPosition) {
        this.inventoryIULastPosition = inventoryIULastPosition;
    }


}





