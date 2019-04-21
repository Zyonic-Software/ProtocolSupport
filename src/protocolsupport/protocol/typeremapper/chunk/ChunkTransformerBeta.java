package protocolsupport.protocol.typeremapper.chunk;

import java.util.Collections;
import java.util.List;

import protocolsupport.protocol.storage.netcache.TileDataCache;
import protocolsupport.protocol.typeremapper.block.BlockRemappingHelper;
import protocolsupport.protocol.typeremapper.block.PreFlatteningBlockIdData;
import protocolsupport.protocol.typeremapper.tile.TileEntityRemapper;
import protocolsupport.protocol.typeremapper.utils.RemappingTable.ArrayBasedIdRemappingTable;

public class ChunkTransformerBeta extends ChunkTransformer {

	public ChunkTransformerBeta(ArrayBasedIdRemappingTable blockDataRemappingTable, TileEntityRemapper tileRemapper, TileDataCache tileCache) {
		super(blockDataRemappingTable, tileRemapper, tileCache);
	}

	public static class ChunkUpdateData {
		protected final int sectionStart;
		protected final int sectionCount;
		protected final byte[] data;
		public ChunkUpdateData(int sectionStart, int sectionCount, byte[] data) {
			this.sectionStart = sectionStart;
			this.sectionCount = sectionCount;
			this.data = data;
		}
		public int getSectionStart() {
			return sectionStart;
		}
		public int getSectionCount() {
			return sectionCount;
		}
		public byte[] getData() {
			return data;
		}
	}

	public List<ChunkUpdateData> toLegacyData(boolean full) {
		if (full) {
			int highestSectionNumber = Math.min(8, Integer.SIZE - Integer.numberOfLeadingZeros(bitmap));
			int sizeY = highestSectionNumber << 4;

			byte[] data = new byte[10240 * highestSectionNumber];

			int blockDataIndex = 4096 * highestSectionNumber;
			int blockLightIndex = 6144 * highestSectionNumber;
			int skyLightIndex = 8192 * highestSectionNumber;

			for (int i = 0; i < highestSectionNumber; i++) {
				ChunkSection section = sections[i];
				if (section != null) {
					BlockStorageReader storage = section.blockdata;

					int betaY = i << 4;
					for (int x = 0; x < 16; x++) {
						int betaX = (x * 16 * sizeY);
						boolean blockIndexEven = (x & 1) == 0;
						for (int z = 0; z < 16; z++) {
							int betaZ = (z * sizeY);
							for (int y = 0; y < 16; y += 2) {
								int betaIndex = betaX + betaZ + betaY + y;
								int betaNibbleIndex = betaIndex >> 1;

								int blockdataIndex1 = getBlockIndex(x, y, z);
								int blockdataIndex2 = getBlockIndex(x, y + 1, z);
								int blockdata1 = BlockRemappingHelper.remapBlockDataNormal(blockDataRemappingTable, storage.getBlockData(blockdataIndex1));
								int blockdata2 = BlockRemappingHelper.remapBlockDataNormal(blockDataRemappingTable, storage.getBlockData(blockdataIndex2));
								data[betaIndex] = (byte) PreFlatteningBlockIdData.getIdFromCombinedId(blockdata1);
								data[betaIndex + 1] = (byte) PreFlatteningBlockIdData.getIdFromCombinedId(blockdata2);
								data[blockDataIndex + betaNibbleIndex] = (byte) (
									(PreFlatteningBlockIdData.getDataFromCombinedId(blockdata2) << 4) |
									PreFlatteningBlockIdData.getDataFromCombinedId(blockdata1)
								);
								data[blockLightIndex + betaNibbleIndex] = (byte) (
									(getNibbleVal(section.blocklight[blockdataIndex2 >> 1], blockIndexEven) << 4) |
									getNibbleVal(section.blocklight[blockdataIndex1 >> 1], blockIndexEven)
								);
								data[skyLightIndex + betaNibbleIndex] = (byte) (
									(getNibbleVal(section.skylight[blockdataIndex2 >> 1], blockIndexEven) << 4) |
									getNibbleVal(section.skylight[blockdataIndex1 >> 1], blockIndexEven)
								);
							}
						}
					}
				}
			}

			return Collections.singletonList(new ChunkUpdateData(0, highestSectionNumber, data));
		} else {
			//TODO
			return Collections.emptyList();
		}
	}

	protected static int getNibbleVal(int val, boolean lower) {
		return lower ? val & 0xF : val >> 4;
	}

	protected static int getBlockIndex(int x, int y, int z) {
		return (y << 8) | (z << 4) | x;
	}

}