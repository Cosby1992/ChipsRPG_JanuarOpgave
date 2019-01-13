package sample;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sample.Control.PlayerControl;
import sample.Factories.CollectiblesFactory;
import sample.Factories.EnemyControl;
import sample.Factories.EnvironmentalFactory;
import sample.Factories.PlayerEnemyFactory;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static sample.EntityTypes.Type.*;

//Player texture size 16*29 px.
//Animated texture size 128*29 px. (frames = 8)

//Inside class

public class Inside extends GameApplication {

    //private datafields for main class (inside)
    private Entity player;
    private Entity enemyTest;
    private Point2D playerPosition;
    private ArrayList<String> playerInventory;
    private AnimatedTexture texture;
    private ArrayList<String> levels = new ArrayList<String>(){{
        add("elements_test.json");
        add("testMap2000");}};

    public String getLevel(int level){
        if (level<=levels.size()) {
            return levels.get(level);
        }
        else{
            return levels.get(0);
        }
    }

    //Window settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280); //Setting up the window size
        settings.setHeight(720);
        settings.setTitle("Chip's RPG"); //Setting window title
/*
        //Enabling Menu
        settings.setMenuEnabled(true);

        //Setting menu keybind
        settings.setMenuKey(KeyCode.ESCAPE);
*/

    }

    //Importing factories, maps (TiledMap), creating camera, and general game settings
    @Override
    protected void initGame() { //setting up Entities in GameWorld

        getGameWorld().addEntityFactory(new EnvironmentalFactory());
        getGameWorld().addEntityFactory(new CollectiblesFactory()); // adding data from tiledMap data (see class)
        getGameWorld().addEntityFactory(new PlayerEnemyFactory());
        getGameWorld().setLevelFromMap(getLevel(0)); // adding tiled map from json file (using tile data from PC Computer - Chips Challenge 2 - Everything (1).png)


        //finding player inside gameWorld
        ArrayList<Entity> players = getGameWorld().getEntities();
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).isType(PLAYER)) {
                player = players.get(i);
            }
        }

        playerPosition = new Point2D(player.getX(),player.getY());

        ArrayList<Entity> enemyTestList = getGameWorld().getEntities();
        for (int i = 0; i < enemyTestList.size(); i++) {
            if(enemyTestList.get(i).isType(ENEMYTEST)) {
                enemyTest = enemyTestList.get(i);
            }
        }




        //adding camera
        Viewport viewport = getGameScene().getViewport();

        //zooming in
        viewport.setZoom(2);

        //place camera and set to follow player
        viewport.bindToEntity(player,300,160);


        //adding scene background
        Image image = new Image("assets/textures/gameBackground.jpg");

        getGameScene().setBackgroundRepeat(image);

        texture = player.getComponent(PlayerControl.class).getTexture();

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        super.initGameVars(vars);

        vars.put("points", 0);

    }

    @Override
    protected void initUI() {
        super.initUI();

        Text textScore1 = getUIFactory().newText("", Color.WHITE,50);

        textScore1.setTranslateX(10);
        textScore1.setTranslateY(50);



        textScore1.textProperty().bind(getGameState().intProperty("points").asString());

        getGameScene().addUINodes(textScore1);
    }

    //Collision handling (physics)
    @Override
    protected void initPhysics() {

        playerInventory = player.getComponent(PlayerControl.class).getInventory();

        //-------------------------------------------------------------------------------------------------------------
        //PLAYER-CHIP-WALL---------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  player and chip
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, CHIP) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity chip) {

                player.getComponent(PlayerControl.class).addInventory(chip);
                chip.removeFromWorld();
                getGameState().increment("points", 10);

            }
        });

        //Collision handling for  player and wall
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WALL) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {

                player.getComponent(PlayerControl.class).setCanMove(true);

            }

            @Override
            protected void onCollision(Entity player, Entity wall) {

                player.getComponent(PlayerControl.class).setCanMove(false);

                Point2D point = wall.getCenter();
                player.translateTowards(point, -10*tpf());

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {

                //player.getComponent(PlayerControl.class).setCanMove(true);

            }

        });


        //-------------------------------------------------------------------------------------------------------------
        //ICE AND ICE-CORNERS------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  player and ice
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity ice) {

                if(!playerInventory.contains("ICEBOOTS")) {
                    player.getComponent(PlayerControl.class).setOnIce(true);
                } else{
                    player.getComponent(PlayerControl.class).setOnIce(false);
                }

            }

            @Override
            protected void onCollision(Entity player, Entity ice) {

                if(!playerInventory.contains("ICEBOOTS")) {
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

                    System.out.println("TR-collision start " + player.getComponent(PlayerControl.class).getLastMove() );

                    if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("up")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY == playerY
                        ||icecornerY == playerY +1
                        ||icecornerY == playerY-1) {
                            player.getComponent(PlayerControl.class).getEntity().translateX(-player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("left");
                            System.out.println("center hit TR, last move : "+ player.getComponent(PlayerControl.class).getLastMove());
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("right")){

                        int playerX = (int) player.getCenter().getX();
                        int icecornerX = (int) iceCorner.getCenter().getX();

                        if(icecornerX == playerX
                        ||icecornerX == playerX+1
                        ||icecornerX == playerX-1){
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

                        if(icecornerX == playerX
                        ||icecornerX == playerX+1
                        || icecornerX == playerX-1){

                            player.getComponent(PlayerControl.class).getEntity().translateY(player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("down");
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("up")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY == playerY
                        ||icecornerY == playerY+1
                        ||icecornerY == playerY-1){
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

                    if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("down")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY == playerY
                        ||icecornerY == playerY+1
                        ||icecornerY == playerY-1){
                            player.getComponent(PlayerControl.class).getEntity().translateX(-player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("left");
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("right")){

                        int playerX = (int) Math.ceil(player.getCenter().getX());
                        int icecornerX = (int) Math.ceil(iceCorner.getCenter().getX());

                        if(icecornerX == playerX
                        ||icecornerX == playerX+1
                        ||icecornerX == playerX-1){
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

                        if(icecornerX == playerX
                                ||icecornerX == playerX+1
                                || icecornerX == playerX-1){

                            player.getComponent(PlayerControl.class).getEntity().translateY(-player.getComponent(PlayerControl.class).getSpeed()*tpf());
                            player.getComponent(PlayerControl.class).setLastMove("up");
                        }

                    }
                    else if (player.getComponent(PlayerControl.class).getLastMove().equalsIgnoreCase("down")){

                        int playerY = (int) player.getCenter().getY();
                        int icecornerY = (int) iceCorner.getCenter().getY();

                        if(icecornerY == playerY
                                ||icecornerY == playerY+1
                                ||icecornerY == playerY-1){
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
        //ENEMIES------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collistion handling for enemyTest and wall (move from side to side on x-axis)
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

        //Collision Handling for EnemyTest on player
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ENEMYTEST,PLAYER) {
            @Override
            protected void onCollisionBegin(Entity enemyTest, Entity player){
                playerInventory.clear();

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

                player.getComponent(PlayerControl.class).addInventory(iceBoots);
                iceBoots.removeFromWorld();

            }
        });


        //-------------------------------------------------------------------------------------------------------------
        //DOORS--------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  player and redDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity redDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity redDoor) {

                if(playerInventory.contains("REDKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    redDoor.removeFromWorld();
                    playerInventory.remove("REDKEY");

                    for (int i = 0; i < playerInventory.size(); i++) {
                        System.out.println(playerInventory.get(i));
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

        //Collision handling for  player and blueDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BLUEDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity blueDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity blueDoor) {

                if(playerInventory.contains("BLUEKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    blueDoor.removeFromWorld();
                    playerInventory.remove("BLUEKEY");

                    for (int i = 0; i < playerInventory.size(); i++) {
                        System.out.println(playerInventory.get(i));
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

        //Collision handling for  player and greenDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, GREENDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity greenDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity greenDoor) {

                if(playerInventory.contains("GREENKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    greenDoor.removeFromWorld();
                    playerInventory.remove("GREENKEY");

                    for (int i = 0; i < playerInventory.size(); i++) {
                        System.out.println(playerInventory.get(i));
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

        //Collision handling for  player and yellowDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, YELLOWDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity yellowDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity yellowDoor) {

                if(playerInventory.contains("YELLOWKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    yellowDoor.removeFromWorld();
                    playerInventory.remove("YELLOWKEY");

                    for (int i = 0; i < playerInventory.size(); i++) {
                        System.out.println(playerInventory.get(i));
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

        //Collision handling for player and redKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity redKey) {

                player.getComponent(PlayerControl.class).addInventory(redKey);
                redKey.removeFromWorld();

            }
        });

        //Collision handling for  player and blueKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BLUEKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity blueKey) {

                player.getComponent(PlayerControl.class).addInventory(blueKey);
                blueKey.removeFromWorld();

            }
        });

        //Collision handling for player and greenKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, GREENKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity greenKey) {

                player.getComponent(PlayerControl.class).addInventory(greenKey);
                greenKey.removeFromWorld();

            }
        });

        //Collision handling for  player and yellowKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, YELLOWKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity yellowKey) {

                player.getComponent(PlayerControl.class).addInventory(yellowKey);
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


        input.addAction(new UserAction("MoveUp") {

            @Override
            protected void onActionBegin(){
                super.onActionBegin();

                texture.loopAnimationChannel(animForward);
                if(player.getComponent(PlayerControl.class).isCanMove()==false) {
                    player.getComponent(PlayerControl.class).setCanMove(true);
                }

            }


            @Override
            protected void onAction() {
                super.onAction();

                if(player.getComponent(PlayerControl.class).isCanMove()==true) {
                    player.getComponent(PlayerControl.class).moveUp(tpf());
                }

            }

            @Override
            protected void onActionEnd(){
                super.onActionEnd();

                setIdleTexture(input);

            }

        }, KeyCode.W);

        input.addAction(new UserAction("MoveDown") {
            @Override
            protected void onActionBegin() {
                super.onActionBegin();
                texture.loopAnimationChannel(animBackward);
                if(player.getComponent(PlayerControl.class).isCanMove()==false) {
                    player.getComponent(PlayerControl.class).setCanMove(true);
                }
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(player.getComponent(PlayerControl.class).isCanMove() == true) {
                    player.getComponent(PlayerControl.class).moveDown(tpf());
                }
            }

            @Override
            protected void onActionEnd(){
                super.onActionEnd();

                setIdleTexture(input);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("MoveLeft") {
            @Override
            protected void onActionBegin(){
                super.onActionBegin();

               texture.loopAnimationChannel(animLeft);
                if(player.getComponent(PlayerControl.class).isCanMove() == false) {
                    player.getComponent(PlayerControl.class).setCanMove(true);
                }
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(player.getComponent(PlayerControl.class).isCanMove()==true) {
                    player.getComponent(PlayerControl.class).moveLeft(tpf());
                }

            }

            @Override
            protected void onActionEnd(){
                super.onActionEnd();

                setIdleTexture(input);
            }

        }, KeyCode.A);

        input.addAction(new UserAction("MoveRight") {

            @Override
            protected void onActionBegin() {
                super.onActionBegin();

                texture.loopAnimationChannel(animRight);

                if(player.getComponent(PlayerControl.class).isCanMove()==false) {
                    player.getComponent(PlayerControl.class).setCanMove(true);
                }
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(player.getComponent(PlayerControl.class).isCanMove()==true) {
                    player.getComponent(PlayerControl.class).moveRight(tpf());
                }
            }

            @Override
            protected void onActionEnd() {
                super.onActionEnd();

                setIdleTexture(input);

            }
        }, KeyCode.D);

    }

    public void setIdleTexture(Input input){
        AnimationChannel animIdleForward, animIdleBackward, animIdleLeft, animIdleRight;
        animIdleForward = new AnimationChannel("PlayerForwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);
        animIdleBackward = new AnimationChannel("PlayerBackwardAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);
        animIdleLeft = new AnimationChannel("PlayerLeftAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);
        animIdleRight = new AnimationChannel("PlayerRightAnimated.png", 8, 16, 29, Duration.seconds(1), 0, 0);


        if(!input.isHeld(KeyCode.W)
                &&!input.isHeld(KeyCode.A)
                &&!input.isHeld(KeyCode.D)
                &&!input.isHeld(KeyCode.K)){
            String lastMove = player.getComponent(PlayerControl.class).getLastMove();
            switch (lastMove){
                case "right": texture.loopAnimationChannel(animIdleRight); break;
                case "left": texture.loopAnimationChannel(animIdleLeft); break;
                case "up": texture.loopAnimationChannel(animIdleForward); break;
                case "down": texture.loopAnimationChannel(animIdleBackward); break;
            }
        }
    }



    //Main method (not important here)
    public static void main(String[] args) {
        launch(args);
    }

}


