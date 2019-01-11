package sample;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import sample.Control.PlayerControl;


import java.util.ArrayList;

import static sample.EntityTypes.Type.*;


//Inside class

public class Inside extends GameApplication {

    private Entity player;

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

    //Collision handling (physics)
    @Override
    protected void initPhysics() {

        //Collision handling for player and redKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, REDKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity redKey) {

                player.getComponent(PlayerControl.class).addInventory(redKey);
                redKey.removeFromWorld();
                System.out.println("Collision");

            }
        });

        //Collision handling for  player and blueKey
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BLUEKEY) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity blueKey) {

                player.getComponent(PlayerControl.class).addInventory(blueKey);
                blueKey.removeFromWorld();
                System.out.println("Collision");

            }
        });

        //Collision handling for  player and chip
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, CHIP) {

            // order of types is the same as passed into the constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity chip) {

                player.getComponent(PlayerControl.class).addInventory(chip);
                chip.removeFromWorld();
                System.out.println("Collision");

            }
        });

        //Collision handling for  player and wall
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, WALL) {

            // order of types (enum) is the same as passed into the constructor

            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {

                player.getComponent(PlayerControl.class).setCanMove(true);
                System.out.println("Collision");

            }

            @Override
            protected void onCollision(Entity player, Entity wall) {

                player.getComponent(PlayerControl.class).setCanMove(false);

                Point2D point = wall.getCenter();
                player.translateTowards(point, -10*tpf());

            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {


                player.getComponent(PlayerControl.class).setCanMove(true);

            }

        });
    }

    //Importing factories, maps (TiledMap), creating camera, and general game settings
    @Override
    protected void initGame() { //setting up Entities in GameWorld

        getGameWorld().addEntityFactory(new InsideFactory()); // adding data from tiledMap data (see class)
        getGameWorld().setLevelFromMap("testMap2000.json"); // adding tiled map from json file (using tile data from PC Computer - Chips Challenge 2 - Everything (1).png)

        //finding player inside gameWorld
        ArrayList<Entity> players = getGameWorld().getEntities();
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).isType(PLAYER)) {
                player = players.get(i);
            }
        }

        //adding camera
        Viewport viewport = getGameScene().getViewport();

        //zooming in
        viewport.setZoom(2);

        //place camera and set to follow player
        viewport.bindToEntity(player,300,140);

        //adding scene background
        Image image = new Image("assets/textures/gameBackground.jpg");

        getGameScene().setBackgroundRepeat(image);


    }


    //input handling
    @Override
    protected void initInput() {
        super.initInput();

        AnimationChannel animForward, animIdleForward, animBackward, animIdleBackward, animLeft, animIdleLeft, animRight, animIdleRight;
        animIdleForward = new AnimationChannel("forward8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animForward = new AnimationChannel("forward8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);
        animIdleBackward = new AnimationChannel("backwards8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animBackward = new AnimationChannel("backwards8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);
        animIdleLeft = new AnimationChannel("left8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animLeft = new AnimationChannel("left8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);
        animIdleRight = new AnimationChannel("right8test.png", 8, 32, 32, Duration.seconds(1), 0, 0);
        animRight = new AnimationChannel("right8test.png", 8, 32, 32, Duration.seconds(1), 0, 7);



        Input input = getInput();

        input.addAction(new UserAction("MoveUp") {

            @Override
            protected void onActionBegin(){
                super.onActionBegin();

                player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animForward);
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
                super.onActionBegin();

                    player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animIdleForward);


            }

        }, KeyCode.W);

        input.addAction(new UserAction("MoveDown") {
            @Override
            protected void onActionBegin() {
                super.onActionBegin();
                player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animBackward);
                System.out.println("action begun with canMove: " + player.getComponent(PlayerControl.class).isCanMove());
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
                super.onActionBegin();

                if(player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animForward
                        || player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animRight
                        || player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animLeft)
                {
                    player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animIdleBackward);
                }


            }
        }, KeyCode.S);

        input.addAction(new UserAction("MoveLeft") {
            @Override
            protected void onActionBegin(){
                super.onActionBegin();

                player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animLeft);
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

                if(player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animForward
                        || player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animBackward
                        || player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animRight)
                {
                    player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animIdleLeft);
                }


            }

        }, KeyCode.A);

        input.addAction(new UserAction("MoveRight") {

            @Override
            protected void onActionBegin() {
                super.onAction();

                player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animRight);

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
                super.onAction();

                if(player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animForward
                        || player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animBackward
                        || player.getComponent(PlayerControl.class).getTexture().getAnimationChannel() != animLeft)
                {
                    player.getComponent(PlayerControl.class).getTexture().loopAnimationChannel(animIdleRight);
                }

            }
        }, KeyCode.D);

    }



    //Main method (not important here)
    public static void main(String[] args) {
        launch(args);
    }

}


