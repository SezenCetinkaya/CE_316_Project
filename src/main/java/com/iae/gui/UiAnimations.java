package com.iae.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

/**
 * Reusable JavaFX transitions and micro-interactions (no third-party libraries).
 */
public final class UiAnimations {

    private static final String PULSE_KEY = "iae-pulse-animation";
    private static final String SHADOW_KEY = "iae-card-shadow";

    private UiAnimations() {}

    public static void fadeIn(Node node) {
        fadeIn(node, Duration.ZERO, Duration.millis(350));
    }

    public static void fadeIn(Node node, Duration delay, Duration duration) {
        if (node == null) {
            return;
        }
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(delay);
        fade.setInterpolator(Interpolator.EASE_OUT);
        fade.play();
    }

    public static void fadeInUp(Node node, Duration delay) {
        if (node == null) {
            return;
        }
        node.setOpacity(0);
        node.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(Duration.millis(450), node);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.setDelay(delay);
        parallel.play();
    }

    public static void staggerFadeInUp(List<? extends Node> nodes, Duration initialDelay, Duration stagger) {
        Duration delay = initialDelay;
        for (Node node : nodes) {
            fadeInUp(node, delay);
            delay = delay.add(stagger);
        }
    }

    public static void scaleIn(Node node, Duration delay) {
        if (node == null) {
            return;
        }
        node.setScaleX(0.92);
        node.setScaleY(0.92);
        node.setOpacity(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(380), node);
        scale.setFromX(0.92);
        scale.setFromY(0.92);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.setDelay(delay);
        parallel.play();
    }

    public static void applyCardShadow(Node node) {
        if (node == null || node.getProperties().containsKey(SHADOW_KEY)) {
            return;
        }
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setColor(Color.color(0.12, 0.18, 0.28, 0.12));
        node.setEffect(shadow);
        node.getProperties().put(SHADOW_KEY, Boolean.TRUE);

        node.setOnMouseEntered(e -> {
            DropShadow hover = new DropShadow();
            hover.setRadius(18);
            hover.setOffsetY(6);
            hover.setColor(Color.color(0.12, 0.18, 0.28, 0.18));
            node.setEffect(hover);
        });
        node.setOnMouseExited(e -> node.setEffect(shadow));
    }

    public static void wireButtonPress(Button button) {
        if (button == null) {
            return;
        }
        button.setOnMousePressed(e -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(80), button);
            press.setToX(0.96);
            press.setToY(0.96);
            press.play();
        });
        button.setOnMouseReleased(e -> {
            ScaleTransition release = new ScaleTransition(Duration.millis(120), button);
            release.setToX(1);
            release.setToY(1);
            release.setInterpolator(Interpolator.EASE_OUT);
            release.play();
        });
    }

    public static Animation startPulse(Node node) {
        stopPulse(node);
        if (node == null) {
            return null;
        }
        ScaleTransition pulse = new ScaleTransition(Duration.millis(700), node);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
        node.getProperties().put(PULSE_KEY, pulse);
        return pulse;
    }

    public static void stopPulse(Node node) {
        if (node == null) {
            return;
        }
        Object stored = node.getProperties().remove(PULSE_KEY);
        if (stored instanceof ScaleTransition pulse) {
            pulse.stop();
            node.setScaleX(1);
            node.setScaleY(1);
        }
    }

    public static void flashSuccess(Node node) {
        if (node == null) {
            return;
        }
        DropShadow glow = new DropShadow();
        glow.setRadius(16);
        glow.setColor(Color.web("#1a7f4b", 0.45));

        SequentialTransition seq = new SequentialTransition();
        FadeTransition brighten = new FadeTransition(Duration.millis(150), node);
        brighten.setFromValue(node.getOpacity());
        brighten.setToValue(1);

        Timeline hold = new Timeline(new KeyFrame(Duration.millis(400), ev -> node.setEffect(glow)));
        Timeline clear = new Timeline(new KeyFrame(Duration.millis(1), ev -> node.setEffect(null)));

        seq.getChildren().addAll(brighten, hold, clear);
        seq.play();
    }

    public static void crossfadeLabelText(Label label, String newText) {
        if (label == null) {
            return;
        }
        FadeTransition out = new FadeTransition(Duration.millis(120), label);
        out.setFromValue(1);
        out.setToValue(0.3);
        out.setOnFinished(e -> {
            label.setText(newText);
            FadeTransition in = new FadeTransition(Duration.millis(220), label);
            in.setFromValue(0.3);
            in.setToValue(1);
            in.setInterpolator(Interpolator.EASE_OUT);
            in.play();
        });
        out.play();
    }

    public static void bounceLabel(Label label) {
        if (label == null) {
            return;
        }
        ScaleTransition scale = new ScaleTransition(Duration.millis(280), label);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.18);
        scale.setToY(1.18);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.setInterpolator(Interpolator.EASE_OUT);
        scale.play();
    }

    public static void shake(Node node) {
        if (node == null) {
            return;
        }
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setToX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> node.setTranslateX(0));
        shake.play();
    }

    public static void slideInRight(Node node, Duration duration) {
        if (node == null) {
            return;
        }
        node.setOpacity(0);
        node.setTranslateX(40);
        ParallelTransition parallel = new ParallelTransition();
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(40);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        parallel.getChildren().addAll(fade, slide);
        parallel.play();
    }

    public static void fadeSceneIn(Node root) {
        if (root == null) {
            return;
        }
        root.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);
        fade.play();
    }

    public static void spinProgress(Node node) {
        if (node == null) {
            return;
        }
        Timeline spin = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(1.2), new KeyValue(node.rotateProperty(), 360))
        );
        spin.setCycleCount(Animation.INDEFINITE);
        spin.play();
        node.getProperties().put("iae-spin", spin);
    }

    public static void stopSpin(Node node) {
        if (node == null) {
            return;
        }
        Object stored = node.getProperties().remove("iae-spin");
        if (stored instanceof Timeline spin) {
            spin.stop();
            node.setRotate(0);
        }
    }

    public static void highlightField(Node field) {
        if (field == null) {
            return;
        }
        ScaleTransition pulse = new ScaleTransition(Duration.millis(200), field);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(1.02);
        pulse.setToY(1.02);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
    }
}
