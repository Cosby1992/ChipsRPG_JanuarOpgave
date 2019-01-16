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
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sample.Control.PlayerControl;
import sample.Factories.CollectiblesFactory;
import sample.Control.EnemyControl;
import sample.Factories.EnvironmentalFactory;
import sample.Factories.EnemyFactory;
import sample.Factories.PlayerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.spawn;
import static sample.EntityTypes.Type.*;

//Player texture size 16*29 px.
//Animated texture size 128*29 px. (frames = 8)

//Inside class

public class Inside extends GameApplication {

    //private datafields for main class (inside)
    private Entity playerFound;
    private Entity enemyTest;
    private Point2D playerPosition;
    private ArrayList<String> playerInventory;
    private AnimatedTexture texture;
    private InGamePanel panel;


    private  ArrayList<String> levels = new ArrayList<>(){{
        add("elements_test.json");
        add("testMap2000.json");}};

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
       // settings.setMenuEnabled(true);

        //Setting menu keybind
       // settings.setMenuKey(KeyCode.ESCAPE);

        settings.setAppIcon("chip.png");




    }

    //Importing factories, maps (TiledMap), creating camera, and general game settings
    @Override
    protected void initGame() { //setting up Entities in GameWorld

        startLevel(getLevel());
        System.out.println("Level gameinit = " + getLevel());

        getGameState().intProperty("level").setValue(1);
        getGameState().intProperty("deaths").setValue(0);


        //adding scene background
        Image image = new Image("assets/textures/gameBackground.jpg");

        getGameScene().setBackgroundRepeat(image);






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


                player.getComponent(PlayerControl.class).setCanMove(true);


                Point2D point = wall.getCenter();
                player.translateTowards(point, -50*tpf());


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
                    getGameState().increment("points", 250);
                    setLevel(getLevel()+1);
                    System.out.println(getLevels().size() + "  " + getLevel());
                    if (getLevels().size()>getLevel()) {
                        startLevel(getLevel());
                    } else {
                        System.out.println("congratulations.. You won the game! ");
                    }
                } else {
                    System.out.println("You have not collected all the chips in this level!!");
                }
            }

            @Override
            protected void onCollision(Entity player, Entity water) {



            }

            @Override
            protected void onCollisionEnd(Entity player, Entity water) {


            }

        });


        //-------------------------------------------------------------------------------------------------------------
        //ICE AND ICE-CORNERS------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and ice
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity ice) {

                if(!getPlayerInventory().contains("ICEBOOTS")) {
                    player.getComponent(PlayerControl.class).setOnIce(true);
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

        //Collision handling for  playerFound and iceCornerTR
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICECORNERTR) {

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

        //Collision handling for  playerFound and iceCornerTL
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

        //Collision handling for  playerFound and iceCornerBR
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

        //Collision handling for  playerFound and iceCornerTL
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
        //WATER--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and water
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WATER) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity water) {

                if (!getPlayerInventory().contains("WATERBOOTS")) {
                    //playerFound.getComponent(PlayerControl.class).setInWater(true);
                    getGameState().increment("points", -50);
                    getGameState().increment("deaths", 1);
                    getAudioPlayer().playSound("Water_Hit.wav");
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

        //-------------------------------------------------------------------------------------------------------------
        //WATER--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and water
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, FIRE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity fire) {

                if (!getPlayerInventory().contains("FIREBOOTS")) {
                    //playerFound.getComponent(PlayerControl.class).setInWater(true);
                    getGameState().increment("points", -50);
                    getGameState().increment("deaths", 1);
                    getAudioPlayer().playSound("Fire_2.wav");
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

                enemyTest.getComponent(EnemyControl.class).setColliding(true);
                enemyTest.getComponent(EnemyControl.class).onUpdate(tpf());
            }
            @Override
            protected void onCollision(Entity enemyTest, Entity wall) {
                super.onCollisionBegin(enemyTest, wall);
            }
            @Override
            protected void onCollisionEnd(Entity enemyTest, Entity wall) {
                super.onCollisionBegin(enemyTest, wall);

                enemyTest.getComponent(EnemyControl.class).setColliding(false);
            }

        });

        //Collision Handling for EnemyTest on playerFound
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ENEMYTEST,PLAYER) {
            @Override
            protected void onCollisionBegin(Entity enemyTest, Entity player){

                        getGameState().increment("points", -50);
                        getAudioPlayer().playSound("Death_By_Enemy.wav");
                        startLevel(getLevel());

            }

        });


        //-------------------------------------------------------------------------------------------------------------
        //BOOTS--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and iceBoots
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICEBOOTS) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity iceBoots) {

                getPlayerInventory().add(iceBoots.getType().toString());
                iceBoots.removeFromWorld();

            }
        });

        //-------------------------------------------------------------------------------------------------------------
        //BOOTS--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and iceBoots
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, FIREBOOTS) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity fireBoots) {

                getPlayerInventory().add(fireBoots.getType().toString());
                fireBoots.removeFromWorld();

            }
        });


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

        //Collision handling for playerFound and redKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity redKey) {

                getPlayerInventory().add("REDKEY");

                getAudioPlayer().playSound("Key_Pickup.wav");

                redKey.removeFromWorld();

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

            }
        });

        //Collision handling for  playerFound and yellowKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, YELLOWKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity yellowKey) {
                //Collision handling for  playerFound and water
                getPlayerInventory().add("WATERBOOTS");
                getPlayerInventory().add("YELLOWKEY");
                getAudioPlayer().playSound("Key_Pickup.wav");
                yellowKey.removeFromWorld();

            }
        });

    }

    //input handling
    @Override
    protected void initInput() {
        super.initInput();



        // initializing AnimationChannels to use with walking
        AnimationChannel animForward, animBackward, animLeft, animRight;
        animForward = new AnimationChannel("PlayerForwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 7);
        animBackward = new AnimationChannel("PlayerBackwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 7);
        animLeft = new AnimationChannel("PlayerLeftAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 7);
        animRight = new AnimationChannel("PlayerRightAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 7);


        Input input = getInput();


        input.addAction(new UserAction("Move Up") {

            @Override
            protected void onActionBegin(){
                super.onActionBegin();

                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                getTexture().loopAnimationChannel(animForward);
                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }

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

                setIdleTexture(input);

            }

        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                super.onActionBegin();
                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                getTexture().loopAnimationChannel(animBackward);
                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
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

                setIdleTexture(input);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin(){
                super.onActionBegin();

                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

               getTexture().loopAnimationChannel(animLeft);
                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
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

                setIdleTexture(input);
            }

        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {

            @Override
            protected void onActionBegin() {
                super.onActionBegin();

                setTexture(getPlayer().getComponent(PlayerControl.class).getTexture());

                texture.loopAnimationChannel(animRight);

                if(!getPlayer().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayer().getComponent(PlayerControl.class).setCanMove(true);
                }
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

                setIdleTexture(input);

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
        // UI CONTAINER -----------------------------------------------------------------------------------------------
        Rectangle uiContainer = new Rectangle(1274,50, Color.BLACK);
        uiContainer.setTranslateX(3);

        uiContainer.setTranslateY(720-53);

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.DARKBLUE);

        uiContainer.setEffect(ds);

        getGameScene().addUINode(uiContainer);


        // TEXT BUTTOM LEFT CORNER (POINTS + CHIPS LEFT) -----------------------------------------------------------------
        Text points = getUIFactory().newText("Points",Color.WHITE, 15);
        Text chipsLeft = getUIFactory().newText("Chips Left",Color.WHITE, 15);
        Text pointsGameVar = getUIFactory().newText("", Color.WHITE,15);
        Text chipsGameVar = getUIFactory().newText("", Color.WHITE,15);

        Text levelGameVar = getUIFactory().newText("", Color.WHITE,15);
        levelGameVar.setTranslateX(1235);
        levelGameVar.setTranslateY(705);

        Text level = getUIFactory().newText("Level ", Color.WHITE, 15);
        level.setTranslateX(1170);
        level.setTranslateY(705);

        Text deaths = getUIFactory().newText("Deaths ", Color.WHITE, 15);
        deaths.setTranslateX(1160);
        deaths.setTranslateY(685);

        Text deathsGameVar = getUIFactory().newText("", Color.WHITE, 15);
        deathsGameVar.setTranslateX(1235);
        deathsGameVar.setTranslateY(685);


        points.setTranslateX(43);
        points.setTranslateY(685);

        chipsLeft.setTranslateX(15);
        chipsLeft.setTranslateY(705);

        pointsGameVar.setTranslateX(145);
        pointsGameVar.setTranslateY(685);

        chipsGameVar.setTranslateX(145);
        chipsGameVar.setTranslateY(705);

        levelGameVar.textProperty().bind(getGameState().intProperty("level").asString());
        pointsGameVar.textProperty().bind(getGameState().intProperty("points").asString());
        chipsGameVar.textProperty().bind(getGameState().intProperty("chipsLeft").asString());
        deathsGameVar.textProperty().bind(getGameState().intProperty("deaths").asString());

        /*
        Texture redkey = new Texture(new Image("assets/textures/redKey.png"));
        redkey.setTranslateX(500);
        redkey.setTranslateY(500);

        Texture yellowkey = new Texture(new Image("assets/textures/yellowKey.png"));
        yellowkey.setTranslateX(500);
        yellowkey.setTranslateY(600);
*/


        getGameScene().addUINodes(points, chipsLeft, level, deaths, pointsGameVar, chipsGameVar, levelGameVar, deathsGameVar);


//Adding tab menu
        panel = new InGamePanel();

        Text text = getUIFactory().newText("Achievements");
        text.setTranslateX(50);
        text.setTranslateY(50);
        panel.getChildren().add(text);

        getGameScene().addUINode(panel);


    }


    //Main method (not important here)
    public static void main(String[] args) {
        launch(args);
    }

    private void startLevel(int level) {
        System.out.println("1. step");
        getGameWorld().clear();

        System.out.println("here ??");

        getGameWorld().addEntityFactory(new PlayerFactory());
        getGameWorld().addEntityFactory(new EnvironmentalFactory());
        getGameWorld().addEntityFactory(new CollectiblesFactory()); // adding data from tiledMap data (see class)
        getGameWorld().addEntityFactory(new EnemyFactory());
        System.out.println("here ?!?!?!?!?!");
        getGameWorld().setLevelFromMap(getLevelAsString(level));
        System.out.println("MAYBE HERE?!?!?!");
        //finding playerFound inside gameWorld

        ArrayList<Entity> entities = getGameWorld().getEntities();
        for (Entity entity : entities) {
            if (entity.isType(PLAYER)) {
                setPlayer(entity);
            } else if(entity.isType(CHIP)){
                getGameState().increment("chipsLeft", 1);
            }
        }

        getGameState().increment("level", 1);

        setPlayerPosition(new Point2D(getPlayer().getX(), getPlayer().getY()));

        ArrayList<Entity> enemyTestList = getGameWorld().getEntities();
        for (Entity anEnemyTestList : enemyTestList) {
            if (anEnemyTestList.isType(ENEMYTEST)) {
                enemyTest = anEnemyTestList;
            }
        }

        //adding camera
        Viewport viewport = getGameScene().getViewport();

        //zooming in
        viewport.setZoom(2.2);

        //place camera and set to follow playerFound
        viewport.bindToEntity(getPlayer(),300,160);


        getPlayer().getComponent(PlayerControl.class).playerInfo();


        setPlayerInventory(new ArrayList<>());


    }

    public Entity getPlayer() {
        return playerFound;
    }

    public void setPlayer(Entity playerFound) {
        this.playerFound = playerFound;
    }

    public AnimatedTexture getTexture() {
        return texture;
    }

    public void setTexture(AnimatedTexture texture) {
        this.texture = texture;
    }

    private void setIdleTexture(Input input){
        AnimationChannel animIdleForward, animIdleBackward, animIdleLeft, animIdleRight;
        animIdleForward = new AnimationChannel("PlayerForwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);
        animIdleBackward = new AnimationChannel("PlayerBackwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);
        animIdleLeft = new AnimationChannel("PlayerLeftAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);
        animIdleRight = new AnimationChannel("PlayerRightAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);


        if(!input.isHeld(KeyCode.W)
                &&!input.isHeld(KeyCode.A)
                &&!input.isHeld(KeyCode.D)
                &&!input.isHeld(KeyCode.K)){
            String lastMove = getPlayer().getComponent(PlayerControl.class).getLastMove();
            switch (lastMove){
                case "right": getTexture().loopAnimationChannel(animIdleRight); break;
                case "left": getTexture().loopAnimationChannel(animIdleLeft); break;
                case "up": getTexture().loopAnimationChannel(animIdleForward); break;
                case "down": getTexture().loopAnimationChannel(animIdleBackward); break;
            }
        }
    }

    public ArrayList<String> getPlayerInventory() {
        return playerInventory;
    }

    public void setPlayerInventory(ArrayList<String> playerInventory) {
        this.playerInventory = playerInventory;
    }

    public Point2D getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Point2D playerPosition) {
        this.playerPosition = playerPosition;
    }

    public ArrayList<String> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<String> levels) {
        this.levels = levels;
    }
}





