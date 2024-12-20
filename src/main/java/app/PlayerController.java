package app;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
public class PlayerController implements Initializable{
    @FXML
    private Text nameDisplay;
    @FXML
    private Text moneyDisplay;
    @FXML 
    private Text stepDisplay;
    @FXML
    private Button tossButton;
    @FXML 
    private Circle playCircle;
    @FXML Button getLuckButton;
    @FXML private Text luckText;
    @FXML TextField textField;
    @FXML Text player2NameDisplay;
    @FXML Text player2MoneyDisplay;
    //@FXML Text player2StepDisplay;
    @FXML Circle player2Circle;
    @FXML private Pane pane;
    @FXML private Pane popUpPane;
    @FXML private Button popCloseButton;
    @FXML private Button popYesButton;
    @FXML private Button popNoButton;
    @FXML private Button popNextButton;
    @FXML private Button popGoButton;
    @FXML private Text popText;
    public List<Rectangle> tile = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();
    private Player player1;
    private Player player2;
    private static Player curPlayer;
    Random random = new Random();
    private ArrayList<Player> p = new ArrayList<>();
    IntegerProperty Money1;
    IntegerProperty Money2;
    IntegerProperty Step = new SimpleIntegerProperty();
    StringProperty Player1Name = new SimpleStringProperty();
    StringProperty Player2Name = new SimpleStringProperty();
    
    int count = 0;
    int dice1;
    int dice2;
    Location l;
    Rectangle rect;
    double posX;
    double posY;

