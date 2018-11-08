package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_4_5_6_7;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.ClientBoundPacket;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleBlockChangeMulti;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.packet.middleimpl.clientbound.play.v_8_9r1_9r2_10_11_12r1_12r2_13.BlockTileUpdate;
import protocolsupport.protocol.serializer.PositionSerializer;
import protocolsupport.protocol.typeremapper.basic.TileNBTRemapper;
import protocolsupport.protocol.typeremapper.block.LegacyBlockData;
import protocolsupport.protocol.typeremapper.block.PreFlatteningBlockIdData;
import protocolsupport.protocol.typeremapper.utils.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupport.protocol.utils.types.Position;
import protocolsupport.utils.recyclable.RecyclableArrayList;
import protocolsupport.utils.recyclable.RecyclableCollection;

public class BlockChangeMulti extends MiddleBlockChangeMulti {

	public BlockChangeMulti(ConnectionImpl connection) {
		super(connection);
	}

	protected final ArrayBasedIdRemappingTable blockRemappingTable = LegacyBlockData.REGISTRY.getTable(connection.getVersion());
	protected final TileNBTRemapper tileremapper = TileNBTRemapper.getRemapper(connection.getVersion());

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		Int2IntMap tilestates = cache.getTileCache().getChunk(chunk);
		RecyclableArrayList<ClientBoundPacketData> packets = RecyclableArrayList.create();
		ClientBoundPacketData serializer = ClientBoundPacketData.create(ClientBoundPacket.PLAY_BLOCK_CHANGE_MULTI_ID);
		PositionSerializer.writeChunkCoord(serializer, chunk);
		serializer.writeShort(records.length);
		serializer.writeInt(records.length * 4);
		for (Record record : records) {
			PositionSerializer.writeLocalCoord(serializer, record.localCoord);
			serializer.writeShort(PreFlatteningBlockIdData.getCombinedId(blockRemappingTable.getRemap(record.id)));
			if (tileremapper.tileThatNeedsBlockData(record.id)) {
				tilestates.put(record.localCoord, record.id);
			}
			if (tileremapper.usedToBeTile(record.id)) {
				packets.add(BlockTileUpdate.create(
					connection, tileremapper.getLegacyTileFromBlock(Position.fromLocal(chunk, record.localCoord), record.id)
				));
			}
		}
		packets.add(0, serializer);
		return packets;
	}

}
