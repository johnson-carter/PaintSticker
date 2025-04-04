public class Sticker {
    private String sticker;
    private int xCord;
    private int yCord;

    public Sticker(String sticker, int xCord, int yCord){
        this.sticker = sticker;
        this.xCord = xCord;
        this.yCord = yCord;
    }
    public String getSticker(){
        return sticker;
    }
    public int getXval(){
        return xCord;
    }
    public int getYval(){
        return yCord;
    }
}
