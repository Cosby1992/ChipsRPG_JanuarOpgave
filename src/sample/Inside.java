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
import javafx.util.Duration;
import sample.Control.PlayerControl;
import sample.Factories.CollectiblesFactory;
import sample.Factories.EnemyControl;
import sample.Factories.EnvironmentalFactory;
import sample.Factories.PlayerEnemyFactory;


import java.util.ArrayList;

import static sample.EntityTypes.Type.*;

//Player texture size 16*29 px.
//Animated texture size 128*29 px. (frames = 8)

//Inside class

public class Inside extends GameApplication {

    private Entity player;
    private Entity enemyTest;
    private ArrayList<String> playerInventory;
    private AnimatedTexture texture;
    private Point2D playerPosition;
    private int points = 0;
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
    protected void initUI() {

    }

    //Collision handling (physics)
    @Override
    protected void initPhysics() {

        playerInventory = player.getComponent(PlayerControl.class).getInventory();

        //PLAYER-CHIP-WALL---------------------------------------------------------------------------------------------
        //Collision handling for  player and chip
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, CHIP) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity chip) {

                player.getComponent(PlayerControl.class).addInventory(chip);
                chip.removeFromWorld();
                points +=10;

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

        //Collision handling for  player and chip
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, ICE) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity ice) {

                player.getComponent(PlayerControl.class).setOnIce(true);

            }

            @Override
            protected void onCollision(Entity player, Entity ice) {

                player.getComponent(PlayerControl.class).setOnIce(true);

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity ice) {

                player.getComponent(PlayerControl.class).setOnIce(false);

            }

        });

        //ENEMIES------------------------------------------------------------------------------------------------------
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

        //DOORS--------------------------------------------------------------------------------------------------------
        //Collision handling for  player and redDoor
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDDOOR) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity redDoor) {

                player.getComponent(PlayerControl.class).setCanMove(true);
                points++;

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
                points++;

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
                points++;

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
                points++;

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

        //KEYS---------------------------------------------------------------------------------------------------------
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


