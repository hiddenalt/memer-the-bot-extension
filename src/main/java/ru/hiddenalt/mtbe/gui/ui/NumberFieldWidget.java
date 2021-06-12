package ru.hiddenalt.mtbe.gui.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NumberFieldWidget extends TextFieldWidget {
    private int min = 0;
    private int max = 255;

    public static final int MAX_NUMBER = Integer.MAX_VALUE;
    public static final int MIN_NUMBER = Integer.MIN_VALUE;

    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.of(""));
        this.setZeroOrMin();
    }

    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, int min, int max) {
        this(textRenderer, x, y, width, height);
        this.setMin(min);
        this.setMax(max);
    }



    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setZeroOrMin(){
        this.setText("" + Math.max(0, min));
    }

//    @Override
//    public boolean charTyped(char chr, int keyCode) {
//        String former = this.getText();
//        char[] arr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
//        if(!ArrayUtils.contains(arr, chr)) {
//            return false;
//        }

//        try{
//            if( (this.getText()+keyCode).equals("") ){
//                this.setZeroOrMin();
//                return false;
//            } else if (Integer.parseInt(this.getText()+keyCode) > this.max) {
//                return false;
//            }

//            // Reduce extra zero numbers
//            this.setText(Integer.parseInt(this.getText())+"");
//
//            return super.charTyped(chr, keyCode);
//        } catch (NumberFormatException ignored){
//            this.setText(former);
//        }
//        return false;
//    }

    @Override
    public void eraseCharacters(int characterOffset) {
        super.eraseCharacters(characterOffset);
//        if( (this.getText()).equals("") ){
//            this.setZeroOrMin();
//        }
    }

    @Override
    public void write(String string) {
//        if(string.trim().equals("")){
//            if ((0 < min)) {
//                string = ""+min;
//            } else {
//                string = "0";
//            }
//        }
//
        super.write(string);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if(Screen.isPaste(keyCode)){
//            try {
//                int val = Integer.parseInt(MinecraftClient.getInstance().keyboard.getClipboard());
//                if(val < this.min || val > this.max)
//                    return false;
//
//                this.setText("");
//                this.write(val+"");
//            } catch (NumberFormatException ignored){
//
//            }
//            return false;
//        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public int getValue() {
        try {
            int val = Integer.parseInt(this.getText());
            if(val < min || val > max) throw new NumberFormatException();

            return val;
        } catch(NumberFormatException e){
            return Math.max(0, min);
        }
    }

    public void setValue(int i){
        this.setText(""+i);
    }

}