package com.example.cats;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class HelloController {


    public CheckBox multiplayerCheck;
    public TextField player2IP;
    public TextField player2Port;

    public Pane pane;
    public VBox cicak;
    public ImageView ivP1;
    public ImageView ivP2;
    public Label player1Port;
    public Label info;

    DatagramSocket socket = null;


    Image[] icon = new Image[5];
    Label[] cat = new Label[5];

    int p1i = 1;
    int p1x = 128;
    int p1y = 128;

    int p2i = 0;
    int p2x = 572;
    int p2y = 572;



    public void initialize(){
        for(int i = 0; i < 5; i++){
            icon[i] = new Image(getClass().getResourceAsStream("icons/cat" + i + ".png"));
            if (i > 0){
                cat[i] = new Label("");
                cat[i].setGraphic(new ImageView(icon[i]));
                int ii = i;
                cat[i].setOnMousePressed(mouseEvent -> selectCat(ii));
                cicak.getChildren().add(cat[i]);
            }
        }
        selectCat(1);
        try {
            socket = new DatagramSocket(678);
            player1Port.setText("Player1 on port "+socket.getLocalPort());
            System.out.printf("Player1 recieving on port "+socket.getLocalPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread fogadoSzal = new Thread(new Runnable() {
            @Override
            public void run() {
                fogad();
            }
        });
        fogadoSzal.setDaemon(true);
        fogadoSzal.start();
        Platform.runLater(() -> pane.requestFocus());

    }

    public void selectCat(int id){
        cat[p1i].setStyle("");
        p1i = id;
        cat[p1i].setStyle("-fx-background-color: lightgrey");
        ivP1.setImage(icon[id]);
    }

    public void onKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.W && p1y > 80) p1y -= 16;
        if(e.getCode() == KeyCode.S && p1y < 620) p1y += 16;
        if(e.getCode() == KeyCode.A && p1x > 80) p1x -= 16;
        if(e.getCode() == KeyCode.D && p1x < 620) p1x += 16;
        ivP1.setLayoutX(p1x-64);
        ivP1.setLayoutY(p1y-64);
        if(multiplayerCheck.isSelected()){
            kuld(String.format("%d;%d;%d",p1i,p1x,p1y),player2IP.getText(),player2Port.getText());
        }
    }

    public void kuld(String msg, String ip, String port){
        try {
            byte[] data = msg.getBytes("utf-8");
            InetAddress ip4 = Inet4Address.getByName(ip);
            int prt = Integer.parseInt(port);
            DatagramPacket packet = new DatagramPacket(data,data.length,ip4,prt);
            socket.send(packet);
            info.setText("Elküldve "+ msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fogad(){
        byte[] data = new byte[256];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true) {
            try {
                socket.receive(packet);
                String msg = new String(data, 0, packet.getLength(), "utf-8");
                String ip = packet.getAddress().getHostName();
                String port = packet.getPort() + "";
                Platform.runLater(() -> onFogadUzenet(msg, ip, port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onFogadUzenet(String msg, String ip, String port){
        if(multiplayerCheck.isSelected() && ip.equals(player2IP.getText())){
            String[] s = msg.split(";");
            p2i = Integer.parseInt(s[0]);
            p2x = Integer.parseInt(s[1]);
            p2y = Integer.parseInt(s[2]);
            ivP2.setLayoutX(p2x-64);
            ivP2.setLayoutY(p2y-64);
            ivP2.setImage(icon[p2i]);
        }
    }

    public void onP2Click() {
        if(multiplayerCheck.isSelected()){
            player2IP.setDisable(true);
            player2Port.setDisable(true);
        }else{
            player2IP.setDisable(false);
            player2Port.setDisable(false);
        }
        pane.requestFocus();
    }
}