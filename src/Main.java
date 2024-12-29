import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;

public class Main implements NativeKeyListener {
    private boolean isActivated = false;

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
            System.out.println("JNativeHook initialized successfully.");
            GlobalScreen.addNativeKeyListener(new Main());
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int keyCode = e.getKeyCode();

        // Activation
        if (isCtrlPressed(e) && keyCode == NativeKeyEvent.VC_2 && !isActivated) {
            isActivated = true;
            System.out.println("Activated!");
        }

        // Deactivation
        if (isCtrlPressed(e) && keyCode == NativeKeyEvent.VC_3 && isActivated) {
            isActivated = false;
            System.out.println("Deactivated!");
        }

        // Trigger
        if (isActivated && keyCode == NativeKeyEvent.VC_ENTER) {
            createNewWindow();
            playSound("soft-notice-146623.wav"); // Play the sound
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    private boolean isCtrlPressed(NativeKeyEvent e) {
        return (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0;
    }

    private void createNewWindow() {
        String message = "I love you";

        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int randomX = (int) (Math.random() * (screenWidth - 300));
        int randomY = (int) (Math.random() * (screenHeight - 150));

        JFrame frame = new JFrame("Love Note");
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Quintessential", Font.BOLD, 20));
        label.setForeground(Color.WHITE);

        // Set frame properties
        frame.setSize(300, 150);
        frame.setLocation(randomX, randomY);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setOpacity(1f);
        frame.setShape(new RoundRectangle2D.Float(0, 0, 300, 150, 50, 50));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, Color.PINK, getWidth(), getHeight(), Color.MAGENTA);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        frame.add(panel);
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private void playSound(String soundFileName) {
        try {

            InputStream audioSource = getClass().getResourceAsStream("/" + soundFileName);
            if (audioSource == null) {
                System.err.println("Sound file not found: " + soundFileName);
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioSource);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound.");
            e.printStackTrace();
        }
    }
}



