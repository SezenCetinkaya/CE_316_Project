package com.iae.gui;

import com.iae.gui.support.FxTestHelper;
import com.iae.gui.support.JavaFxExtension;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxExtension.class)
class UiAnimationsTest {

    @Test
    void fadeIn_setsOpacityToZeroThenAnimates() throws Exception {
        FxTestHelper.runOnFxThread(() -> {
            Label label = new Label("test");
            label.setOpacity(1);

            UiAnimations.fadeIn(label, Duration.ZERO, Duration.millis(50));

            assertEquals(0, label.getOpacity(), 0.01);
        });
    }

    @Test
    void fadeInUp_appliesTranslateY() throws Exception {
        FxTestHelper.runOnFxThread(() -> {
            Label label = new Label("slide");
            UiAnimations.fadeInUp(label, Duration.ZERO);
            assertEquals(20, label.getTranslateY(), 0.01);
            assertEquals(0, label.getOpacity(), 0.01);
        });
    }

    @Test
    void pulse_startAndStop_restoresScale() throws Exception {
        FxTestHelper.runOnFxThread(() -> {
            Button button = new Button("Run");
            UiAnimations.startPulse(button);
            assertNotNull(button.getProperties().get("iae-pulse-animation"));

            UiAnimations.stopPulse(button);
            assertEquals(1, button.getScaleX(), 0.01);
            assertEquals(1, button.getScaleY(), 0.01);
        });
    }

    @Test
    void applyCardShadow_setsDropShadowEffect() throws Exception {
        FxTestHelper.runOnFxThread(() -> {
            Label card = new Label("card");
            assertNull(card.getEffect());
            UiAnimations.applyCardShadow(card);
            assertNotNull(card.getEffect());
            assertTrue(card.getProperties().containsKey("iae-card-shadow"));
        });
    }

    @Test
    void nullNodes_doNotThrow() throws Exception {
        FxTestHelper.runOnFxThread(() -> {
            assertDoesNotThrow(() -> {
                UiAnimations.fadeIn(null);
                UiAnimations.startPulse(null);
                UiAnimations.stopPulse(null);
                UiAnimations.shake(null);
            });
        });
    }
}
