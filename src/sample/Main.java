package sample;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;

import java.awt.*;

//Main class

public class Main extends GameApplication {

    //Game world settings
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

        getGameWorld().setLevelFromMap("level_1.assets.json");
        */
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new ChipFactory());
        getGameWorld().setLevelFromMap("map_test.json");



    }

    public static void main(String[] args) {
        launch(args);
    }
}


