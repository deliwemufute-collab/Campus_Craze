package org.example.game;
import com.sun.javafx.application.PlatformImpl;
import org.junit.jupiter.api.BeforeAll;

public class HUDManagerTest {
    // Boot the JavaFX runtime before tests run, as JUnit does not start it automatically
    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }
}