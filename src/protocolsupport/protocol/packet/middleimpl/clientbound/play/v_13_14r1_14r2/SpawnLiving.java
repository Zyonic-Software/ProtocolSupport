package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_13_14r1_14r2;

import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.PacketType;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleSpawnLiving;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.serializer.MiscSerializer;
import protocolsupport.protocol.serializer.NetworkEntityMetadataSerializer;
import protocolsupport.protocol.serializer.NetworkEntityMetadataSerializer.NetworkEntityMetadataList;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.protocol.typeremapper.entity.FlatteningEntityId;
import protocolsupport.protocol.typeremapper.utils.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupport.protocol.types.networkentity.NetworkEntityType;

public class SpawnLiving extends MiddleSpawnLiving {

	protected final ArrayBasedIdRemappingTable flatteningEntityIdTable = FlatteningEntityId.REGISTRY.getTable(version);

	public SpawnLiving(ConnectionImpl connection) {
		super(connection);
	}

	@Override
	public void writeToClient0(NetworkEntityType remappedEntityType) {
		ClientBoundPacketData spawnliving = ClientBoundPacketData.create(PacketType.CLIENTBOUND_PLAY_SPAWN_LIVING);
		VarNumberSerializer.writeVarInt(spawnliving, entity.getId());
		MiscSerializer.writeUUID(spawnliving, entity.getUUID());
		VarNumberSerializer.writeVarInt(spawnliving, flatteningEntityIdTable.getRemap(remappedEntityType.getNetworkTypeId()));
		spawnliving.writeDouble(x);
		spawnliving.writeDouble(y);
		spawnliving.writeDouble(z);
		spawnliving.writeByte(yaw);
		spawnliving.writeByte(pitch);
		spawnliving.writeByte(headPitch);
		spawnliving.writeShort(motX);
		spawnliving.writeShort(motY);
		spawnliving.writeShort(motZ);
		NetworkEntityMetadataSerializer.writeData(spawnliving, version, cache.getAttributesCache().getLocale(), NetworkEntityMetadataList.EMPTY);
		codec.write(spawnliving);
	}

}
