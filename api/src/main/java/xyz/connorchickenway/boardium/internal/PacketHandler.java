package xyz.connorchickenway.boardium.internal;

import org.bukkit.entity.Player;

public interface PacketHandler {

    Object createObjectivePacket( String objectiveName , int mode , String text );

    Object createObjectiveSlotPacket( String objectiveName );

    Object createScorePacket( String scoreName , String objectiveName , int mode , int index );

    Object createTeamPacket( String teamName , int mode , String prefix , String suffix , String scoreName );

    void sendPacket( Player player , Object packet );

    NMSVersion getNMSVersion();

}
