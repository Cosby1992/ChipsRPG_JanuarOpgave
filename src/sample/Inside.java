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
import com.almasb.fxgl.ui.InGamePanel;
import javafx.geometry.Point2D;
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
    private ArrayList<String> levels = new ArrayList<String>(){{
        add("elements_test.json");
        add("testMap2000.json");}};
    private InGamePanel panel;
    int level = 0;

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
            String lastMove = playerFound.getComponent(PlayerControl.class).getLastMove();
            switch (lastMove){
                case "right": texture.loopAnimationChannel(animIdleRight); break;
                case "left": texture.loopAnimationChannel(animIdleLeft); break;
                case "up": texture.loopAnimationChannel(animIdleForward); break;
                case "down": texture.loopAnimationChannel(animIdleBackward); break;
            }
        }
    }

    private String getLevel(int level){
        if (level<=levels.size()) {
            this.level = level;
            return levels.get(level);
        }
        else{
            this.level = 0;
            return levels.get(0);
        }
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

        startLevel(level);
/*
        getGameWorld().addEntityFactory(new EnvironmentalFactory());
        getGameWorld().addEntityFactory(new PlayerFactory());
        getGameWorld().addEntityFactory(new CollectiblesFactory()); // adding data from tiledMap data (see class)
        getGameWorld().addEntityFactory(new EnemyFactory());

        getGameWorld().setLevelFromMap(getLevel(0)); // adding tiled map from json file (using tile data from PC Computer - Chips Challenge 2 - Everything (1).png)


        //finding playerFound inside gameWorld
        ArrayList<Entity> players = getGameWorld().getEntities();
        for (Entity player1 : players) {
            if (player1.isType(PLAYER)) {
                setPlayerFound(player1);
            }
        }



        playerPosition = new Point2D(playerFound.getX(), playerFound.getY());

        ArrayList<Entity> enemyTestList = getGameWorld().getEntities();
        for (Entity anEnemyTestList : enemyTestList) {
            if (anEnemyTestList.isType(ENEMYTEST)) {
                enemyTest = anEnemyTestList;
            }
        }



        AnimationChannel introChannel = new AnimationChannel("playerBackwardAnimated",8,16,29,Duration.seconds(0.6),0,7);
        IntroScene intro;
        intro.

        new AnimatedTexture(introChannel);


        //adding camera
        Viewport viewport = getGameScene().getViewport();

        //zooming in
        viewport.setZoom(2);

        //place camera and set to follow playerFound
        viewport.bindToEntity(getPlayerFound(),300,160);
*/

        //adding scene background
        Image image = new Image("assets/textures/gameBackground.jpg");

        getGameScene().setBackgroundRepeat(image);




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

    }


    //Collision handling (physics)
    @Override
    protected void initPhysics() {

        setTexture(getPlayerFound().getComponent(PlayerControl.class).getTexture());
        setPlayerInventory(getPlayerFound().getComponent(PlayerControl.class).getInventory());

        //-------------------------------------------------------------------------------------------------------------
        //PLAYER-CHIP-WALL---------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        //Collision handling for  playerFound and chip
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, CHIP) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity chip) {

                player.getComponent(PlayerControl.class).addInventory(chip);
                chip.removeFromWorld();
                getGameState().increment("points", 10);

            }
        });

        //Collision handling for  playerFound and wall
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

                //playerFound.getComponent(PlayerControl.class).setCanMove(true);

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

        //Collision handling for  playerFound and iceCornerTR
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

        //Collision handling for  playerFound and ice
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WATER) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity water) {

                if (!playerInventory.contains("WATERBOOTS")) {
                    //playerFound.getComponent(PlayerControl.class).setInWater(true);
                    startLevel(level);
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

        //Collision Handling for EnemyTest on playerFound
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ENEMYTEST,PLAYER) {
            @Override
            protected void onCollisionBegin(Entity enemyTest, Entity player){
                playerInventory.clear();

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

                player.getComponent(PlayerControl.class).addInventory(iceBoots);
                iceBoots.removeFromWorld();

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
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity redDoor) {

                if(playerInventory.contains("REDKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    redDoor.removeFromWorld();
                    playerInventory.remove("REDKEY");

                    for (String aPlayerInventory : playerInventory) {
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
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity blueDoor) {

                if(playerInventory.contains("BLUEKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    blueDoor.removeFromWorld();
                    playerInventory.remove("BLUEKEY");

                    for (String aPlayerInventory : playerInventory) {
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
                getGameState().increment("points", 1);

            }

            @Override
            protected void onCollision(Entity player, Entity greenDoor) {

                if(playerInventory.contains("GREENKEY")){
                    player.getComponent(PlayerControl.class).setCanMove(true);
                    greenDoor.removeFromWorld();
                    playerInventory.remove("GREENKEY");

                    for (String aPlayerInventory : playerInventory) {
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
                System.out.println(getPlayerInventory());

            }

            @Override
            protected void onCollision(Entity player, Entity yellowDoor) {

                if(getPlayerInventory().contains("YELLOWKEY")){
                    getGameState().increment("points", 1);

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
                redKey.removeFromWorld();

            }
        });

        //Collision handling for  playerFound and blueKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BLUEKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity blueKey) {

                getPlayerInventory().add("BLUEKEY");
                blueKey.removeFromWorld();

            }
        });

        //Collision handling for playerFound and greenKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, GREENKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity greenKey) {

                getPlayerInventory().add("GREENKEY");
                greenKey.removeFromWorld();

            }
        });

        //Collision handling for  playerFound and yellowKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, YELLOWKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity yellowKey) {

                getPlayerInventory().add("YELLOWKEY");
                System.out.println(getPlayerInventory());
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

                setTexture(getPlayerFound().getComponent(PlayerControl.class).getTexture());

                getTexture().loopAnimationChannel(animForward);
                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove()==false) {
                    getPlayerFound().getComponent(PlayerControl.class).setCanMove(true);
                }

            }


            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove()) {
                    getPlayerFound().getComponent(PlayerControl.class).moveUp(tpf());
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
                setTexture(getPlayerFound().getComponent(PlayerControl.class).getTexture());

                getTexture().loopAnimationChannel(animBackward);
                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove()==false) {
                    getPlayerFound().getComponent(PlayerControl.class).setCanMove(true);
                }
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove() == true) {
                    getPlayerFound().getComponent(PlayerControl.class).moveDown(tpf());
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

                setTexture(getPlayerFound().getComponent(PlayerControl.class).getTexture());

               getTexture().loopAnimationChannel(animLeft);
                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove() == false) {
                    getPlayerFound().getComponent(PlayerControl.class).setCanMove(true);
                }
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove()==true) {
                    getPlayerFound().getComponent(PlayerControl.class).moveLeft(tpf());
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

                setTexture(getPlayerFound().getComponent(PlayerControl.class).getTexture());

                texture.loopAnimationChannel(animRight);

                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove()==false) {
                    getPlayerFound().getComponent(PlayerControl.class).setCanMove(true);
                }
            }

            @Override
            protected void onAction() {
                super.onAction();

                if(getPlayerFound().getComponent(PlayerControl.class).isCanMove()==true) {
                    getPlayerFound().getComponent(PlayerControl.class).moveRight(tpf());
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


    //Designing and implementing UI
    @Override
    protected void initUI() {
        super.initUI();
        // UI CONTAINER -----------------------------------------------------------------------------------------------
        Rectangle uiContainer = new Rectangle(1280,50, Color.BLACK);

        uiContainer.setTranslateY(720-53);

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.DARKBLUE);

        uiContainer.setEffect(ds);

        getGameScene().addUINode(uiContainer);


        // TEXT TOP LEFT CORNER (POINTS + CHIPS LEFT) -----------------------------------------------------------------
        Text points = getUIFactory().newText("Points",Color.WHITE, 15);
        Text chipsLeft = getUIFactory().newText("Chips Left",Color.WHITE, 15);
        Text pointsGameVar = getUIFactory().newText("", Color.WHITE,15);
        Text chipsGameVar = getUIFactory().newText("", Color.WHITE,15);

        points.setTranslateX(10);
        points.setTranslateY(20);

        chipsLeft.setTranslateX(10);
        chipsLeft.setTranslateY(40);

        pointsGameVar.setTranslateX(100);
        pointsGameVar.setTranslateY(20);

        chipsGameVar.setTranslateX(100);
        chipsGameVar.setTranslateY(40);

        pointsGameVar.textProperty().bind(getGameState().intProperty("points").asString());
        chipsGameVar.textProperty().bind(getGameState().intProperty("chipsLeft").asString());
        getGameScene().addUINodes(points, chipsLeft, pointsGameVar, chipsGameVar);


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
        getGameWorld().setLevelFromMap(getLevel(level));
        System.out.println("MAYBE HERE?!?!?!");
        //finding playerFound inside gameWorld

        ArrayList<Entity> players = getGameWorld().getEntities();
        for (Entity player1 : players) {
            if (player1.isType(PLAYER)) {
                setPlayerFound(player1);
            }
        }

        setPlayerPosition(new Point2D(getPlayerFound().getX(), getPlayerFound().getY()));

        ArrayList<Entity> enemyTestList = getGameWorld().getEntities();
        for (Entity anEnemyTestList : enemyTestList) {
            if (anEnemyTestList.isType(ENEMYTEST)) {
                enemyTest = anEnemyTestList;
            }
        }

        //adding camera
        Viewport viewport = getGameScene().getViewport();

        //zooming in
        viewport.setZoom(2);

        //place camera and set to follow playerFound
        viewport.bindToEntity(getPlayerFound(),300,160);


        getPlayerFound().getComponent(PlayerControl.class).playerInfo();


    }

    public Entity getPlayerFound() {
        return playerFound;
    }

    public void setPlayerFound(Entity playerFound) {
        this.playerFound = playerFound;
    }

    public AnimatedTexture getTexture() {
        return texture;
    }

    public void setTexture(AnimatedTexture texture) {
        this.texture = texture;
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

}





