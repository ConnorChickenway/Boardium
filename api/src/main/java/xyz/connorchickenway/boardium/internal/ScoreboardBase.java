package xyz.connorchickenway.boardium.internal;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.connorchickenway.boardium.api.Scoreboardium;

import java.util.List;


public abstract class ScoreboardBase implements Scoreboardium, PacketHandler {

    private final Player player;
    private final String objectiveName;
    private final List<FakeTeam> lines;
    private boolean destroyed;

    public ScoreboardBase( Player player ) {
        this.player = player;
        String tempText = "FakeBoard|" + player.getName();
        objectiveName = tempText.length() > 16 ? tempText.substring( 0 , 16 ) : tempText;
        lines = Lists.newArrayList();
        destroyed = false;
        /*
         * 0 = create
         * 1 = remove
         * 2 = update
         */
        sendPacket( player , createObjectivePacket( objectiveName , 0 , "" ) );
        sendPacket( player , createObjectiveSlotPacket( objectiveName ) );
    }

    @Override
    public void setTitle( String text ) {
        if ( destroyed ) return;
        if ( text == null || text.isEmpty() ) return;
        String tempText = ChatColor.translateAlternateColorCodes( '&' , text );
        if ( ! is1_13orAbove() && tempText.length() > 32 )
            tempText = tempText.substring( 0 , 32 );
        sendPacket( player , createObjectivePacket( objectiveName , 2 , tempText ) );
    }

    @Override
    public void setLine( int index , String originalText ) {
        if ( destroyed ) return;
        if ( index < 1 || index > 15 ) return;
        FakeTeam fakeTeam = getFakeTeam( index );
        boolean created = true;
        if ( fakeTeam == null ) {
            fakeTeam = FakeTeam.createFakeTeam( index );
            lines.add( fakeTeam );
            created = false;
        }
        Presuffix presuffix = Presuffix.createPresuffix();
        if ( originalText == null || originalText.isEmpty() )
            presuffix.setPrefix( fakeTeam.getIdentifier() );
        else {
            String text = ChatColor.translateAlternateColorCodes( '&' , originalText );
            if ( is1_13orAbove() || text.length() <= 16 )
                presuffix.setPrefix( text );
            else {
                int i = text.charAt( 15 ) == ChatColor.COLOR_CHAR ? 15 : 16;
                String prefix = text.substring( 0 , i );
                String suffix = ( i == 16 ? ChatColor.getLastColors( prefix ) : "" ) + text.substring( i );
                presuffix.setPrefix( prefix );
                presuffix.setSuffix( suffix.length() > 16 ? suffix.substring( 0 , 16 ) : suffix );
            }
        }
        if ( ! created )
            sendPacket( player , createScorePacket( fakeTeam.getIdentifier() , objectiveName , 0 , index ) );
        sendPacket( player , createTeamPacket( fakeTeam.getName() , created ? 2 : 0 ,
                presuffix.getPrefix() , presuffix.getSuffix() , fakeTeam.getIdentifier() ) );
    }

    @Override
    public void setLines( List<String> lines ) {
        if ( destroyed ) return;
        if ( lines.size() == 0 ) return;
        for ( int index = ( lines.size() > 15 ? 15 : lines.size() ); index >= 1; index-- )
            setLine( index , lines.get( index - 1 ) );
    }

    @Override
    public void removeLine( int index ) {
        if ( destroyed ) return;
        if ( index < 1 || index > 15 ) return;
        FakeTeam fakeTeam = getFakeTeam( index );
        if ( fakeTeam != null ) {
            sendPacket( player , createScorePacket( fakeTeam.getIdentifier() , objectiveName , 1 , index ) );
            sendPacket( player , createTeamPacket( fakeTeam.getName() ,
                    1 , null , null , null ) );
            lines.remove( fakeTeam );
        }
    }

    @Override
    public void destroy() {
        if ( destroyed ) return;
        sendPacket( player , createObjectivePacket( objectiveName , 1 , null ) );
        lines.forEach( fakeTeam ->
        {
            sendPacket( player , createScorePacket( fakeTeam.getIdentifier() , objectiveName , 1 , fakeTeam.getIndex() ) );
            sendPacket( player , createTeamPacket( fakeTeam.getName() ,
                    1 , null , null , null ) );
        } );
        lines.clear();
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean is1_13orAbove() {
        return this.getNMSVersion().isAboveOrEqual( NMSVersion.V1_13_R1 );
    }

    public FakeTeam getFakeTeam( int index ) {
        for ( FakeTeam fakeTeam : lines )
            if ( fakeTeam.getIndex() == index ) return fakeTeam;
        return null;
    }

    private static class FakeTeam {

        private final String name, identifier;
        private final int index;

        private FakeTeam( int index ) {
            this.index = index;
            this.name = "FAKE_SLOT-" + index;
            this.identifier = ChatColor.values()[ index ].toString() + ChatColor.RESET;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static FakeTeam createFakeTeam( int index ) {
            return new FakeTeam( index );
        }

    }

    private static class Presuffix {
        private String prefix, suffix;

        public void setPrefix( String prefix ) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }


        public String getSuffix() {
            return suffix;
        }

        public void setSuffix( String suffix ) {
            this.suffix = suffix;
        }

        public static Presuffix createPresuffix() {
            return new Presuffix();
        }

    }

}

