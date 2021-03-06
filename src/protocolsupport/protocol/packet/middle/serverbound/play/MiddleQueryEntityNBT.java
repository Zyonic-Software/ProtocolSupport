package protocolsupport.protocol.packet.middle.serverbound.play;

import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.PacketType;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.packet.middleimpl.ServerBoundPacketData;
import protocolsupport.protocol.serializer.VarNumberSerializer;

public abstract class MiddleQueryEntityNBT extends ServerBoundMiddlePacket {

	public MiddleQueryEntityNBT(ConnectionImpl connection) {
		super(connection);
	}

	protected int id;
	protected int entityId;

	@Override
	public void writeToServer() {
		ServerBoundPacketData queryentitynbt = ServerBoundPacketData.create(PacketType.SERVERBOUND_PLAY_QUERY_ENTITY_NBT);
		VarNumberSerializer.writeVarInt(queryentitynbt, id);
		VarNumberSerializer.writeVarInt(queryentitynbt, entityId);
		codec.read(queryentitynbt);
	}

}
