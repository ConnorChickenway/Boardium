package xyz.connorchickenway.boardium.api;

import java.util.List;

public interface Scoreboardium {

    void setTitle( String text );

    void setLine( int index , String text );

    void setLines( List<String> lines );

    void removeLine( int index );

    boolean isDestroyed();

    void destroy();

}
