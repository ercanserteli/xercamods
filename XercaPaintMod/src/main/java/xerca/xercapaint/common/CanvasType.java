package xerca.xercapaint.common;


public enum CanvasType {
    SMALL, LARGE;

    public static CanvasType fromByte(byte x) {
        switch(x) {
            case 0:
                return SMALL;
            case 1:
                return LARGE;
        }
        return null;
    }

    public static int getWidth(CanvasType canvasType){
        switch (canvasType){
            case SMALL:
                return 16;
            case LARGE:
                return 32;
        }
        return 0;
    }

    public static int getHeight(CanvasType canvasType){
        switch (canvasType){
            case SMALL:
                return 16;
            case LARGE:
                return 32;
        }
        return 0;
    }
}