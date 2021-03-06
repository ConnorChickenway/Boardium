package xyz.connorchickenway.boardium.nms.v1_13_R1;

import net.minecraft.server.v1_13_R1.*;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.connorchickenway.boardium.internal.NMSVersion;
import xyz.connorchickenway.boardium.internal.ScoreboardBase;

import static xyz.connorchickenway.boardium.util.ReflectionUtil.addObject;
import static xyz.connorchickenway.boardium.util.ReflectionUtil.setField;

public class ScoreboardPacket_v1_13_R1 extends ScoreboardBase {

    public ScoreboardPacket_v1_13_R1( Player player ) {
        super( player );
    }

    @Override
    public Object createObjectivePacket( String objectiveName , int mode , String text ) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        setField( packet , "a" , objectiveName );
        if ( mode == 0 || mode == 2 ) {
            setField( packet , "b" , new ChatComponentText( text ) );
            setField( packet , "c" , IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER );
        }
        setField( packet , "d" , mode );
        return packet;
    }

    @Override
    public Object createObjectiveSlotPacket( String objectiveName ) {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
        setField( packet , "a" , 1 );
        setField( packet , "b" , objectiveName );
        return packet;
    }

    @Override
    public Object createScorePacket( String scoreName , String objectiveName , int mode , int index ) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
        setField( packet , "a" , scoreName );
        setField( packet , "b" , objectiveName );
        if ( mode == 0 )
            setField( packet , "c" , index );
        setField( packet , "d" , ScoreboardServer.Action.values()[ mode ] );
        return packet;
    }

    @Override
    public Object createTeamPacket( String teamName , int mode , String prefix , String suffix , String scoreName ) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField( packet , "a" , teamName );
        setField( packet , "i" , mode );
        if ( mode == 0 || mode == 2 ) {
            if ( prefix != null )
                setField( packet , "c" , new ChatComponentText( prefix ) );
            if ( suffix != null )
                setField( packet , "d" , new ChatComponentText( suffix ) );
        }
        if ( mode == 0 )
            addObject( packet , "h" , scoreName );
        return packet;
    }

    @Override
    public void sendPacket( Player player , Object packet ) {
        ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( ( Packet<?> ) packet );
    }

    @Override
    public NMSVersion getNMSVersion() {
        return NMSVersion.V1_13_R1;
    }

}