    static int index = 0;
    int startMoney = 500;
    //#region initialize  
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        init();
        update();
    }
    public void init(){
        player1 = new Player(1000, "Jame");
        player2 = new Player(1000,"Billy");
        player1.setCircle(playCircle);
        player2.setCircle(player2Circle);
        curPlayer = player1;
        p.add(player1);
        p.add(player2);
        nameDisplay.setText(player1.getName());
        moneyDisplay.setText(""+player1.getMoney());
        player2NameDisplay.setText(player2.getName());
        player2MoneyDisplay.setText(""+player2.getMoney());
        Money1 = player1.moneyProperty();
        Money2 = player2.moneyProperty();
        stepDisplay.setText(""+Step.getValue());
        //tossButtonCheck.set(false);
        //#region Add rectangle to list
        List<Node> nodes = pane.getChildren();
        for(Node node:nodes){
            if(node instanceof Rectangle){
                Rectangle rectangle = (Rectangle) node;
                //rectangle.setDisable(true);
                tile.add(rectangle);
            }
        }//#endregion
        
        /*
         * id   mean
         * 0    jail tile
         * 1    property tile
         * 2    start tile
         * 3    GoToJail tile
         * 4    go to any tile
         * 5    Special property tile
         */
        locations.add(new EventTile(2));
        locations.add(new Property(1, 150, 30));
        locations.add(new EventTile(0));
        locations.add(new Property(1, 250, 50));
        locations.add(new EventTile(3));
        locations.add(new Property(5, 350, 70));
        locations.add(new EventTile(4));
        locations.add(new Property(1, 450, 90));
        rect = tile.get(0);
        posX = rect.getWidth()/2+rect.getLayoutX();
        posY = rect.getHeight()/2+rect.getLayoutY();
        playCircle.setLayoutX(posX);
        playCircle.setLayoutY(posY);
        player2Circle.setLayoutX(posX);
        player2Circle.setLayoutY(posY);

        String audioFile = "monopoly/src/main/resources/BGMusic.mp3";
        Media media = new Media(new File(audioFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(javafx.util.Duration.ZERO));
        mediaPlayer.play();
    }
    public void update(){

        Money1.addListener((observer,oldval,newval) ->{
           moneyDisplay.setText(String.valueOf(newval.intValue()));
        });
        Money2.addListener((observer,oldval,newval) ->{
            player2MoneyDisplay.setText(String.valueOf(newval.intValue()));
        });
        Step.addListener((obs,oldval,newval) ->{
            stepDisplay.setText(String.valueOf(newval.intValue()));
        });
        // tossButtonCheck.addListener((obs,oldval,newval)->{
        //     tossButton.setDisable(newval);
        // });
    }
//#endregion
   

    //#region Add and Minus money
    public void addMoney() {
        curPlayer.setMoney(curPlayer.getMoney() + 1000);
        checkBankrupt();
    }

    public void minusMoney() {
        curPlayer.setMoney(curPlayer.getMoney() - 1000);
        checkBankrupt();
    }
    //#endregion
    private int iteratorIndex(){
        index++;
        if(index >= p.size()){
            index = 0;
        }
        return index;
    }
    public void DoubleDice() throws InterruptedException{
        dice1 = 2;
        dice2 =2;
        Step.set(dice1+dice2);
        if(curPlayer.CheckDouble_count()){
            curPlayer.PlayerPos(2);
            rect = tile.get(curPlayer.PlayerPos());
            posX = rect.getWidth()/2+rect.getLayoutX();
            posY = rect.getHeight()/2+rect.getLayoutY();
            curPlayer.getCircle().setLayoutX(posX);
            curPlayer.getCircle().setLayoutY(posY);
            //System.out.println(curPlayer.getName()+" is in jailed");
        }
        else if(curPlayer.getWaitInjaild()>0){
            boolean sameDice = dice1==dice2;
            if(sameDice){
                //System.out.println(curPlayer.getName()+"is geted out of jail");
                moveCircle(dice1+dice2);
            }
            else{
                curPlayer.setWaitinJail(curPlayer.getWaitInjaild()-1);
                curPlayer = p.get(iteratorIndex());
                //System.out.println("You didn't get double wait in jail for "+curPlayer.getWaitInjaild() + "turn");
            }
        }
        else{
            moveCircle(dice1+dice2);

        }
    }
    public void TossDice(ActionEvent event) throws InterruptedException{
        dice1 = random.nextInt(6)+1;
        dice2 = random.nextInt(6)+1;
        Step.set(dice1+dice2);
        System.out.println(curPlayer.getName()+" turn");
        // Platform.runLater(()->{
            //     movePlayer.run();
            // });
            if(curPlayer.CheckDouble_count()){
                curPlayer.PlayerPos(2);
                rect = tile.get(curPlayer.PlayerPos());
                posX = rect.getWidth()/2+rect.getLayoutX();
                posY = rect.getHeight()/2+rect.getLayoutY();
                curPlayer.getCircle().setLayoutX(posX);
                curPlayer.getCircle().setLayoutY(posY);
                //System.out.println(curPlayer.getName()+" is in jailed");
            }
            else if(curPlayer.getWaitInjaild()>0){
                boolean sameDice = dice1==dice2;
                if(sameDice){
                    //System.out.println(curPlayer.getName()+"is geted out of jail");
                    curPlayer.setDouble_countToZero();
                    moveCircle(dice1+dice2);
                }
                else{
                    curPlayer.setWaitinJail(curPlayer.getWaitInjaild()-1);
                    //System.out.println(curPlayer.getName()+" didn't get double wait in jail for "+curPlayer.getWaitInjaild() + "turn");
                }
            }
            else{
                moveCircle(dice1+dice2);
    
            }
        
    }
    //move player circle with animation 'dice1 +dice' times
    public void moveCircle(int Sumdice) throws InterruptedException {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.25),event ->{
            curPlayer.PlayerPos(curPlayer.PlayerPos()+1);
            rect = tile.get(curPlayer.PlayerPos());
            posX = rect.getWidth()/2+rect.getLayoutX();
            posY = rect.getHeight()/2+rect.getLayoutY();
            curPlayer.getCircle().setLayoutX(posX);
            curPlayer.getCircle().setLayoutY(posY);
            count++;

            l = locations.get(curPlayer.PlayerPos());
            tossButton.setDisable(true);
            switch (l.id) {
                case 2:
                    ((EventTile) l).giveMoney(curPlayer);
                    
                    break;
            }
            
            //when count or animation finish reset tossbutton, reset count to 0, show popup window and change popwindow text
            if(count == Sumdice){
                if(dice1==dice2){
                    curPlayer.Inc_DoubleC();
                    //System.out.println(curPlayer.getDoubleCount());
                }
                tossButton.setDisable(false);
                luckText.setVisible(false);
                checkTile(l);
                count = 0;
            }
        }));
        timeline.setCycleCount(Sumdice);
        timeline.play();
        
    }
    public void Goback(int Sumdice) throws InterruptedException {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.25),event ->{
            curPlayer.PlayerPos(curPlayer.PlayerPos()-1);
            rect = tile.get(curPlayer.PlayerPos());
            posX = rect.getWidth()/2+rect.getLayoutX();
            posY = rect.getHeight()/2+rect.getLayoutY();
            curPlayer.getCircle().setLayoutX(posX);
            curPlayer.getCircle().setLayoutY(posY);
            count++;

            l = locations.get(curPlayer.PlayerPos());
            tossButton.setDisable(true);
            switch (l.id) {
                case 2:
                    ((EventTile) l).giveMoney(curPlayer);
                    
                    break;
            }
            
            //when count or animation finish reset tossbutton, reset count to 0, show popup window and change popwindow text
            if(count == Sumdice){
                if(dice1==dice2){
                    curPlayer.Inc_DoubleC();
                    //System.out.println(curPlayer.getDoubleCount());
                }
                tossButton.setDisable(false);
                luckText.setVisible(false);
                checkTile(l);
                
                count = 0;
            }
        }));
        timeline.setCycleCount(Sumdice);
        timeline.play();
        
    }
    //show popup window
    public void popUpwindow(){
        popUpPane.setVisible(true);
    }
    public void setButton(int id){
        switch (id) {
            case 1:
                popCloseButton.setVisible(false);
                popYesButton.setVisible(true);
                popNoButton.setVisible(true);
                popNextButton.setVisible(false);
                popGoButton.setVisible(false);
                break;

            case 2: 
                popCloseButton.setVisible(false);
                popYesButton.setVisible(false);
                popNoButton.setVisible(false);
                popNextButton.setVisible(true);
                popGoButton.setVisible(false);
                break;
            case 3:
                popCloseButton.setVisible(false);
                popYesButton.setVisible(false);
                popNoButton.setVisible(false);
                popNextButton.setVisible(false);
                popGoButton.setVisible(true);
                break;
            case 0:
                popCloseButton.setVisible(true);
                popYesButton.setVisible(false);
                popNoButton.setVisible(false);
                popNextButton.setVisible(false);
                popGoButton.setVisible(false);
                break;
        }
    }
    
    //when click button to close popup window
    public void Exit(ActionEvent event){
        if(dice1!=dice2){
            curPlayer.setDouble_countToZero();
            curPlayer = p.get(iteratorIndex());
            curPlayer.getCircle().toFront();
        }
        popUpPane.setVisible(false);
    }
    public void buyProperty(ActionEvent event){
        if(curPlayer.getMoney()<((Property)l).getPrice()){
            popYesButton.setDisable(true);
        }
        else{
            popYesButton.setDisable(false);
        }
        Player owner = ((Property)l).getOwner();
        if(owner == null || owner== curPlayer){
            curPlayer.setMoney(curPlayer.getMoney()-((Property) l).getPrice());
        }
        else{
            curPlayer.setMoney(curPlayer.getMoney()-((Property) l).getPrice());
            owner.setMoney(owner.getMoney()+((Property)l).getPrice());
        }
        ((Property) l).setOwner(curPlayer);
        Rectangle rect = tile.get(curPlayer.PlayerPos());
        rect.setFill(curPlayer.getCircle().getFill());
        if(l.getID() ==1){
            ((Property)l).UgpradeProp();
        }
        
        if(dice1!=dice2){
            curPlayer.setDouble_countToZero();
            curPlayer = p.get(iteratorIndex());
            curPlayer.getCircle().toFront();
        }
        popUpPane.setVisible(false);
    }

    public void NextButton(ActionEvent event){
        if(((Property) l).getUpgradeC()<=3){    
            popText.setText("Would you like to buy?\n"+((Property) l).getPrice()+ " baht, with upgrade "+((Property)l).getUpgradeC());
            setButton(1);
        }
        else{
            popText.setText("upgrade is max can't not purchase");
            setButton(0);
        }
        
    }
    public void GotoSelectTile() throws InterruptedException{
        int tileN = Integer.parseInt(textField.getText());
        moveCircle(tileN);
        textField.setVisible(false);
        tossButton.setDisable(false);
        popCloseButton.setDisable(false);
        //checkTile(l);
        popUpPane.setVisible(false);

    }
    public void checkTile(Location los){
        switch (los.getID()) {
            case 2:
                if(dice1!= dice2){
                curPlayer.setDouble_countToZero();
                curPlayer = p.get(iteratorIndex());
                curPlayer.getCircle().toFront();
                }
                break;
            
            case 1:
                Player owner = ((Property) los).getOwner();
                if(owner == null){
                    popText.setText("Would you like to buy?\n"+((Property) l).getPrice()+ " baht");
                    setButton(1);
                }
                else if(owner == curPlayer){
                    if(((Property) l).getUpgradeC()<=3){
                        popText.setText("Would you like to buy upgrade "+((Property) l).getUpgradeC()+"\n"+((Property) l).getPrice()+" baht");
                        setButton(1);
                    }
                    else{
                        popText.setText("upgrade is max can't not purchase");
                        setButton(0);
                    }
                }
                else{
                    popText.setText("You paid "+((Property) l).getPaid() + " to the "+owner.getName());
                    curPlayer.setMoney(curPlayer.getMoney()-((Property) l).getPaid());
                    owner.setMoney(owner.getMoney()+((Property) l).getPaid());
                    setButton(2);
                }
                popUpwindow();
                break;
            case 3:
                curPlayer.PlayerPos(2);
                rect = tile.get(curPlayer.PlayerPos());
                posX = rect.getWidth()/2+rect.getLayoutX();
                posY = rect.getHeight()/2+rect.getLayoutY();
                curPlayer.getCircle().setLayoutX(posX);
                curPlayer.getCircle().setLayoutY(posY);
                curPlayer.setWaitinJail(3);
                //System.out.println(curPlayer.getName()+" is in jailed");
                curPlayer = p.get(iteratorIndex());
                curPlayer.getCircle().toFront();
                // popText.setText("You are going to F");
                // setButton(0);
                break;
            case 4:
                popText.setText("How much tile do you want to go?");
                tossButton.setDisable(true);
                textField.setVisible(true);
                popCloseButton.setDisable(true);
                setButton(3);
                popUpwindow();
                break;
            case 5:
                Player o = ((Property)los).getOwner();
                if(o== null){
                    popText.setText("Would you like to buy?\n"+((Property) l).getPrice()+ " baht");
                    setButton(1);
                    popUpwindow();
                }
                else if(o==curPlayer){
                    break;
                }
                else{
                    popText.setText("You paid "+((Property) l).getPaid() + " to the "+o.getName());
                    curPlayer.setMoney(curPlayer.getMoney()-((Property) l).getPaid());
                    o.setMoney(o.getMoney()+((Property) l).getPaid());
                    setButton(0);
                    popUpwindow();
                }
                break;
            default:
                if(dice1!= dice2){
                    curPlayer.setDouble_countToZero();
                    curPlayer = p.get(iteratorIndex());
                    curPlayer.getCircle().toFront();
                }
                break;
            }   
        }
    public void getLuck() throws InterruptedException{
        int rand = random.nextInt(4)+1;
        switch (rand) {
            case 1:
                curPlayer.setMoney(curPlayer.getMoney()+100);
                luckText.setText(curPlayer.getName()+" get 100 baht");
                luckText.setVisible(true);
                break;
            case 2:
                curPlayer.setMoney(curPlayer.getMoney()-50);
                luckText.setText(curPlayer.getName()+" lose 50 baht");
                luckText.setVisible(true);
                break;
            case 3:
                luckText.setText(curPlayer.getName()+" move forward 2 times");
                luckText.setVisible(true);
                moveCircle(2);
                break;
            case 4:
                luckText.setText(curPlayer.getName()+" move backward 2 times");
                luckText.setVisible(true);
                Goback(2);
                break;
            
        }
    }
    
    public void checkBankrupt() {
        if (curPlayer.getMoney() <= 0) {
            for (Location lo : locations) {
                if (lo instanceof Property && ((Property) lo).getOwner() == curPlayer) {
                    /// System.out.println(((Property)lo).getOwner());
                    ((Property) lo).setOwner(null);
                    ((Property) lo).getRectangle().setFill(Color.WHITE);
                    ((Property) lo).resetUpgradeC();
                }
            }
            for (Player o : p) {
                if (o == curPlayer) {
                    curPlayer.getCircle().setVisible(false);
                    curPlayer.setMoney(0);
                    p.remove(curPlayer);
                    break;
                }
            }
            if (p.size() == 1) {
                luckText.setText(p.get(0).getName() + " wins");
                luckText.setVisible(true);
            } else {
                curPlayer = p.get(iteratorIndex());
            }
        }
    }
}
