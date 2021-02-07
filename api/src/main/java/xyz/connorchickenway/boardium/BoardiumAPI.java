package xyz.connorchickenway.boardium;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.connorchickenway.boardium.api.Scoreboardium;

public class BoardiumAPI {

    private Class<?> classAPI;
    private boolean serverVersion = true;

    private BoardiumAPI() {
        String versionName = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];

        try {
            classAPI = Class.forName( "xyz.connorchickenway.boardium.nms."
                    + versionName + ".ScoreboardPacket_" + versionName );
        } catch ( ClassNotFoundException e ) {
            System.out.println( "The server's version ( " + versionName + " ) isn't supported." );
            serverVersion = false;
        }

    }

    public boolean isVersionSupported() {
        return serverVersion;
    }

    public Scoreboardium createScoreboard( Player player ) {
        try {
            return ( Scoreboardium ) classAPI.getConstructors()[ 0 ].newInstance( player );
        } catch ( Exception ex ) {
        }
        return null;
    }

    private static BoardiumAPI instance;

    public static BoardiumAPI getInstance() {
        return instance != null ? instance : ( instance = new BoardiumAPI() );
    }

}
