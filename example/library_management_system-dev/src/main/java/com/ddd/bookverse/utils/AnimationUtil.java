package com.ddd.bookverse.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for creating various animations.
 */
public class AnimationUtil {

    /**
     * Sets a rotate transition animation on the specified node.
     *
     * @param node The node to apply the animation to.
     * @param fromAngle The starting angle of the rotation.
     * @param toAngle The ending angle of the rotation.
     * @return The RotateTransition object.
     */
    public static RotateTransition setAnimationRotateTransition(Node node, double fromAngle, double toAngle) {
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(node);
        rotateTransition.setDuration(Duration.seconds(0.5));
        rotateTransition.setFromAngle(fromAngle);
        rotateTransition.setToAngle(toAngle);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAxis(javafx.geometry.Point3D.ZERO.add(0, 1, 0));
        rotateTransition.play();

        return rotateTransition;
    }

    /**
     * Sets a fade transition animation on the specified node.
     *
     * @param node The node to apply the animation to.
     */
    public static void setAnimationfadeTransition(Node node) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        fadeIn.play();
    }

    /**
     * Sets a blinking fade transition animation on the specified node.
     *
     * @param node The node to apply the animation to.
     * @return The FadeTransition object.
     */
    public static FadeTransition setAnimationBlink(Node node) {
        node.setOpacity(0.4);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.4), node);
        fadeTransition.setFromValue(0.4);
        fadeTransition.setToValue(0.2);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        return fadeTransition;
    }

    /**
     * Creates a rain drop animation on the specified AnchorPane.
     *
     * @param node The AnchorPane to apply the animation to.
     */
    public static void createAnimationDropRain(AnchorPane node) {
        int NUM_DROPS = 20;
        int WIDTH = (int) node.getPrefWidth();
        int HEIGHT = (int) node.getPrefHeight();
        Random random = new Random();
        List<Circle> raindrops = new ArrayList<>();
        List<FadeTransition> listFadeIn = new ArrayList<>();

        for (int i = 0; i < NUM_DROPS; i++) {
            Circle drop = new Circle(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2 + random.nextInt(3));
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), drop);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDuration(Duration.seconds(2));
            fadeIn.setCycleCount(Timeline.INDEFINITE);
            fadeIn.setAutoReverse(true);
            listFadeIn.add(fadeIn);
            drop.setFill(Color.rgb(209, 202, 244, 1));
            raindrops.add(drop);
            node.getChildren().add(drop);
        }
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            int randcount = random.nextInt(NUM_DROPS);
            if (listFadeIn.get(randcount).getStatus() == Animation.Status.RUNNING) {
                listFadeIn.get(randcount).pause();
            } else {
                listFadeIn.get(randcount).play();
            }
            for (Circle drop : raindrops) {
                drop.setCenterY(drop.getCenterY() + 0.5);
                if (drop.getCenterY() > HEIGHT) {
                    drop.setCenterY(0);
                    drop.setCenterX(random.nextInt(WIDTH));
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}