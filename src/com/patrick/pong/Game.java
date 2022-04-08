package com.patrick.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Game extends JPanel implements KeyListener, Runnable {

    int pPoints;
    int ePoints;

    double pSpeed = 1;
    double eSpeed = 3;

    double eVis = 200;

    double pVelo;
    double eVelo;

    double bSpeed = 3;

    double bx;
    double by;

    double bxVelo;

    double px;
    double py;
    double ex;
    double ey;

    double centerX;
    double centerY;

    int paddleOffset = 100;

    int ballW = 8;

    int pW = 160;
    int pH = 8;

    boolean rightPressed;
    boolean leftPressed;

    boolean[] ballDir = {false, false};

    public static void main(String args[]) {
        //new Thread(new Game()).start();
        Game panel = new Game();
        panel.setDoubleBuffered(true);
        new Thread(panel).start();
        JFrame frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBackground(Color.GRAY);
        frame.add(panel);
        frame.setVisible(true);
        frame.addKeyListener(panel);
    }

    public Game() {
        setSize(600, 600);
        setVisible(true);
        setBackground(Color.GRAY);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.drawLine(0, (int) centerY, getWidth(), (int) centerY);

        g2.setColor(Color.GREEN);
        g2.fillOval((int) bx - (ballW / 2), (int) by - (ballW / 2), ballW, ballW);
        g2.drawString("P: " + pPoints, 20, getHeight() - 50);
        g2.drawString("E: " + ePoints, 320, getHeight() - 50);

        g2.setColor(Color.WHITE);

        g2.fillRect((int) px, (int) py, pW, pH);
        g2.fillRect((int) ex, (int) ey, pW, pH);

        g2.dispose();
    }

    void loop() {

        // key presses
        if(leftPressed) pVelo = -5;
        if(rightPressed) pVelo = 5;

        // points
        if (by > py + 160) { // enemy scored
            ePoints++;
            bx = getWidth() / 2;
            by = getHeight() / 2;
        }
        if (by < ey - 160) { // player scored
            pPoints++;
            bx = getWidth() / 2;
            by = getHeight() / 2;
            bSpeed += .5;
            if (pW > 10)
                pW -= 5;
            eVis += 25;
            eSpeed += .5;
        }

        // player movement
        if ((pVelo < 0.1 && pVelo > 0) || (pVelo > -0.1 && pVelo < 0))
            pVelo = 0; // reset
        if (pVelo < 0) {
            double move = px - (pSpeed * pVelo) > 0 ? pSpeed * pVelo : -px;
            px += move;
            pVelo += .5;
        } else if (pVelo > 0) {
            double move = px + (pSpeed * pVelo) < getWidth() - pW ? pSpeed * pVelo : (getWidth() - pW) - px;
            px += move;
            pVelo -= .5;
        }

        // enemy movement
        if (bx < ex + pW && Math.abs(by - ey) < eVis && ex > 0) {
            ex -= eSpeed;
            eVelo = -3;
        } else if (bx > ex && Math.abs(by - ey) < eVis && ex + pW < getWidth()) {
            ex += eSpeed;
            eVelo = 3;
        } else eVelo = 0;

        // ball move
        if ((bxVelo < 0.3 && bxVelo > 0) || (bxVelo > -0.3 && bxVelo < 0)) bxVelo = 0; // reset
        if (bxVelo > 0) bxVelo -= 0.1;
        if (bxVelo < 0) bxVelo += 0.1;

        // bounce off walls
        if (ballDir[0] == false && bx - (ballW / 2) > 0) bx -= bSpeed + bxVelo;
        else if (ballDir[0] == false) ballDir[0] = true; // flip

        if (ballDir[0] == true && bx + (ballW / 2) < getWidth()) bx += bSpeed + bxVelo;
        else if (ballDir[0] == true) ballDir[0] = false; // flip

        if (ballDir[1] == false) by -= bSpeed;
//        else if (ballDir[1] == false) ballDir[1] = true; // flip

        if (ballDir[1] == true) by += bSpeed;
//        else if (ballDir[1] == true) ballDir[1] = false; // flip

        // bounce off player
        if (close(by, py) && bx >= px - 2 && bx <= px + pW + 2) {
            ballDir[1] = !ballDir[1];
            if (pVelo > .2)
                flipX(true);
            else if (pVelo < -.2)
                flipX(false);
        }

        // bounce off enemy
        if (close(by, ey + pH) && bx >= ex - 2 && bx <= ex + pW + 2) {
            ballDir[1] = !ballDir[1];
            if (eVelo > .2)
                flipX(false);
            else if (eVelo < -.2)
                flipX(true);
        }
    }

    void flipX(boolean dir) {
        if (dir == true) bxVelo = 1;
        else bxVelo = 1;
    }

    boolean close(double v1, double v2) {
        if (Math.abs(v1 - v2) < 3) return true;
        return false;
    }

    @Override
    public void update(Graphics g) {
        super.update(g);

    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        centerX = w / 2;
        centerY = h / 2;
        bx = centerX;
        by = centerY;
        px = centerX - (pW / 2);
        py = h - pH - paddleOffset;
        ex = centerX - (pH / 2);
        ey = 0 + paddleOffset;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            rightPressed = false;
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            loop();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}