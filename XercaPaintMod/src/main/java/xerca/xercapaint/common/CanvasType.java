package xerca.xercapaint.common;


public enum CanvasType {
    SMALL, LARGE, LONG, TALL;

    public static CanvasType fromByte(byte x) {
        switch(x) {
            case 0:
                return SMALL;
            case 1:
                return LARGE;
            case 2:
                return LONG;
            case 3:
                return TALL;
        }
        return null;
    }

    public static int getWidth(CanvasType canvasType){
        switch (canvasType){
            case SMALL:
            case TALL:
                return 16;
            case LARGE:
            case LONG:
                return 32;
        }
        return 0;
    }

    public static int getHeight(CanvasType canvasType){
        switch (canvasType){
            case SMALL:
            case LONG:
                return 16;
            case LARGE:
            case TALL:
                return 32;
        }
        return 0;
    }
}